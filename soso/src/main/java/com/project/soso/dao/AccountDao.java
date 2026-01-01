package com.project.soso.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.soso.dto.AccountDto;

@Repository
public class AccountDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public void insert(AccountDto accountDto) {
		sqlSession.insert("account.insert", accountDto);
	}
	
}
