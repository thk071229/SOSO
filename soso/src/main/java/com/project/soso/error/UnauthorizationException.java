package com.project.soso.error;

//권한 부족으로 인해서 실행이 불가능한 경우 발생하는 예외
public class UnauthorizationException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public UnauthorizationException() {//@NoArgsConstructor로 대체 가능
		super();
		// TODO Auto-generated constructor stub
	}

	public UnauthorizationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
}
