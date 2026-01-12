package com.project.soso.dao;

import java.util.List;

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

}
