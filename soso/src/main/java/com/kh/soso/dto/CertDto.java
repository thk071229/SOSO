package com.kh.soso.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @Data @AllArgsConstructor @Builder
public class CertDto {
	private String certEmail;
	private String certNumber;
	private Timestamp certTime;
}
