package com.project.soso.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.soso.dto.ClubMemberDto;

@Repository
public class ClubMemberDao {

	@Autowired
	private SqlSession sqlSession;
	
	public void insert(ClubMemberDto clubMemberDto) {
		sqlSession.insert("clubMember.insert", clubMemberDto);
	}
}
