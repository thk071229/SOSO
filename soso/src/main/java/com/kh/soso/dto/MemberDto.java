package com.kh.soso.dto;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MemberDto {
	
	private String memberId;
	private String memberPw;
	private String memberNickname;
	private String memberEmail;
	private String memberGender;
	private Date memberBirth;
	private String memberLevel;
	private Timestamp memberJoin;
	private String memberAuthority;
	private int attachmentNo;
}
