package com.project.soso.vo;

import java.util.List;

import com.project.soso.dto.ClubDto;
import com.project.soso.dto.ClubMemberDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ClubDetailVO {
	
	private ClubDto clubDto;
	private List<ClubMemberDto> memberList;
	
	@Builder.Default
	private boolean isMember = false;
	@Builder.Default
	private boolean isLeader = false;
	@Builder.Default
	private boolean isManager = false;
	
}
