package com.kh.soso.controller; // controller 패키지

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.soso.service.RegionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/setup") // (아무도 모르는 임시 주소)
@RequiredArgsConstructor
public class RegionSetupController {

    private final RegionService regionService;

    /**
     * 이 URL을 브라우저에서 직접 1번만 호출하세요.
     * (예: http://localhost:8080/admin/setup/regions)
     */
    @GetMapping("/regions")
    public String setupRegions() {
        System.out.println("[Controller] Region 데이터 초기화를 시작합니다...");
        try {
            // 1단계: 기본 데이터 삽입
            String step1Result = regionService.step1_setupRegionData();
            
            // 2단계: 좌표 업데이트
            String step2Result = regionService.step2_updateCoordinates();

            return "<h1>데이터 초기화 완료</h1>" + 
                   "<p>" + step1Result + "</p>" +
                   "<p>" + step2Result + "</p>";

        } catch (Exception e) {
            e.printStackTrace();
            return "<h1>오류 발생</h1><p>" + e.getMessage() + "</p>";
        }
    }
}