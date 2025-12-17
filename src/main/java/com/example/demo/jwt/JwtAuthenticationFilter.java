package com.example.demo.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 토큰 인증 필터
 *
 * 역할:
 * 1. HTTP 요청에서 JWT 토큰 추출
 * 2. 토큰 유효성 검증
 * 3. 유효한 토큰이면 Spring Security Context에 인증 정보 설정
 *
 * OncePerRequestFilter: 요청당 한 번만 실행되는 필터
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 생성자: JWT 토큰 처리 클래스 주입
     */
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 필터 메인 로직: 모든 HTTP 요청에 대해 실행
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 다음 필터로 넘기기 위한 체인
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        // 1. HTTP 요청 헤더에서 JWT 토큰 추출
        String token = getTokenFromRequest(request);

        // 2. 토큰이 존재하고 유효한지 검증
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

            // 3. 토큰에서 사용자 정보 추출
            String userId = jwtTokenProvider.getUserIdFromToken(token);
            String authorities = jwtTokenProvider.getAuthoritiesFromToken(token);

            // 4. 권한 문자열을 Spring Security 권한 객체로 변환
            List<SimpleGrantedAuthority> grantedAuthorities = null;
            if (StringUtils.hasText(authorities)) {
                grantedAuthorities = Arrays.stream(authorities.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }

            // 5. Spring Security 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, grantedAuthorities);

            // 6. Security Context에 인증 정보 설정
            // 이후 @PreAuthorize, @Secured 등에서 이 정보를 사용
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 7. 다음 필터로 요청 전달 (필터 체인 계속 진행)
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 헤더에서 JWT 토큰 추출
     *
     * @param request HTTP 요청 객체
     * @return JWT 토큰 문자열 (Bearer 제거된 순수 토큰)
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // Authorization 헤더 값 가져오기
        String bearerToken = request.getHeader("Authorization");

        // "Bearer "로 시작하는지 확인하고 토큰 부분만 추출
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거 (7글자)
        }

        return null; // 토큰이 없거나 형식이 잘못된 경우
    }
}