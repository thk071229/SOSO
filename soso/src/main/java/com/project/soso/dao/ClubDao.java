package com.project.soso.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.soso.dto.ClubDto;

@Repository
public class ClubDao {

	@Autowired
	private SqlSession sqlSession;
	
	public void create(ClubDto clubDto) {
		sqlSession.insert("club.create", clubDto);
	}
}
