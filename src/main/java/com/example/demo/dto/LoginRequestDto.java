package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema; 
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 요청 DTO
 *
 * 역할:
 * 1. 클라이언트에서 보내는 로그인 정보를 받는 객체
 * 2. 입력값 검증 (아이디, 비밀번호 필수)
 * 3. REST API에서 JSON 요청을 Java 객체로 변환
 */
@Schema(description = "로그인 요청 정보") 
@Getter
@Setter
public class LoginRequestDto {

    /**
     * 로그인 아이디
     * @NotBlank: null, 빈 문자열, 공백만 있는 문자열 모두 불허
     */
	@Schema(description = "사용자 아이디", example = "admin01") 
    @NotBlank(message = "아이디는 필수입니다")
    private String userId;

    /**
     * 로그인 비밀번호
     * @NotBlank: null, 빈 문자열, 공백만 있는 문자열 모두 불허
     */
	@Schema(description = "사용자 비밀번호", example = "password123")
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}