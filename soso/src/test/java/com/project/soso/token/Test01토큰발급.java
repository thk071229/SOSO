package com.project.soso.token;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.soso.SosoApplication;
import com.project.soso.dao.AccountDao;
import com.project.soso.dto.AccountDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = { SosoApplication.class })
public class Test01토큰발급 {
	
	@Autowired
	private PasswordEncoder passwordEncoder; // 암호화 도구

	@Autowired
	private AccountDao accountDao; // DB 도구

	@Test
	@DisplayName("개발용 테스트 계정 생성 (인증 없이 강제 생성)")
	public void createTestAccount() {
	    // 1. 일반 유저 (User1)
	    AccountDto user = AccountDto.builder()
	            .accountId("testuser1")
	            .accountPw(passwordEncoder.encode("Testuser1!")) // [핵심] 여기서 암호화를 해줍니다!
	            .accountNickname("테스트유저1")
	            .accountBirth("2000-01-01")
	            .accountContact("01010000001") // 가짜 번호
	            .accountEmail("user1@test.com")
	            .accountGender("남")
	            .accountMarketingAgree("Y")
	            .accountThirdPartyAgree("Y")
	            .build();

	    // 2. 관리자 (Admin1)
	    AccountDto admin = AccountDto.builder()
	            .accountId("adminuser1")
	            .accountPw(passwordEncoder.encode("Adminuser1!")) // 비밀번호는 1234
	            .accountNickname("관리자1")
	            .accountBirth("1990-01-01")
	            .accountContact("01010000002")
	            .accountEmail("admin1@test.com")
	            .accountGender("여")
	            .accountMarketingAgree("Y")
	            .accountThirdPartyAgree("Y")
	            .build();

	    // 3. DB에 저장 (Service를 안 거치고 DAO로 바로 넣으면 인증 과정 생략 가능!)
	    try {
	        accountDao.insert(user);
	        log.info("user1 생성 완료!");
	    } catch (Exception e) {
	    	e.printStackTrace(); // <-- 빨간색 에러 로그를 콘솔에 출력함
	        log.error("계정 생성 실패! 이유: {}", e.getMessage());
	    }
	    
	    try {
	        accountDao.insert(admin);
	        log.info("admin1 생성 완료!");
	    } catch (Exception e) {
	    	e.printStackTrace(); // <-- 빨간색 에러 로그를 콘솔에 출력함
	        log.error("계정 생성 실패! 이유: {}", e.getMessage());
	    }
	}
}