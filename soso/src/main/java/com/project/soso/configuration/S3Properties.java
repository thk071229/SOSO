package com.project.soso.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "spring.cloud.aws.s3")
public class S3Properties {
	
	private String bucket;

}
