package com.kh.soso.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AttachmentDto {
	private int attachmentNo;
	private String attachmentName;
	private String attachmentPath;
	private long attachmentSize;
	private String attachmentType;
	private Timestamp attachmentTime;
	
}
