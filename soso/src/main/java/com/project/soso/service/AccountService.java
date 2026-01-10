package com.project.soso.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.soso.dao.AccountDao;
import com.project.soso.dto.AccountDto;
import com.project.soso.error.TargetAlreadyExistsException;
import com.project.soso.error.TargetNotfoundException;
import com.project.soso.vo.AccountLoginResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountService {
	
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private TokenService tokenService;
	
	// 회원가입
	@Transactional
	public void join(AccountDto accountDto, MultipartFile attach) throws IllegalStateException, IOException {
		// [1] 아이디 중복검사
		if(accountDao.countByAccountId(accountDto.getAccountId()) > 0)
			throw new TargetAlreadyExistsException("이미 존재하는 아이디입니다");
		
		// [2] 닉네임 중복검사
		if(accountDao.countByAccountNickname(accountDto.getAccountNickname()) > 0)
			throw new TargetAlreadyExistsException("이미 존재하는 닉네임입니다");
		
		// [3] 연락처 중복검사
		if(accountDao.countByAccountContact(accountDto.getAccountContact()) > 0)
			throw new TargetAlreadyExistsException("이미 존재하는 전화번호입니다");
		
		// 비밀번호 암호화
		String encryptPassword = passwordEncoder.encode(accountDto.getAccountPw());
		accountDto.setAccountPw(encryptPassword);
		
		// 등록
		accountDao.insert(accountDto);
		
		// 회원 프로필 추가
		if(attach != null && attach.isEmpty() == false) {
			Long attachmentNo = attachmentService.save(attach);
			accountDao.connect(accountDto.getAccountId(), attachmentNo);
		}
	}
	// 아이디 중복검사
	public boolean checkAccountId(String accountId) {
		return accountDao.countByAccountId(accountId) == 0;
	}
	// 닉네임 중복검사 
	public boolean checkAccountNickname(String accountNickname) {
		return accountDao.countByAccountNickname(accountNickname) == 0;
	}
	// 로그인
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

}




