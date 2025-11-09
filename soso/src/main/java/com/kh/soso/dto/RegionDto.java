package com.kh.soso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class RegionDto {

	private long regionNo;
	private String regionName;
	private String regionDepth1;
	private String regionDepth2;
	private String regionDepth3;
	private double xCoord;
	private double yCoord;
}
