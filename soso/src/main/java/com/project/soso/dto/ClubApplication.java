package com.project.soso.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ClubApplication {
	
	// 소모임 가입 신청서
	
	private int clubAppNo;
	private int clubNo;
	private String accountId;
	private String appMessage;
	private LocalDateTime appDate;

}
