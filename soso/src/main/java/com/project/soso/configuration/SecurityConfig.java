package com.project.soso.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // [1] PasswordEncoder 등록: 이게 없으면 로그인 시 비밀번호 비교가 불가능합니다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 1. 정적 리소스 (이미지, CSS 등)
                .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.ico").permitAll()
                
                // 2. 스웨거(Swagger) 관련 주소 [여기가 핵심입니다!]
                .requestMatchers(
                    "/v3/api-docs/**",      // API 데이터
                    "/swagger-ui/**",       // 화면 구성 파일
                    "/swagger-ui.html"      // 메인 페이지
                ).permitAll()
                
                // 3. 회원가입/로그인 API
                .requestMatchers("/account/**", "/cert/**", "/api/**","/region/**").permitAll()
                
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            );
            
        return http.build();
    }
}