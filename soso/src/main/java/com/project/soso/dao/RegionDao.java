package com.project.soso.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.soso.vo.RegionVO;

@Repository
public class RegionDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public List<RegionVO> selectList(){
		return sqlSession.selectList("region.selectList");
	}
	
	// 회원-지역 정보 등록
	public void insert(String accountId, int regionNo, String regionType) {
		Map<String, Object> params = new HashMap<>();
		params.put("accountId", accountId);
		params.put("regionNo", regionNo);
		params.put("regionType", regionType);
		sqlSession.insert("accountRegion.connect", params);
	}
	
	// 회원이 등록한 지역 등록 여부 
	public int findRegionType(String accountId, String regionType) {
		Map<String, Object> params = new HashMap<>();
		params.put("accountId", accountId);
		params.put("regionType", regionType);
		return sqlSession.selectOne("accountRegion.findRegionType", params);
	}
	
	// 회원이 등록한 지역 삭제
	public boolean delete(String accountId, String regionType) {
		Map<String, Object> params = new HashMap<>();
		params.put("accountId", accountId);
		params.put("regionType", regionType);
		return sqlSession.delete("accountRegion.delete", params) > 0;
	}

}
