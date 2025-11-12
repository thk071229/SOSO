package com.kh.soso.service; // service 패키지

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap; // ★ HashMap import
import java.util.List;
import java.util.Map; // ★ Map import

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ★ Transactional
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.soso.dao.RegionDao;
import com.kh.soso.dto.RegionDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class RegionService {

    private final RegionDao regionDao; 
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(); 

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    /**
     * [1단계] 공공데이터 파일을 읽어 DB에 INSERT 합니다.
     * (Map을 사용해 중복 PK 데이터를 제거하는 로직으로 수정)
     */
    @Transactional // ★[필수] 1단계 작업이 실패하면 모두 롤백(Rollback)합니다.
    public String step1_setupRegionData() throws Exception {
        // [안전 장치] DB에 데이터가 이미 있으면 건너뜁니다.
        if (regionDao.count() > 0) {
            System.out.println("[INFO] 1단계: REGION 기본 데이터가 이미 존재하여 건너뜁니다.");
            return "[INFO] 1단계: REGION 기본 데이터가 이미 존재하여 건너뜁니다.";
        }

        System.out.println("[INFO] 1단계: 'region_code.txt' 파일 읽기 시작...");
        
        // Map을 사용하면 'regionNo' (PK)가 중복될 경우, 자동으로 덮어쓰기(중복 제거)
        Map<Long, RegionDto> regionMap = new HashMap<>();
        
        ClassPathResource resource = new ClassPathResource("region_code.txt");
        
        int skippedCount = 0;
        int headerCount = 0;
        int duplicateCount = 0; // 중복 카운트용

        // (엑셀에서 저장한 .txt 파일은 "EUC-KR" 인코딩입니다)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), "EUC-KR"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                
                String[] parts = line.split("\t", -1); 
                
                if (parts.length < 8) { skippedCount++; continue; }
                
                String codeStr = parts[4]; // (E) 주소코드 -> PK
                String depth1 = parts[1]; // (B) 시도명
                String depth2 = parts[2]; // (C) 시군구명
                String depth3 = parts[5]; // (F) 동리명
                String abolishDate = parts[7]; // (H) 말소일자
                
                if (parts[0].equals("행정코드")) { headerCount++; continue; }
                if (depth3 == null || depth3.trim().isEmpty()) { skippedCount++; continue; }
                if (abolishDate != null && !abolishDate.trim().isEmpty()) { skippedCount++; continue; }

                try {
                    Long code = Long.parseLong(codeStr); 
                    String fullName = depth1 + " " + depth2 + " " + depth3;

                    RegionDto regionDto = RegionDto.builder()
                                        .regionNo(code)
                                        .regionName(fullName)
                                        .regionDepth1(depth1)
                                        .regionDepth2(depth2)
                                        .regionDepth3(depth3)
                                        .build();
                    
                    // ★[수정] DB에 바로 insert하지 않고 Map에 추가
                    // regionMap.put(code, regionDto)은 이전에 저장된 값이 있으면 그 값을 반환 (null이 아니면 중복)
                    if (regionMap.put(code, regionDto) != null) {
                        duplicateCount++; // 중복 횟수 카운트
                    }

                } catch (NumberFormatException e) {
                    System.out.println("[WARN] 파싱 오류 (스킵): " + line);
                    skippedCount++;
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] 1단계: 'region_code.txt' 파일 읽기 실패!");
            throw e; // 트랜잭션 롤백을 위해 예외를 던짐
        }
        
        // ★[수정] 루프가 끝난 후, Map에 담긴 '중복이 제거된' 데이터만 DB에 삽입
        System.out.println("[INFO] 1단계: 파일 읽기 완료. 총 " + regionMap.size() + "건의 고유 데이터 DB 삽입 시작...");
        System.out.println(" (파일 원본: 헤더 " + headerCount + "건, 스킵 " + skippedCount + "건, 중복 " + duplicateCount + "건)");

        for (RegionDto region : regionMap.values()) {
            // 이제 regionMap.values()에는 PK 중복이 없는 고유한 데이터만 존재
            regionDao.insert(region);
        }

        String result = "[INFO] 1단계: REGION 기본 데이터 삽입 완료. (삽입: " + regionMap.size() + "건)";
        System.out.println(result);
        return result;
    }

    /**
     * [2단계] Kakao API를 호출하여 x, y 좌표를 UPDATE 합니다.
     */
    public String step2_updateCoordinates() throws Exception {
        // [안전 장치 2] 1. 좌표가 없는(NULL) 지역 목록을 조회
        List<RegionDto> regionsToUpdate = regionDao.findByxCoordIsNull();
        
        if (regionsToUpdate.isEmpty()) {
            System.out.println("[INFO] 2단계: 모든 REGION 좌표가 이미 존재하여 건너뜁니다.");
            return "[INFO] 2단계: 모든 REGION 좌표가 이미 존재하여 건너뜁니다.";
        }

        System.out.println("[INFO] 2단계: 총 " + regionsToUpdate.size() + "건의 좌표 업데이트가 필요합니다.");
        
        String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey); 

        int updatedCount = 0;
        int failedCount = 0;

        for (RegionDto region : regionsToUpdate) {
            try {
                String query = region.getRegionName();
                String requestUrl = apiUrl + "?query=" + query;
                
                HttpEntity<String> entity = new HttpEntity<>(headers);
                
                // 2. Kakao API 호출
                ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class);
                
                // 3. JSON 응답 파싱
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode documents = root.path("documents");
                
                if (documents.isArray() && documents.size() > 0) {
                    // 4. 첫 번째 검색 결과의 x, y 좌표 추출
                    double x = documents.get(0).path("x").asDouble(); 
                    double y = documents.get(0).path("y").asDouble(); 
                    
                    region.setXCoord(x);
                    region.setYCoord(y);
                    
                    // 5. DB 업데이트
                    regionDao.updateCoordinates(region);
                    updatedCount++;

                    if (updatedCount % 100 == 0 || updatedCount == regionsToUpdate.size()) {
                        System.out.println("[SUCCESS] ("+updatedCount+"/"+regionsToUpdate.size()+") 좌표 업데이트 진행 중...");
                    }
                } else {
                    failedCount++;
                    // System.out.println("[FAIL] " + query + " 좌표 검색 결과 없음"); // 너무 길어서 생략
                }

                // 6. (★매우 중요★) API 호출 제한(Rate Limit)에 걸리지 않도록 잠시 대기
                Thread.sleep(80); 

            } catch (Exception e) {
                failedCount++;
                System.err.println("[ERROR] " + region.getRegionName() + " 좌표 업데이트 중 오류: " + e.getMessage());
            }
        }
        String result = "[INFO] 2단계: 좌표 업데이트 완료 (성공: " + updatedCount + "건, 실패: " + failedCount + "건)";
        System.out.println(result);
        return result;
    }
}