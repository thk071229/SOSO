package com.kh.soso.config; // 패키지는 com.kh.soso 하위면 됩니다.

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // 이 파일이 '설정 파일'임을 Spring에게 알립니다.
public class AppConfig {

	@Bean // 이 메서드가 반환하는 객체(new RestTemplate())를 'Bean'으로 등록합니다.
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}