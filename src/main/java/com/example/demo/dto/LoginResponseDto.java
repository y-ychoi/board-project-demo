package com.example.demo.dto;

import com.example.demo.entity.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 로그인 응답 DTO
 *
 * 역할:
 * 1. 로그인 성공 시 클라이언트에게 보낼 정보를 담는 객체
 * 2. JWT 토큰과 사용자 기본 정보 포함
 * 3. 클라이언트가 이후 API 호출에 필요한 모든 정보 제공
 */
@Schema(description = "로그인 성공 응답 정보") 
@Getter
public class LoginResponseDto {

    /**
     * JWT Access Token
     * 클라이언트가 API 호출 시 Authorization 헤더에 포함해야 함
     */
	@Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String accessToken;

    /**
     * JWT Refresh Token
     * Access Token 만료 시 새로운 토큰 발급받을 때 사용
     */
	@Schema(description = "JWT 리프레시 토큰")
    private final String refreshToken;

    /**
     * 토큰 타입 (항상 "Bearer")
     * HTTP Authorization 헤더 형식: "Bearer {accessToken}"
     */
	@Schema(description = "토큰 타입", example = "Bearer")
    private final String tokenType = "Bearer";

    /**
     * Access Token 만료 시간 (초 단위)
     * 클라이언트가 토큰 갱신 시점을 알 수 있도록 제공
     */
	@Schema(description = "토큰 만료 시간(초)", example = "3600")
    private final long expiresIn;

    /**
     * 로그인한 사용자 기본 정보
     * 클라이언트 UI에서 사용자 정보 표시용
     */
	@Schema(description = "사용자 정보")
    private final UserInfo user;

    /**
     * 생성자: 로그인 성공 시 응답 객체 생성
     *
     * @param accessToken JWT Access Token
     * @param refreshToken JWT Refresh Token
     * @param expiresIn 토큰 만료 시간 (초)
     * @param userNo 사용자 번호
     * @param userId 사용자 ID
     * @param name 사용자 이름
     * @param email 사용자 이메일
     * @param role 사용자 권한
     */
    public LoginResponseDto(String accessToken, String refreshToken, long expiresIn,
                           Long userNo, String userId, String name, String email, Role role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = new UserInfo(userNo, userId, name, email, role);
    }

    /**
     * 사용자 정보를 담는 내부 클래스
     * 로그인 응답에 포함될 사용자 기본 정보
     */
    @Getter
    public static class UserInfo {
        private final Long userNo;      // 사용자 번호 (PK)
        private final String userId;    // 로그인 ID
        private final String name;      // 사용자 이름
        private final String email;     // 이메일
        private final Role role;        // 권한 (GUEST/ADMIN)

        public UserInfo(Long userNo, String userId, String name, String email, Role role) {
            this.userNo = userNo;
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.role = role;
        }
    }
}

