package com.project.soso.restcontroller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.soso.dto.AccountDto;
import com.project.soso.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@CrossOrigin
@RestController
@RequestMapping("/account")
public class AccountRestController {
	
	@Autowired
	private AccountService accountService;
	
	@Operation(
			summary = "신규 회원 가입", // [1] 짧은 제목
			description = "사용자가 입력한 정보를 바탕으로 새로운 회원을 등록합니다.<br>"
					+ "비밀번호는 암호화되어 저장되며, <strong>아이디, 닉네임, 이메일, 연락처는 중복될 수 없습니다.</strong>", // [2] 상세 설명 (HTML 태그 사용 가능)
			responses = {
				@ApiResponse(
					responseCode = "200", 
					description = "회원 가입 성공",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)) // [3] 응답 본문이 없음을 명시
				),
				@ApiResponse(
					responseCode = "400", 
					description = "잘못된 요청 (필수 입력값 누락, 유효성 검사 실패)",
					content = @Content
				),
				@ApiResponse(
					responseCode = "409", 
					description = "중복된 데이터 존재 (아이디, 닉네임, 연락처 등)", // [4] 409 Conflict 추가 추천
					content = @Content
				),
				@ApiResponse(
					responseCode = "500", 
					description = "서버 내부 에러 (DB 연결 실패 등)",
					content = @Content
				)
			}
		)
	@PostMapping("/join")
	public void join(
			@ModelAttribute AccountDto accountDto,
			@RequestParam(required = false) MultipartFile attach) throws IllegalStateException, IOException {
		accountService.join(accountDto, attach);
	}
	// 아이디 중복검사
	@GetMapping("/accountId/{accountId}")
	public boolean checkAccountId(@PathVariable String accountId) {
		return accountService.checkAccountId(accountId);
	}
	// 닉네임 중복검사
	@GetMapping("/accountNickname/{accountNickname}")
	public boolean checkAccountNickname(@PathVariable String accountNickname) {
		return accountService.checkAccountNickname(accountNickname);
	}
}
