package com.project.soso.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer{
	
	@Autowired
	private AccountInterceptor accountInterceptor;
	@Autowired
	private TokenRenewalInterceptor tokenRenewalInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    // 1. 계정 인터셉터 (보안 검사)
	    registry.addInterceptor(accountInterceptor)
	            .addPathPatterns("/**") // 일단 다 막고 시작! (보안상 좋음)
	            .excludePathPatterns(
	                    // [필수] 로그인, 가입은 누구나 들어올 수 있어야 함
	                    "/account/login",
	                    "/account/join",
	                    "/account/findId",
	                    "/account/findPw",
	                    "/account/checkUser",
	                    "/account/resetPw",
	                    "/account/refresh", // 토큰 갱신 요청도 제외

	                    // [필수] 회원가입/검색에 필요한 공공 데이터
	                    "/region/**",
	                    "/category/**",
	                    
	                    // [필수] 이미지, 정적 리소스 (안 풀어주면 엑박 뜸)
	                    "/attachment/download",
	                    "/css/**", "/js/**", "/images/**", "/favicon.ico",

	                    // [선택] 소모임 목록이나 검색은 로그인 안 해도 보여줄 거라면
	                    "/club/list",
	                    "/club/search"
	            );
	    
	    // 2. 토큰 갱신 인터셉터 (로그인 연장)
	    registry.addInterceptor(tokenRenewalInterceptor)
	            .addPathPatterns("/**")
	            .excludePathPatterns(
	                    // 여기는 갱신이 필요 없는 곳들만 제외
	                     "/account/refresh",
	                     "/account/join",
	                     "/account/login",
	                     "/account/findId",
	                     "/account/findPw",
	                     "/account/checkUser",
	                     "/account/resetPw",
	                     "/region/**",
	                     "/category/**",
	                     "/attachment/**",
	                     "/css/**", "/js/**", "/images/**", "/favicon.ico"
	            );
	}
}
