package com.project.soso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountRegionDto {
	
	private String accountId;
	private int regionNo;
	private String regionType;
	
	// Join의 결과로 담을 변수
	private String regionName;

}
