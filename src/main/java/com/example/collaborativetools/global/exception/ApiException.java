package com.example.collaborativetools.global.exception;


import com.example.collaborativetools.global.constant.ErrorCode;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException{
	private final ErrorCode errorCode;
	private final String message;

	public ApiException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.message = errorCode.getMessage();
	}
}
