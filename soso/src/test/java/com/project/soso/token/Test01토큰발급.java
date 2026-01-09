package com.project.soso.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.soso.SosoApplication;
import com.project.soso.dto.AccountDto;
import com.project.soso.service.TokenService;
import com.project.soso.vo.TokenVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = { SosoApplication.class })
public class Test01토큰발급 {
	
	@Autowired
	private TokenService tokenService;
	
	@Test
	public void test() {
		
		AccountDto user = AccountDto.builder()
				.accountId("thk0411")   // 태훈님 아이디
				.accountLevel("일반")   // 등급
				.build();
		
		log.info("테스트 요청 데이터: {}", user);
		
		String accessToken = tokenService.generateAccessToken(user);
		
		log.info("--------------------------------------------------");
		log.info("발급된 JWT 토큰: {}", accessToken);
		log.info("--------------------------------------------------");
		
		assertNotNull(accessToken);
		
		String authorizationHeader = "Bearer " + accessToken;
		
		TokenVO result = tokenService.parse(authorizationHeader);
		log.info("토큰 해석 결과: 아이디={}, 등급={}", result.getLoginId(), result.getLoginLevel());
		
		// 5. 원래 넣었던 데이터와 꺼낸 데이터가 똑같은지 검증
		assertEquals(user.getAccountId(), result.getLoginId());
		assertEquals(user.getAccountLevel(), result.getLoginLevel());
		
		log.info("테스트 성공! 토큰 기능이 정상 작동합니다.");
		
	}

}
