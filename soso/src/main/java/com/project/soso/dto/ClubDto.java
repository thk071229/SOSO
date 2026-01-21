package com.project.soso.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ClubDto {
	private int clubNo;
	private String clubLeader;
	private String clubName;
	private String clubIntroduce;
	private int regionNo;
	private int categoryNo;
	private Long clubProfile; // null 허용
	private int clubMax; // 소모임 최대 인원수
	private int clubCount; // 소모임 현재 인원수
	private String clubOpen; // 가입방식(Y,N)
	private LocalDateTime clubCtime; // 생성일

}
