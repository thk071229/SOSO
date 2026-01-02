package com.project.soso.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.soso.dto.AttachmentDto;

@Repository
public class AttachmentDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public Long sequence() {
		return sqlSession.selectOne("attachment.sequence");
	}
	
	public void insert(AttachmentDto attachmentDto) {
		sqlSession.insert("attachment.insert", attachmentDto);
	}
	
	public AttachmentDto selectOne(Long attachmentNo) {
		return sqlSession.selectOne("attachment.detail", attachmentNo);
	}
	
	public boolean delete(Long attachmentNo) {
		return sqlSession.delete("attachment.delete", attachmentNo) > 0;
	}
	
	public boolean update(AttachmentDto attachmentDto) {
		return sqlSession.update("attachment.edit", attachmentDto) > 0;
	}

}
