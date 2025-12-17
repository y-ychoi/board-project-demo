package com.example.demo.exception;

import com.example.demo.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * REST API 전역 예외 처리 클래스
 *
 * 역할:
 * 1. REST API에서 발생하는 모든 예외를 중앙에서 처리
 * 2. 일관된 오류 응답 형식 제공
 * 3. 클라이언트가 이해하기 쉬운 오류 메시지 반환
 *
 * @RestControllerAdvice: REST API 전용 예외 처리
 * basePackages로 특정 패키지만 적용 가능
 */
@RestControllerAdvice(basePackages = "com.example.demo.controller")
public class RestExceptionHandler {

    /**
     * 입력값 검증 오류 처리 (@Valid 실패)
     *
     * @param ex MethodArgumentNotValidException
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        // 첫 번째 검증 오류 메시지 추출
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity.badRequest()
                .body(ApiResponseDto.error(
                        errorMessage,
                        "VALIDATION_ERROR",
                        "입력값을 확인해주세요."
                ));
    }

    /**
     * 폼 바인딩 오류 처리
     *
     * @param ex BindException
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleBindException(BindException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity.badRequest()
                .body(ApiResponseDto.error(
                        errorMessage,
                        "BIND_ERROR",
                        "요청 데이터 형식을 확인해주세요."
                ));
    }

    /**
     * 인증 실패 처리 (로그인 실패 등)
     *
     * @param ex AuthenticationException
     * @return 401 Unauthorized 응답
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleAuthenticationException(
            AuthenticationException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDto.error(
                        "인증에 실패했습니다.",
                        "AUTHENTICATION_FAILED",
                        "아이디 또는 비밀번호를 확인해주세요."
                ));
    }

    /**
     * 잘못된 자격증명 처리 (아이디/비밀번호 오류)
     *
     * @param ex BadCredentialsException
     * @return 401 Unauthorized 응답
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleBadCredentialsException(
            BadCredentialsException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDto.error(
                        "아이디 또는 비밀번호가 올바르지 않습니다.",
                        "INVALID_CREDENTIALS",
                        "로그인 정보를 다시 확인해주세요."
                ));
    }

    /**
     * 접근 권한 부족 처리 (403 Forbidden)
     *
     * @param ex AccessDeniedException
     * @return 403 Forbidden 응답
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleAccessDeniedException(
            AccessDeniedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponseDto.error(
                        "접근 권한이 없습니다.",
                        "ACCESS_DENIED",
                        "해당 기능을 사용할 권한이 없습니다."
                ));
    }

    /**
     * 비즈니스 로직 예외 처리 (중복 아이디 등)
     *
     * @param ex IllegalStateException
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleIllegalStateException(
            IllegalStateException ex) {

        return ResponseEntity.badRequest()
                .body(ApiResponseDto.error(
                        ex.getMessage(),
                        "BUSINESS_LOGIC_ERROR",
                        "요청을 처리할 수 없습니다."
                ));
    }

    /**
     * 잘못된 인자 예외 처리
     *
     * @param ex IllegalArgumentException
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        return ResponseEntity.badRequest()
                .body(ApiResponseDto.error(
                        ex.getMessage(),
                        "INVALID_ARGUMENT",
                        "요청 파라미터를 확인해주세요."
                ));
    }

    /**
     * 기타 모든 예외 처리 (최후의 안전망)
     *
     * @param ex Exception
     * @return 500 Internal Server Error 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Object>> handleGenericException(Exception ex) {

        // 개발 환경에서는 상세 오류 정보 로깅
        System.err.println("Unexpected error occurred: " + ex.getMessage());
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.error(
                        "서버 내부 오류가 발생했습니다.",
                        "INTERNAL_SERVER_ERROR",
                        "잠시 후 다시 시도해주세요."
                ));
    }
}