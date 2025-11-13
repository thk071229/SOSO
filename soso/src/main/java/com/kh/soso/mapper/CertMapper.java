package com.kh.soso.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.soso.dto.CertDto;

@Component
public class CertMapper implements RowMapper<CertDto>{

	@Override
	public CertDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return CertDto.builder()
				.certEmail(rs.getString("cert_email"))
				.certNumber(rs.getString("cert_number")) //인증번호 6자리
				.certTime(rs.getTimestamp("cert_time"))
				.build();
	}

}
