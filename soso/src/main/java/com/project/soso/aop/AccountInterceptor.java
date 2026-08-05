package com.project.soso.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.project.soso.error.UnauthorizationException;
import com.project.soso.service.TokenService;
import com.project.soso.vo.TokenVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AccountInterceptor implements HandlerInterceptor{
	
	@Autowired
	private TokenService tokenService;
	
	// 사용자가 보낸 요청의 헤더에 있는 Authorization 분석 및 판정
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		// [1] OPTIONS 요청 통과
		if(request.getMethod().equalsIgnoreCase("options")) {
			return true;
		}
		
		// [2] Authorization 헤더 검사
		try {
			String authorization = request.getHeader("Authorization");
			
			// 🚨 [수정 포인트] 헤더가 없을 때 (비회원) 처리 로직 변경
			if(authorization == null) {
				String requestURI = request.getRequestURI();
				
				// (1) 소모임 상세 페이지나 목록 조회 등 '로그인 없이도 볼 수 있는 페이지'라면?
				// 그냥 통과시킨다! (이 경우 request에 tokenVO는 없음 -> Controller에서 null이 됨)
				if(
					requestURI.startsWith("/club/detail") || 
					requestURI.startsWith("/club/list") || 
					requestURI.startsWith("/region") || 
					requestURI.startsWith("/category")) {
					return true; 
				}
				
				// (2) 그 외(개설, 마이페이지 등)는 짤없이 차단
				throw new UnauthorizationException(); 
			}
			
			// [3] 토큰이 있다면 해석 (기존 로직)
			TokenVO tokenVO = tokenService.parse(authorization);
			request.setAttribute("tokenVO", tokenVO);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			response.sendError(401); // 401 Unauthorized
			return false;
		}
	}
}
