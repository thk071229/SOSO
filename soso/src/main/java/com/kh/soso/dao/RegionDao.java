package com.kh.soso.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.soso.dto.RegionDto;
import com.kh.soso.mapper.RegionMapper;

@Repository
public class RegionDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private RegionMapper regionMapper;
	
	
	// 처음 공공데이터 값을 넣기 위한 insert 구문
	public void insert(RegionDto regionDto) {
		String sql = "insert into region(region_no, region_name, region_depth1, region_depth2, region_depth3, x_coord, y_coord)"
				+ " values(?, ?, ?, ?, ?, null, null)";
		Object[] params = {regionDto.getRegionNo(), regionDto.getRegionName(), regionDto.getRegionDepth1(), regionDto.getRegionDepth2(), 
				regionDto.getRegionDepth3()};
		jdbcTemplate.update(sql, params);
		}
	//
	public int count() {
		String sql = "select count(*) from region";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}
	
	public boolean updateCoordinates(RegionDto regionDto) {
		String sql = "update region set x_coord = ?, y_coord = ? where region_no = ?";
		Object[] params = {regionDto.getXCoord(), regionDto.getYCoord(), regionDto.getRegionNo()};
		return jdbcTemplate.update(sql, params) > 0;
	}
	public List<RegionDto> findByxCoordIsNull(){
		String sql = "select * from region where x_coord is null";
		return jdbcTemplate.query(sql, regionMapper);
	}
	
}
