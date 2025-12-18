package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 요청 DTO (REST API용)
 *
 * 역할:
 * 1. REST API 회원가입 요청 데이터를 받는 객체
 * 2. JSON 형태의 회원가입 정보를 Java 객체로 변환
 * 3. 기존 UserSignupDto와 유사하지만 REST API 전용
 */
@Schema(description = "회원가입 요청 정보")
@Getter
@Setter
public class SignupRequestDto {

    /**
     * 로그인 아이디
     * 영문/숫자 3~15자 제한은 Controller에서 추가 검증
     */
    @Schema(description = "사용자 아이디", example = "user01")
    @NotBlank(message = "아이디는 필수입니다")
    private String userId;

    /**
     * 비밀번호
     * 최소 길이 등 추가 검증은 Controller에서 처리
     */
    @Schema(description = "비밀번호", example = "password123")
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    /**
     * 비밀번호 확인
     * Controller에서 password와 일치 여부 검증
     */
    @Schema(description = "비밀번호 확인", example = "password123")
    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String passwordConfirm;

    /**
     * 사용자 이름
     */
    @Schema(description = "사용자 이름", example = "홍길동")
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    /**
     * 이메일 주소
     * @Email: 이메일 형식 자동 검증
     */
    @Schema(description = "이메일 주소", example = "user@example.com")
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
}