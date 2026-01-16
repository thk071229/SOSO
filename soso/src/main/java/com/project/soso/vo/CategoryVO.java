package com.project.soso.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryVO {
	
	private int categoryNo;
	private String categoryName;
	private int parentCategoryNo;
	
	// 아이콘 추가
	private String categoryIcon;
	

}
