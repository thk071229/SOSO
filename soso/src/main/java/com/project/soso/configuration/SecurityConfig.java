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
            // 1. CSRF 비활성화 (REST API/React 연동 시 필수)
            .csrf(csrf -> csrf.disable())
            
            // 2. 요청 권한 설정
            .authorizeHttpRequests(auth -> auth
                // 인증번호, 아이디 중복체크, 로그인 등은 누구나 허용
                .requestMatchers("/", "/login", "/cert/**", "/account/**").permitAll()
                // 그 외 모든 요청은 로그인 필요
                .anyRequest().authenticated()
            )

            // 3. 로그인 설정 (컨트롤러를 직접 만들지 않아도 시큐리티가 처리함)
            .formLogin(form -> form
                .loginProcessingUrl("/login")       // React에서 POST 보낼 주소
                .usernameParameter("accountId")     // ID 파라미터명
                .passwordParameter("accountPw")     // PW 파라미터명
                // 로그인 성공 시 응답 (React에 200 OK 전송)
                .successHandler((request, response, authentication) -> {
                    response.setStatus(200);
                })
                // 로그인 실패 시 응답 (401 에러 전송)
                .failureHandler((request, response, exception) -> {
                    response.setStatus(401);
                })
                .permitAll()
            );
        
        return http.build();
    }
}