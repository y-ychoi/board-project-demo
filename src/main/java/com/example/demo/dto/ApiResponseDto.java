package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * REST API 공통 응답 형식 DTO
 *
 * 역할:
 * 1. 모든 REST API 응답을 표준화된 형식으로 통일
 * 2. 성공/실패 여부, 데이터, 메시지, 타임스탬프 포함
 * 3. 클라이언트에서 일관된 방식으로 응답 처리 가능
 *
 * @param <T> 실제 데이터의 타입 (제네릭)
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값인 필드는 JSON에서 제외
public class ApiResponseDto<T> {

    /**
     * 요청 성공 여부
     * true: 성공, false: 실패
     */
    private final boolean success;

    /**
     * 실제 응답 데이터
     * 성공 시에만 포함, 실패 시에는 null
     */
    private final T data;

    /**
     * 응답 메시지
     * 성공/실패에 대한 설명
     */
    private final String message;

    /**
     * 오류 정보 (실패 시에만 포함)
     */
    private final ErrorInfo error;

    /**
     * 응답 생성 시간
     */
    private final LocalDateTime timestamp;

    // ========================================
    // 생성자들 (private - 외부에서 직접 생성 불가)
    // ========================================

    /**
     * 성공 응답용 생성자
     */
    private ApiResponseDto(T data, String message) {
        this.success = true;
        this.data = data;
        this.message = message;
        this.error = null;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 실패 응답용 생성자
     */
    private ApiResponseDto(String message, ErrorInfo error) {
        this.success = false;
        this.data = null;
        this.message = message;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    // ========================================
    // 정적 팩토리 메서드들 (응답 객체 생성용)
    // ========================================

    /**
     * 성공 응답 생성 (데이터 + 메시지)
     *
     * @param data 응답 데이터
     * @param message 성공 메시지
     * @return 성공 응답 객체
     */
    public static <T> ApiResponseDto<T> success(T data, String message) {
        return new ApiResponseDto<>(data, message);
    }

    /**
     * 성공 응답 생성 (데이터만, 기본 메시지)
     *
     * @param data 응답 데이터
     * @return 성공 응답 객체
     */
    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(data, "요청이 성공적으로 처리되었습니다.");
    }

    /**
     * 성공 응답 생성 (메시지만, 데이터 없음)
     *
     * @param message 성공 메시지
     * @return 성공 응답 객체
     */
    public static <T> ApiResponseDto<T> success(String message) {
        return new ApiResponseDto<T>(null, message);
    }

    /**
     * 실패 응답 생성 (메시지 + 상세 오류 정보)
     *
     * @param message 오류 메시지
     * @param errorCode 오류 코드
     * @param errorDetails 오류 상세 정보
     * @return 실패 응답 객체
     */
    public static <T> ApiResponseDto<T> error(String message, String errorCode, String errorDetails) {
        ErrorInfo errorInfo = new ErrorInfo(errorCode, message, errorDetails);
        return new ApiResponseDto<>(message, errorInfo);
    }

    /**
     * 실패 응답 생성 (메시지만)
     *
     * @param message 오류 메시지
     * @return 실패 응답 객체
     */
    public static <T> ApiResponseDto<T> error(String message) {
        return error(message, "INTERNAL_ERROR", null);
    }

    /**
     * 오류 정보를 담는 내부 클래스
     */
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorInfo {
        private final String code;      // 오류 코드 (예: "INVALID_CREDENTIALS")
        private final String message;   // 오류 메시지
        private final String details;   // 오류 상세 정보

        public ErrorInfo(String code, String message, String details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }
    }
}
