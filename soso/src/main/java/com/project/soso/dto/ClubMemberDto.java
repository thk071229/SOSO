package com.project.soso.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ClubMemberDto {
	
	private int clubNo;
	private String accountId;
	private String clubMemberRole;
	private LocalDateTime clubMemberJoin;
	
	// 테이블 조인으로 가져온 정보
	private String accountNickname;
	private Long attachmentNo;

}
