package com.project.soso.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.soso.dto.AccountDto;

@Repository
public class AccountDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	// 회원가입
	public void insert(AccountDto accountDto) {
		sqlSession.insert("account.insert", accountDto);
	}
	// 회원 프로필
	public void connect(String accountId, long attachmentNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("accountId", accountId);
		params.put("attachmentNo", String.valueOf(attachmentNo));
		sqlSession.insert("account.connect", params);
	}
	public Long findAttach(String accountId) {
		return sqlSession.selectOne("account.findAttach", accountId);
	}
	// 중복검사
	public int countByAccountId(String accountId) {
		AccountDto accountDto = new AccountDto();
		accountDto.setAccountId(accountId);
		return sqlSession.selectOne("account.checkDuplicate", accountDto);
	}
	public int countByAccountNickname(String accountNickname) {
		AccountDto accountDto = new AccountDto();
		accountDto.setAccountNickname(accountNickname);
		return sqlSession.selectOne("account.checkDuplicate", accountDto);
	}
	public int countByAccountContact(String accountContact) {
		AccountDto accountDto = new AccountDto();
		accountDto.setAccountContact(accountContact);
		return sqlSession.selectOne("account.checkDuplicate", accountDto);
	}
	// 로그인 시간 업데이트
	public void updateLoginTime(String accountId) {
		sqlSession.update("account.updateLoginTime", accountId);
	}
	// 수정
	public boolean update(AccountDto accountDto) {
		return sqlSession.update("account.edit", accountDto) > 0;
	}
	// 아이디찾기
	public String findAccountId(String accountContact, String accountEmail) {
		Map<String, Object> params = new HashMap<>();
		params.put("accountContact", accountContact);
		params.put("accountEmail", accountEmail);
		return sqlSession.selectOne("account.findAccountId", params);
	}
	// 회원 상세조회
	public AccountDto selectOne(String accountId) {
		return sqlSession.selectOne("account.detail", accountId);
	}
	
}
