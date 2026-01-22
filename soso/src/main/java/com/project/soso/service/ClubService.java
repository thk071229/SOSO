package com.project.soso.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.soso.dao.ClubDao;
import com.project.soso.dao.ClubMemberDao;
import com.project.soso.dto.ClubDto;
import com.project.soso.dto.ClubMemberDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClubService {

	@Autowired
	private ClubDao clubDao;
	@Autowired
	private ClubMemberDao clubMemberDao;
	@Autowired 
	private AttachmentService attachmentService;
	
	@Transactional
	public void createClub(ClubDto clubDto, MultipartFile attach) throws IllegalStateException, IOException {
		// 1. 프로필 이미지 
		if(attach != null && !attach.isEmpty()) {
			Long attachmentNo = attachmentService.save(attach);
			clubDto.setClubProfile(attachmentNo);
		}
		// 1. 소모임 생성
		clubDao.create(clubDto);
		
		// 2. 소모임 생성자를 모임장으로 임명
		ClubMemberDto clubMemberDto = ClubMemberDto.builder()
				.clubNo(clubDto.getClubNo())
				.accountId(clubDto.getClubLeader())
				.clubMemberRole("모임장")
				.build();
		
		clubMemberDao.insert(clubMemberDto);
	}
	
	
}
