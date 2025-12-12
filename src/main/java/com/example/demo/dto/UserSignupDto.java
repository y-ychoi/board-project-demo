package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입용 DTO
 * 사용자가 입력한 회원가입 정보를 받아오는 객체
 */
@Getter
@Setter
public class UserSignupDto {

    @NotBlank(message = "아이디는 필수입니다")  // 빈 값 또는 공백만 있으면 에러
    private String userId;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String userPw;

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")  // 이메일 형식 검증 (예: user@example.com)
    private String email;
}