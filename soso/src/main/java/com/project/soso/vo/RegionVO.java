package com.project.soso.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RegionVO {
	private int regionNo;
	private String regionName;
	private String regionDepth1;
	private String regionDepth2;
	private String regionCode; ///법정동 코드

}
