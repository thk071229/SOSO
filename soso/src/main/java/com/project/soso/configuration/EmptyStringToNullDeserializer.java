package com.project.soso.configuration;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

// JSON 내부에 있는 빈 문자열을 null로 치환하는 도구
// - 이 도구를 만들어서 Spring Boot가 사용하는 기본 방식으로 지정해야함(Configuration)

public class EmptyStringToNullDeserializer extends JsonDeserializer<String>{

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		// 1. 해석중인 값을 문자열로 불러온다.
		String value = p.getText();
		// 2. 원래 null이었거나 불필요한 공백을 제거하고 나니 비어있는 경우 null로 치환
		if(value == null || value.strip().isEmpty()) {
			return null;
		}
		
		// 3. 나머지 값들은 원래대로 반환
		return value;
	}

}
