package com.kh.soso.batch;

// (우리가 만든 클래스 import)
import com.kh.soso.region.Region;
import com.kh.soso.region.RegionRepository;

// (Spring 및 Java 라이브러리 import)
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor; // [Lombok]
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
// (EUC-KR 인코딩을 위해 StandardCharsets 대신 "EUC-KR" 문자열 직접 사용)
// import java.nio.charset.StandardCharsets; 
import java.util.List;

/**
 * Spring Boot 시작 시 REGION 테이블 데이터를 초기화하는 메인 로직
 * (image_8ea202.png / image_f_06fd.png 엑셀 파일 기준)
 */
@Component
@RequiredArgsConstructor // 'final' 필드 생성자를 자동으로 만듭니다.
public class RegionDataInitializer implements CommandLineRunner {

    // (주입받을 부품들)
    private final RegionRepository regionRepository;
    private final RestTemplate restTemplate;
    
    // (objectMapper는 Spring Bean이 아니므로, 여기서 바로 초기화)
    private final ObjectMapper objectMapper = new ObjectMapper(); 

    // (application.properties의 "이름표"를 사용합니다)
    @Value("${kakao.api.key}")
    private String kakaoApiKey;
    

    /**
     * Spring Boot 애플리케이션 시작 시 이 run 메소드가 1회 실행됩니다.
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=========================================");
        System.out.println("[RegionDataInitializer] 데이터베이스 초기화 시작...");
        
        // 1단계: 법정동 코드(.txt) 파일 읽어서 DB에 삽입
        step1_InsertBaseData();
        
        // 2단계: 1단계에서 삽입된 데이터 중 좌표(x,y)가 없는 것을 찾아 API로 업데이트
        step2_UpdateCoordinates();
        
        System.out.println("[RegionDataInitializer] 모든 작업 완료.");
        System.out.println("=========================================");
    }

    /**
     * [1단계] 행정안전부 법정동 코드 .txt 파일을 읽어 DB에 대량 삽입합니다.
     * (image_8ea202.png / image_f_06fd.png 엑셀 파일 기준)
     */
    private void step1_InsertBaseData() throws Exception {
        // [안전 장치 1] DB에 데이터가 이미 있는지 확인
        if (regionRepository.count() > 0) {
            System.out.println("[INFO] 1단계: REGION 기본 데이터가 이미 존재하여 건너뜁니다.");
            return;
        }

        System.out.println("[INFO] 1단계: 'region_code.txt' 파일 읽기 시작...");
        
        // `src/main/resources/region_code.txt` 파일을 찾습니다.
        ClassPathResource resource = new ClassPathResource("region_code.txt");
        
        int insertedCount = 0;
        int skippedCount = 0;
        int headerCount = 0;

        // (엑셀에서 저장한 .txt 파일은 "EUC-KR"일 확률이 높습니다)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), "EUC-KR"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                
                // [★수정 1★] 엑셀에서 저장한 .txt는 "\t" (탭)으로 구분됩니다.
                String[] parts = line.split("\t", -1); 
                
                // [★수정 2★] 엑셀 파일은 (H) '말소일자'까지 8개 이상의 컬럼이 필요합니다.
                if (parts.length < 8) { 
                    skippedCount++;
                    continue; 
                }

                // (엑셀 컬럼 순서(image_f_06fd.png)에 맞춰서 데이터를 추출합니다)
                // (B) 시도명 (parts[1])
                String depth1 = parts[1];
                // (C) 시군구명 (parts[2])
                String depth2 = parts[2];
                // (E) 주소코드 (parts[4]) -> PK
                String codeStr = parts[4]; 
                // (F) 동리명 (parts[5]) -> depth3
                String depth3 = parts[5];
                // (H) 말소일자 (parts[7])
                String abolishDate = parts[7];

                // [필터 1: 헤더] "행정코드"라는 글자가 있으면 스킵
                if (parts[0].equals("행정코드")) {
                    headerCount++;
                    continue;
                }
                
                // [필터 2: "동" 단위] (F)동리명(parts[5])이 비어있는 데이터는 스킵
                if (depth3 == null || depth3.trim().isEmpty()) {
                    skippedCount++;
                    continue;
                }
                
                // [필터 3: "폐지"된 지역] (H)말소일자(parts[7])가 비어있지 않은 지역은 스킵
                if (abolishDate != null && !abolishDate.trim().isEmpty()) {
                    skippedCount++;
                    continue;
                }

                try {
                    // (PK) (E)주소코드(parts[4])를 Long 타입으로 변환
                    Long code = Long.parseLong(codeStr); 
                    
                    // (region_name) "시도 시군구 읍면동"으로 조합
                    String fullName = depth1 + " " + depth2 + " " + depth3;

                    // Region Entity 생성 (Lombok @Builder 사용)
                    Region region = Region.builder()
                                        .regionNo(code)
                                        .regionName(fullName)
                                        .regionDepth1(depth1)
                                        .regionDepth2(depth2)
                                        .regionDepth3(depth3)
                                        // xCoord, yCoord는 null
                                        .build();
                    
                    regionRepository.save(region);
                    insertedCount++;

                } catch (NumberFormatException e) {
                    // (숫자 변환 실패 시 - 헤더 등)
                    System.out.println("[WARN] 파싱 오류 (스킵): " + line);
                    skippedCount++;
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] 1단계: 'region_code.txt' 파일 읽기 실패!");
            System.err.println(" (1) 'src/main/resources'에 파일이 있는지 확인하세요.");
            System.err.println(" (2) 파일 인코딩(EUC-KR)이 맞는지 확인하세요. (깨지면 UTF-8로 변경 시도)");
            throw e;
        }
        System.out.println("[INFO] 1단계: REGION 기본 데이터 삽입 완료.");
        System.out.println(" (헤더: " + headerCount + "건, 스킵: " + skippedCount + "건, 삽입: " + insertedCount + "건)");
    }

    /**
     * [2단계] Kakao API를 호출하여 x, y 좌표를 업데이트합니다.
     */
    private void step2_UpdateCoordinates() throws Exception {
        // [안전 장치 2] 1. 좌표가 없는(NULL) 지역 목록을 DB에서 조회
        List<Region> regionsToUpdate = regionRepository.findByxCoordIsNull();
        
        if (regionsToUpdate.isEmpty()) {
            System.out.println("[INFO] 2단계: 모든 REGION 좌표가 이미 존재하여 건너뜁니다.");
            return;
        }

        System.out.println("[INFO] 2단계: 총 " + regionsToUpdate.size() + "건의 좌표 업데이트가 필요합니다.");
        
        // [★최종 수정★] "https://" 프로토콜과 "dapi"를 추가했습니다.
        String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey); // (Kakao API 키 설정)

        int updatedCount = 0;
        int failedCount = 0;

        for (Region region : regionsToUpdate) {
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
                    double x = documents.get(0).path("x").asDouble(); // 경도
                    double y = documents.get(0).path("y").asDouble(); // 위도
                    
                    region.setXCoord(x);
                    region.setYCoord(y);
                    
                    // 5. DB 업데이트
                    regionRepository.save(region);
                    updatedCount++;
                    // [로그 수정] 너무 많은 로그를 피하기 위해 100건마다 한 번씩만 출력
                    if (updatedCount % 100 == 0 || updatedCount == regionsToUpdate.size()) {
                        System.out.println("[SUCCESS] ("+updatedCount+"/"+regionsToUpdate.size()+") 좌표 업데이트 진행 중...");
                    }
                } else {
                    failedCount++;
                    System.out.println("[FAIL] " + query + " 좌표 검색 결과 없음");
                }

                // 6. (★매우 중요★) API 호출 제한(Rate Limit)에 걸리지 않도록 잠시 대기
                Thread.sleep(80); // (0.08초 대기 - Kakao의 초당 30회 제한을 피하기 위함)

            } catch (Exception e) {
                failedCount++;
                System.err.println("[ERROR] " + region.getRegionName() + " 좌표 업데이트 중 오류: " + e.getMessage());
            }
        }
        System.out.println("[INFO] 2단계: 좌표 업데이트 완료 (성공: " + updatedCount + "건, 실패: " + failedCount + "건)");
    }
}