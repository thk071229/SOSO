package com.project.soso.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginInfoVO {
	
	// 헤더 설정용
	
	private String accountId;
	private String accountNickname;
	private String accountLevel;
	
	private Long attachmentNo;
	

}
