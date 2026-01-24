package com.project.soso.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.soso.dao.ClubDao;
import com.project.soso.dao.ClubMemberDao;
import com.project.soso.dto.ClubDto;
import com.project.soso.dto.ClubMemberDto;
import com.project.soso.error.TargetNotfoundException;
import com.project.soso.vo.ClubDetailVO;

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
	
	// 소모임 상세
	@Transactional(readOnly = true)
	public ClubDetailVO getClubDetail(int clubNo, String loginId) {
		
		// 1. 소모임 정보
		ClubDto clubDto = clubDao.selectOne(clubNo);
		if(clubDto == null) throw new TargetNotfoundException("존재하지 않는 소모임");
		
		// 2. 소모임 회원 목록
		List<ClubMemberDto> memberList = clubMemberDao.memberList(clubNo);
		
		// 3. 권한 체크
		boolean isMember = false;
		boolean isLeader = false;
		boolean isManager = false;
		
		// 3. 회원의 역할 확인
		if(loginId != null) {
			for(ClubMemberDto member : memberList) {
				// 멤버 리스트에 내 아이디가 있다면 가입자
				if(member.getAccountId().equals(loginId)) {
					isMember = true;
					
					// 가입자의 신분
					if("모임장".equals(member.getClubMemberRole())) {
						isLeader = true;
					}
					else if("임원".equals(member.getClubMemberRole())) {
						isManager = true;
					}
					break;
				}
			}
		}
		
		return ClubDetailVO.builder()
				.clubDto(clubDto)
				.memberList(memberList)
				.isMember(isMember)
				.isLeader(isLeader)
				.isManager(isManager)
				.build();
	}
	
	
}
