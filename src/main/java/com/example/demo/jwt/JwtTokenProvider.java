package com.example.demo.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.demo.config.JwtConfig;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성, 검증, 파싱을 담당하는 핵심 클래스
 *
 * 역할:
 * 1. 로그인 성공 시 JWT 토큰 생성
 * 2. API 요청 시 토큰 유효성 검증
 * 3. 토큰에서 사용자 정보 추출
 */
@Component
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    /**
     * 생성자: JWT 설정을 주입받고 비밀키 초기화
     *
     * @param jwtConfig JWT 설정 정보 (application.yml에서 읽어온 값들)
     */
    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        // 문자열 비밀키를 암호화에 사용할 SecretKey 객체로 변환
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    /**
     * Access Token 생성
     *
     * @param authentication Spring Security 인증 객체 (로그인한 사용자 정보)
     * @return JWT Access Token 문자열
     */
    public String createAccessToken(Authentication authentication) {
        // 현재 시간
        Date now = new Date();
        // 토큰 만료 시간 = 현재시간 + 설정된 만료시간
        Date expiryDate = new Date(now.getTime() + jwtConfig.getAccessTokenExpiration());

        // 사용자의 권한들을 문자열로 변환 (예: "ROLE_ADMIN,ROLE_USER")
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // JWT 토큰 생성
        return Jwts.builder()
                .setSubject(authentication.getName())           // 토큰 주체 (사용자 ID)
                .claim("auth", authorities)                     // 사용자 권한 정보
                .setIssuedAt(now)                              // 토큰 발급 시간
                .setExpiration(expiryDate)                     // 토큰 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명 (비밀키 + 알고리즘)
                .compact();                                    // 최종 토큰 문자열 생성
    }

    /**
     * Refresh Token 생성 (Access Token보다 긴 만료시간)
     *
     * @param authentication Spring Security 인증 객체
     * @return JWT Refresh Token 문자열
     */
    public String createRefreshToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshTokenExpiration());

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 사용자 ID 추출
     *
     * @param token JWT 토큰 문자열
     * @return 사용자 ID (로그인 ID)
     */
    public String getUserIdFromToken(String token) {
        // 토큰을 파싱하여 Claims(토큰 내용) 추출
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)    // 서명 검증용 비밀키
                .build()
                .parseClaimsJws(token)       // 토큰 파싱 및 서명 검증
                .getBody();                  // 토큰 내용(Claims) 추출

        return claims.getSubject();          // Subject = 사용자 ID
    }

    /**
     * 토큰에서 권한 정보 추출
     *
     * @param token JWT 토큰 문자열
     * @return 권한 문자열 (예: "ROLE_ADMIN,ROLE_USER")
     */
    public String getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // "auth" 클레임에서 권한 정보 추출
        return claims.get("auth", String.class);
    }

    /**
     * 토큰 유효성 검증
     *
     * @param token JWT 토큰 문자열
     * @return true: 유효한 토큰, false: 무효한 토큰
     */
    public boolean validateToken(String token) {
        try {
            // 토큰 파싱 시도 - 성공하면 유효한 토큰
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;

        } catch (SecurityException | MalformedJwtException e) {
            // 잘못된 JWT 서명 또는 형식
            System.out.println("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            // 만료된 토큰
            System.out.println("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 토큰
            System.out.println("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 잘못되었습니다
            System.out.println("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}