package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰 관련 설정값을 관리하는 클래스
 *
 * 역할:
 * 1. application.yml에서 JWT 설정값 읽어오기
 * 2. 토큰 만료시간, 비밀키 등 중앙 관리
 * 3. 설정값 변경 시 코드 수정 없이 yml 파일만 수정하면 됨
 */
@Component
@ConfigurationProperties(prefix = "jwt")  // application.yml의 jwt.* 설정값을 자동 매핑
public class JwtConfig {

    /**
     * JWT 토큰 서명에 사용할 비밀키
     * 보안상 중요하므로 복잡한 문자열 사용 필요
     */
    private String secret = "mySecretKey123456789012345678901234567890";  // 기본값 (32자 이상)

    /**
     * Access Token 만료 시간 (밀리초)
     * 기본값: 1시간 (3600초 = 3600000밀리초)
     */
    private long accessTokenExpiration = 3600000;  // 1시간

    /**
     * Refresh Token 만료 시간 (밀리초)
     * 기본값: 7일 (604800초 = 604800000밀리초)
     */
    private long refreshTokenExpiration = 604800000;  // 7일

    // ========================================
    // Getter/Setter 메서드들
    // Spring이 application.yml 값을 자동으로 주입하기 위해 필요
    // ========================================

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(long refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
}