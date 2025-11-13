package com.kh.soso.dao;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.soso.dto.AttachmentDto;
import com.kh.soso.mapper.AttachmentMapper;

@Repository
public class AttachmentDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private AttachmentMapper attachmentMapper;
	

	// 시퀀스 생성
	public int sequence() {
		String sql = "select attachment_seq.nextval from dual";
		return jdbcTemplate.queryForObject(sql, int.class);
	}
	
	// 파일 등록
	public void insert(AttachmentDto attachmentDto) {
		String sql = "insert into attachment(attachment_no, attachment_name, attachment_path, "
				+ "attachment_size, attachment_type) values(?, ?, ?, ?, ?)";
		Object[] params = {attachmentDto.getAttachmentNo(), attachmentDto.getAttachmentName(), 
				attachmentDto.getAttachmentPath(), attachmentDto.getAttachmentSize(), attachmentDto.getAttachmentType()};
		jdbcTemplate.update(sql, params);
	}
	
	// 파일 조회
	public AttachmentDto selectOne(int attachmentNo) {
		String sql = "select * from attachment where attachment_no = ?";
		Object[] params = {attachmentNo};
		List<AttachmentDto> list = jdbcTemplate.query(sql, attachmentMapper, params);
		return list.isEmpty() ? null : list.get(0);
	}
	
	// 파일 삭제
	public boolean delete(int attachmentNo) {
		String sql = "delete attachment where attachment_no = ?";
		Object[] params = {attachmentNo};
		return jdbcTemplate.update(sql, params) > 0;
	}
}
