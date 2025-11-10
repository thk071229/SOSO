package com.kh.soso.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List; // List import

import org.springframework.jdbc.core.BatchPreparedStatementSetter; // Batch import
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.soso.dto.RegionDto;
import com.kh.soso.mapper.RegionMapper;

import lombok.RequiredArgsConstructor; // Lombok import

@Repository
@RequiredArgsConstructor // ★ @Autowired 대신 생성자 주입 방식 사용
public class RegionDao {

    // ★ final 키워드 추가
	private final JdbcTemplate jdbcTemplate;
	private final RegionMapper regionMapper;
	
	
	// [수정 안함] 단일 삽입용 (필요시 사용)
	public void insert(RegionDto regionDto) {
		String sql = "insert into region(region_no, region_name, region_depth1, region_depth2, region_depth3, x_coord, y_coord)"
				+ " values(?, ?, ?, ?, ?, null, null)";
		Object[] params = {regionDto.getRegionNo(), regionDto.getRegionName(), regionDto.getRegionDepth1(), regionDto.getRegionDepth2(), 
				regionDto.getRegionDepth3()};
		jdbcTemplate.update(sql, params);
	}

    /**
     * [★필수 추가★] 1단계를 위한 배치(Batch) INSERT
     * @param regionDtoList
     */
    public void batchInsert(List<RegionDto> regionDtoList) {
        String sql = "insert into region(region_no, region_name, region_depth1, " +
                     "region_depth2, region_depth3, x_coord, y_coord) " +
                     "values(?, ?, ?, ?, ?, null, null)"; // 좌표는 2단계에서 채움

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                // 리스트에서 i번째 RegionDto 객체를 가져옵니다.
                RegionDto region = regionDtoList.get(i);
                
                // SQL의 ? 순서대로 값을 설정합니다.
                ps.setLong(1, region.getRegionNo());
                ps.setString(2, region.getRegionName());
                ps.setString(3, region.getRegionDepth1());
                ps.setString(4, region.getRegionDepth2());
                ps.setString(5, region.getRegionDepth3());
            }

            @Override
            public int getBatchSize() {
                // 리스트의 전체 크기(데이터 총 개수)를 반환합니다.
                return regionDtoList.size();
            }
        });
    }
	
	// [수정 안함]
	public int count() {
		String sql = "select count(*) from region";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}
	
	// [수정 안함]
	public boolean updateCoordinates(RegionDto regionDto) {
		String sql = "update region set x_coord = ?, y_coord = ? where region_no = ?";
		Object[] params = {regionDto.getXCoord(), regionDto.getYCoord(), regionDto.getRegionNo()};
		return jdbcTemplate.update(sql, params) > 0;
	}

	// [수정 안함]
	public List<RegionDto> findByxCoordIsNull(){
		String sql = "select * from region where x_coord is null";
		return jdbcTemplate.query(sql, regionMapper);
	}
	
}