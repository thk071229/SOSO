package com.project.soso.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.soso.vo.CategoryVO;

@Repository
public class CategoryDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	// 전체 카테고리 조회
	public List<CategoryVO> selectList(){
		return sqlSession.selectList("category.selectList");
	}
	
	// 회원-카테고리 정보 등록
	public void insert(String accountId, int categoryNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("accountId", accountId);
		params.put("categoryNo", categoryNo);
		sqlSession.insert("accountCategory.connect", params);
	}
	
	public boolean delete(String accountId, String categoryNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("accountId", accountId);
		params.put("categoryNo", categoryNo);
		return sqlSession.delete("accountCategory.delete", params) > 0;
	}

}
