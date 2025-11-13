package com.kh.soso.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kh.soso.dto.MemberDto;

@Component
public class MemberMapper implements RowMapper<MemberDto>{

	@Override
	public MemberDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		return MemberDto.builder()
				.memberId(rs.getString("member_id"))
				.memberPw(rs.getString("member_pw"))
				.memberNickname(rs.getString("member_nickname"))
				.memberBirth(rs.getDate("member_birth"))
				.memberEmail(rs.getString("member_email"))
				.memberGender(rs.getString("member_gender"))
				.memberAuthority(rs.getString("member_authority"))
				.memberLevel(rs.getString("member_level"))
				.memberJoin(rs.getTimestamp("member_join"))
				.attachmentNo(rs.getInt("attachment_no"))
				.build();
	}

}
