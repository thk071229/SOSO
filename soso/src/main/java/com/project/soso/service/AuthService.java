package com.project.soso.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.soso.dao.AccountDao;
import com.project.soso.dao.RefreshTokenDao;
import com.project.soso.dto.AccountDto;
import com.project.soso.error.TargetNotfoundException;
import com.project.soso.error.UnauthorizationException;
import com.project.soso.vo.AccountLoginResponseVO;
import com.project.soso.vo.TokenVO;

// 인증 관련 서비스
@Service
public class AuthService {
	
	@Autowired
	private TokenService tokenService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private RefreshTokenDao refreshTokenDao;
	
	@Transactional
	public AccountLoginResponseVO refresh(String refreshToken) {
		// 1. 기본 유효성 검사
		if(refreshToken == null || refreshToken.isEmpty()) {
			throw new UnauthorizationException();
		}
		
		// 2. 토큰 해석
		TokenVO tokenVO = tokenService.parse(refreshToken);
		
		// 3. DB 검증
		boolean isValid = tokenService.checkRefreshToken(tokenVO, refreshToken);
		
		if(!isValid) {
			throw new TargetNotfoundException();
		}
		
		// 4. 결과 반환
		return AccountLoginResponseVO.builder()
				.loginId(tokenVO.getLoginId())
				.loginLevel(tokenVO.getLoginLevel())
				.accessToken(tokenService.generateAccessToken(tokenVO))
				.refreshToken(tokenService.generateRefreshToken(tokenVO))
				.build();
	}
	
	// 로그인
	@Transactional
	public AccountLoginResponseVO login(AccountDto accountDto) {
		AccountDto findDto = accountDao.selectOne(accountDto.getAccountId());
		if(findDto == null) throw new TargetNotfoundException("아이디가 존재하지 않습니다");
		
		// 비밀번호 검사
		boolean isValid = passwordEncoder.matches(accountDto.getAccountPw(), findDto.getAccountPw());
		if(!isValid) throw new TargetNotfoundException("비밀번호 불일치");
		
		// 로그인 시간 업데이트
		accountDao.updateLoginTime(accountDto.getAccountId());
		
		// 로그인 성공
		return AccountLoginResponseVO.builder()
					.loginId(findDto.getAccountId())
					.loginLevel(findDto.getAccountLevel())
					.accessToken(tokenService.generateAccessToken(findDto))
					.refreshToken(tokenService.generateRefreshToken(findDto))
				.build();
	}
	// 로그아웃
	@Transactional
	public void logout(String loginId) {
		refreshTokenDao.deleteByTarget(loginId);
	}

}
