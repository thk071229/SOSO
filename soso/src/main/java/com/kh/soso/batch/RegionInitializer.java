package com.kh.soso.batch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

// (Spring 및 Java 라이브러리 import)
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.soso.dao.RegionDao;
// (우리가 만든 클래스 import)
import com.kh.soso.dto.RegionDto;

import lombok.RequiredArgsConstructor;

/**
 * Spring Boot 시작 시 REGION 테이블 데이터를 초기화하는 메인 로직 (JdbcTemplate 통일)
 */
@Component
@RequiredArgsConstructor 
public class RegionInitializer implements CommandLineRunner {

    // (JdbcTemplate 기반의 RegionDao를 주입받습니다.)
    private final RegionDao regionDao; 
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(); 

    @Value("${kakao.api.key}")
    private String kakaoApiKey;
    

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=========================================");
        System.out.println("[RegionInitializer] 데이터베이스 초기화 시작 (JdbcTemplate)...");
        
        // 1단계: 엑셀 파일을 읽어 DB에 INSERT 합니다. (배치)
        step1_InsertBaseData();
        
        // 2단계: 좌표가 없는 데이터를 찾아 Kakao API로 UPDATE 합니다.
        step2_UpdateCoordinates();
        
        System.out.println("[RegionInitializer] 모든 작업 완료.");
        System.out.println("=========================================");
    }

    /**
     * [1단계] 공공데이터 파일을 읽어 JdbcTemplate으로 INSERT 합니다.
     */
    private void step1_InsertBaseData() throws Exception {
        // [안전 장치] DB에 데이터가 이미 있으면 건너뜁니다.
        if (regionDao.count() > 0) {
            System.out.println("[INFO] 1단계: REGION 기본 데이터가 이미 존재하여 건너뜁니다.");
            return;
        }

        System.out.println("[INFO] 1단계: 'region_code.txt' 파일 읽기 시작...");
        
        ClassPathResource resource = new ClassPathResource("region_code.txt");
        
        int insertedCount = 0;
        int skippedCount = 0;
        int headerCount = 0;

        // (엑셀에서 저장한 .txt 파일은 "EUC-KR" 인코딩입니다)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), "EUC-KR"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                
                // [★최종 수정★] 엑셀에서 저장한 .txt는 "\t" (탭)으로 구분됩니다.
                String[] parts = line.split("\t", -1); 
                
                // [필터 1: 컬럼 수]
                if (parts.length < 8) { 
                    skippedCount++;
                    continue; 
                }
                
                // (엑셀 컬럼 순서(image_8f06fd.png)에 맞춰서 데이터를 추출합니다)
                String codeStr = parts[4]; // (E) 주소코드 -> PK
                String depth1 = parts[1]; // (B) 시도명
                String depth2 = parts[2]; // (C) 시군구명
                String depth3 = parts[5]; // (F) 동리명
                String abolishDate = parts[7]; // (H) 말소일자
                
                // [필터 2: 헤더] "행정코드"라는 글자가 있으면 스킵 (엑셀 헤더)
                if (parts[0].equals("행정코드")) {
                    headerCount++;
                    continue;
                }
                
                // [필터 3: "동" 단위] 동리명(parts[5])이 비어있는 데이터는 스킵
                if (depth3 == null || depth3.trim().isEmpty()) {
                    skippedCount++;
                    continue;
                }
                
                // [필터 4: "폐지"된 지역] 말소일자(parts[7])가 비어있지 않은(폐지된) 지역은 스킵
                if (abolishDate != null && !abolishDate.trim().isEmpty()) {
                    skippedCount++;
                    continue;
                }

                try {
                    // (PK) 주소코드(parts[4])를 Long 타입으로 변환
                    Long code = Long.parseLong(codeStr); 
                    
                    // (region_name) "시도 시군구 읍면동"으로 조합
                    String fullName = depth1 + " " + depth2 + " " + depth3;

                    // RegionDto 생성 (JdbcTemplate용 VO)
                    RegionDto regionDto = RegionDto.builder()
                                        .regionNo(code)
                                        .regionName(fullName)
                                        .regionDepth1(depth1)
                                        .regionDepth2(depth2)
                                        .regionDepth3(depth3)
                                        .build(); // xCoord, yCoord는 NULL
                    
                    // JdbcTemplate을 사용하는 DAO의 insert 메소드 호출
                    regionDao.insert(regionDto); 
                    insertedCount++;

                } catch (NumberFormatException e) {
                    // (숫자 변환 실패 시)
                    System.out.println("[WARN] 파싱 오류 (스킵): " + line);
                    skippedCount++;
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] 1단계: 'region_code.txt' 파일 읽기 실패!");
            System.err.println(" (1) 'src/main/resources'에 파일이 있는지 확인하세요.");
            System.err.println(" (2) 파일 인코딩(EUC-KR)이 맞는지 확인하세요.");
            throw e;
        }
        System.out.println("[INFO] 1단계: REGION 기본 데이터 삽입 완료.");
        System.out.println(" (헤더: " + headerCount + "건, 스킵: " + skippedCount + "건, 삽입: " + insertedCount + "건)");
    }

    /**
     * [2단계] Kakao API를 호출하여 x, y 좌표를 UPDATE 합니다.
     */
    private void step2_UpdateCoordinates() throws Exception {
        // [안전 장치 2] 1. 좌표가 없는(NULL) 지역 목록을 조회
        List<RegionDto> regionsToUpdate = regionDao.findByxCoordIsNull();
        
        if (regionsToUpdate.isEmpty()) {
            System.out.println("[INFO] 2단계: 모든 REGION 좌표가 이미 존재하여 건너뜁니다.");
            return;
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
                    
                    // 5. DB 업데이트 (JdbcTemplate 사용)
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
        System.out.println("[INFO] 2단계: 좌표 업데이트 완료 (성공: " + updatedCount + "건, 실패: " + failedCount + "건)");
    }
}