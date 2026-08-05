package com.project.soso.restcontroller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.soso.dto.AccountDto;
import com.project.soso.service.AccountService;
import com.project.soso.service.AuthService;
import com.project.soso.vo.AccountLoginResponseVO;
import com.project.soso.vo.AccountRefreshVO;
import com.project.soso.vo.LoginInfoVO;
import com.project.soso.vo.TokenVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@CrossOrigin
@RestController
@RequestMapping("/account")
public class AccountRestController {
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private AuthService authService;
	
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
	public ResponseEntity<Map<String, Object>> join(
			@ModelAttribute AccountDto accountDto,
			@RequestParam(required = false) MultipartFile attach) throws IllegalStateException, IOException {
		String accessToken = accountService.join(accountDto, attach);
		
		Map<String, Object> response = new HashMap<>();
		response.put("message", "가입 성공");
		response.put("token", accessToken);
		
		return ResponseEntity.ok(response);
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
	@Operation(
			summary = "로그인 (토큰 발급)", 
			description = "회원의 아이디와 비밀번호를 검증하여 <strong>Access Token</strong>과 <strong>Refresh Token</strong>을 발급합니다.<br>"
					+ "로그인 성공 시 반환되는 <code>accessToken</code>을 복사하여, 우측 상단 <strong>[Authorize]</strong> 버튼에 등록하면 인증된 상태로 다른 API를 테스트할 수 있습니다.",
			responses = {
				@ApiResponse(
					responseCode = "200", 
					description = "로그인 성공",
					content = @Content(
						mediaType = "application/json", 
						schema = @Schema(implementation = AccountLoginResponseVO.class)
					)
				),
				@ApiResponse(
					responseCode = "404", 
					description = "로그인 실패 (아이디가 없거나 비밀번호가 일치하지 않음)",
					content = @Content(
						mediaType = "text/plain",
						examples = @ExampleObject(value = "로그인 정보 오류")
					)
				),
				@ApiResponse(
					responseCode = "500", 
					description = "서버 내부 오류",
					content = @Content
				)
			}
		)
	@PostMapping("/login")
	public AccountLoginResponseVO login(@RequestBody AccountDto accountDto) {
		return authService.login(accountDto);
	}
	
	// 로그아웃
	@DeleteMapping("/logout")
	public void logout(@RequestAttribute TokenVO tokenVO) {
		authService.logout(tokenVO.getLoginId());
	}
	
	// 토큰갱신
	@PostMapping("/refresh")
	public AccountLoginResponseVO refresh(@RequestBody AccountRefreshVO accountRefreshVO) {
		return authService.refresh(accountRefreshVO.getRefreshToken());
	}
	
	// 헤더 설정 로그인정보
	@GetMapping("/profile")
	public LoginInfoVO profile(
			@RequestAttribute TokenVO tokenVO) {
		return accountService.loginInfo(tokenVO.getLoginId());
	}
	
}
