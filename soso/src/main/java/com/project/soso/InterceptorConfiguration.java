package com.project.soso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.project.soso.aop.AccountInterceptor;
import com.project.soso.aop.TokenRenewalInterceptor;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer{
	
	@Autowired
	private AccountInterceptor accountInterceptor;
	@Autowired
	private TokenRenewalInterceptor tokenRenewalInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(accountInterceptor)
				.addPathPatterns(
						"/account/logout",
						"/account/mypage",
						"/account/edit",
						"/account/withdraw",
						"/account/profile"
						)
				.excludePathPatterns();
		registry.addInterceptor(tokenRenewalInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns(
						 "/account/refresh",
                         "/account/join",
                         "/account/login",
                         "/account/logout"
						);
		
	}
}
