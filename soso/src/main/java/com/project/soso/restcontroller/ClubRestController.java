package com.project.soso.restcontroller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.soso.dto.ClubDto;
import com.project.soso.service.ClubService;
import com.project.soso.vo.ClubDetailVO;
import com.project.soso.vo.TokenVO;

@CrossOrigin
@RestController
@RequestMapping("/club")
public class ClubRestController {

	@Autowired
	private ClubService clubService;
	
	@PostMapping("/create")
	public int create(
			@RequestAttribute TokenVO tokenVO,
			@ModelAttribute ClubDto clubDto,
			@RequestParam(required = false) MultipartFile attach) throws IllegalStateException, IOException {
		
		String accountId = tokenVO.getLoginId();
		
		clubDto.setClubLeader(accountId);
		
		clubService.createClub(clubDto, attach);
		
		// 프론트에 보낼 소모임 번호
		return clubDto.getClubNo();
	}
	
	// 소모임 페이지
	@GetMapping("/detail/{clubNo}")
	public ClubDetailVO detail(
			@PathVariable int clubNo,
			@RequestAttribute(required = false) TokenVO tokenVO) {
		
		String loginId = (tokenVO == null) ? null : tokenVO.getLoginId();
		
		return clubService.getClubDetail(clubNo, loginId);
	}
}
