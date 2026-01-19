package com.project.soso.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.soso.configuration.S3Properties;
import com.project.soso.dao.AttachmentDao;
import com.project.soso.dto.AttachmentDto;
import com.project.soso.error.TargetNotfoundException;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AttachmentService {
	
	@Autowired
	private AttachmentDao attachmentDao;
	// Aws S3 도구
	@Autowired
	private S3Template s3Template;
	// Bean으로 등록한 S3 bucket
	@Autowired
	private S3Properties s3Properties;
	
	@Transactional
	public Long save(MultipartFile attach) throws IllegalStateException, IOException {
		// 1. 시퀀스 번호 생성
		Long attachmentNo = attachmentDao.sequence();
		
		// 2. 로컬 파일 대신 S3에 저장
		String filename = String.valueOf(attachmentNo);
		
		// 3. S3 업로드
		try(InputStream inputStream = attach.getInputStream()){
			s3Template.upload(s3Properties.getBucket(), filename, inputStream);
		}
		
		// 4. DB에 저장된 파일의 정보를 기록
		AttachmentDto attachmentDto = AttachmentDto.builder()
					.attachmentNo(attachmentNo)
					.attachmentName(attach.getOriginalFilename())
					.attachmentType(attach.getContentType())
					.attachmentSize(attach.getSize())
				.build();
		attachmentDao.insert(attachmentDto);
		
		return attachmentNo;//생성한 파일의 번호를 반환
	}
	
	@Transactional
	public ByteArrayResource load(Long attachmentNo) throws IOException {
		// 파일 탐색
		String filename = String.valueOf(attachmentNo);
		
		// S3에서 파일 가져오기
		S3Resource resource = s3Template.download(s3Properties.getBucket(), filename);
		
		if(!resource.exists()) {// 파일이 존재하지 않으면
			throw new TargetNotfoundException();
		}
		
		// 바이트 배열로 변환해서 반환
		return new ByteArrayResource(resource.getContentAsByteArray());
	}
	
	public void delete(Long attachmentNo) {
		AttachmentDto attachmentDto = attachmentDao.selectOne(attachmentNo);
		if(attachmentDto == null) throw new TargetNotfoundException();
		
		// S3 파일 삭제
		String filename= String.valueOf(attachmentNo);
		s3Template.deleteObject(s3Properties.getBucket(), filename);
		
		// DB 정보 삭제
		attachmentDao.delete(attachmentNo);
		
	}

}
