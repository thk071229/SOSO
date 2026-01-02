package com.project.soso.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttachmentDto {

	private Long attachmentNo;
	private String attachmentName;
	private Long attachmentSize;
	private String attachmentType;
	private LocalDateTime attachmentTime;
	
}
