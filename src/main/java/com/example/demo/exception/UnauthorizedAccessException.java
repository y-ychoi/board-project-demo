package com.example.demo.exception;

/**
 * 권한이 없는 접근(수정/삭제 등) 시 발생하는 예외입니다.
 * RuntimeException을 상속받아 Unchecked Exception으로 처리됩니다.
 */
public class UnauthorizedAccessException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    
    // 필요하다면 cause를 받는 생성자도 추가할 수 있습니다.
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}