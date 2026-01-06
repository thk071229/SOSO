package com.project.soso.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.soso.dao.AccountDao;
import com.project.soso.dto.AccountDto;
import com.project.soso.dto.CertDto;
import com.project.soso.error.TargetAlreadyExistsException;
import com.project.soso.error.TargetNotfoundException;
import com.project.soso.service.CertService;

import io.swagger.v3.oas.annotations.Operation;

@CrossOrigin
@RestController
@RequestMapping("/cert")
public class CertRestController {
	
	@Autowired
	private CertService certService;
	@Autowired
	private AccountDao accountDao;
	
	@Operation(summary = "휴대폰 인증번호 발송")
	@PostMapping("/sendPhone")
	public void sendPhone(@RequestParam String phone) {
		// 발송 전 중복 검사 방지
		int count = accountDao.countByAccountContact(phone);
		if(count > 0) throw new TargetAlreadyExistsException("이미 가입된 전화번호입니다");
		certService.sendCertPhone(phone);
	}
	
	@Operation(summary = "이메일 인증번호 발송")
	@PostMapping("/sendEmail")
	public void sendEmail(@RequestParam String email) {
		certService.sendCertEmail(email);
	}
	
	@Operation(summary = "휴대폰 인증번호 확인")
	@PostMapping("/check")
	public boolean checkCert(@RequestBody CertDto certDto) {
		return certService.checkCertNumber(certDto.getCertTarget(), certDto.getCertNumber());
	}
	
	
	// 아이디 찾기용 인증번호 발송
	@Operation(summary = "아이디 찾기용 휴대폰 인증번호 발송")
	@PostMapping("/sendPhoneForFind")
	public void sendPhoneForFind(@RequestParam String phone) {
	    // 1. 가입된 번호인지 확인
	    int count = accountDao.countByAccountContact(phone);
	    
	    // 2. 가입되지 않았다면 예외 발생 
	    if(count == 0) throw new TargetNotfoundException("가입되지 않은 전화번호입니다");
	    
	    // 3. 인증번호 발송
	    certService.sendCertPhone(phone);
	}
	// 아이디 찾기용 인증번호 발송
	@Operation(summary = "아이디 찾기용 이메일 인증번호 발송")
	@PostMapping("/sendEmailForFind")
	public void sendEmailForFind(@RequestParam String email) {
	    // 1. 가입된 번호인지 확인
	    int count = accountDao.countByAccountEmail(email);
	    
	    if(count == 0) throw new TargetNotfoundException("가입되지 않은 이메일입니다");
	    
	    // 3. 인증번호 발송
	    certService.sendCertEmail(email);
	}
	// 비밀번호 찾기 시 아이디와 연락처 검사 
	@Operation(summary = "비밀번호 찾기용 이메일 인증번호 발송")
	@PostMapping("/sendEmailForFindPw")
	public void sendEmailForFindPw(@RequestParam String email, @RequestParam String accountId) {
		// 1. 아이디로 조회
		AccountDto findDto = accountDao.selectOne(accountId);
		if(findDto == null) throw new TargetNotfoundException("존재하지 않는 회원");
		
		// 2. 조회한 아이디와 입력한 이메일이 일치하는 지 검사
		boolean isValid = email.equals(findDto.getAccountEmail());
		if(!isValid) throw new TargetNotfoundException("회원 정보 불일치");	 
		certService.sendCertEmail(email);
	}
	@Operation(summary = "비밀번호 찾기용 이메일 인증번호 발송")
	@PostMapping("/sendPhoneForFindPw")
	public void sendPhoneForFindPw(@RequestParam String phone, @RequestParam String accountId) {
		// 1. 아이디로 조회
		AccountDto findDto = accountDao.selectOne(accountId);
		if(findDto == null) throw new TargetNotfoundException("존재하지 않는 회원");
		
		// 2. 조회한 아이디와 입력한 이메일이 일치하는 지 검사
		boolean isValid = phone.equals(findDto.getAccountContact());
		if(!isValid) throw new TargetNotfoundException("회원 정보 불일치");	 
		certService.sendCertPhone(phone);
	}
}
