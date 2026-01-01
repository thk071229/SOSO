package com.project.soso.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// [@Configuration]
// : 스프링에게 "이 클래스는 설정 파일(Configuration)이야"라고 알려주는 태그입니다.
// : 서버가 켜질 때 이 파일을 읽어서 보안 설정을 적용합니다.
@Configuration
public class SecurityConfig {
	
	// [@Bean]
	// : 이 메서드가 리턴하는 결과(SecurityFilterChain)를 스프링이 관리하는 객체(Bean)로 등록합니다.
	// : 이제부터 스프링 시큐리티는 이 필터 체인의 규칙대로 동작하게 됩니다.
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		
		http
			// 1. CSRF 보호 기능 비활성화
			// - CSRF(Cross-Site Request Forgery)는 해커가 사용자의 권한을 도용해서 요청을 보내는 공격입니다.
			// - 보통 세션 기반의 웹사이트(JSP 등)에서는 필수지만, 
			// - REST API(React 연동) 방식이나 개발 초기 단계에서는 테스트를 위해 꺼두는(disable) 경우가 많습니다.
			.csrf(csrf -> csrf.disable())
			
			// 2. HTTP 요청에 대한 접근 권한 설정
			// - "누가 어느 URL에 들어갈 수 있는지" 정하는 곳입니다.
			.authorizeHttpRequests(auth -> auth
				// .anyRequest() : 서버로 들어오는 '모든' 요청에 대해서
				// .permitAll()  : 누구든지(로그인 안 해도) 다 허용하겠다.
				// (나중에는 .authenticated()로 바꿔서 로그인한 사람만 쓰게 막아야 합니다)
				.anyRequest().permitAll()
			);
		
		// 위에서 설정한 규칙들을 조립(build)해서 보안 필터 체인을 완성하여 반환
		return http.build();
	}

}