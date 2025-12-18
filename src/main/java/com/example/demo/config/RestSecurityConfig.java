package com.example.demo.config;

import com.example.demo.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * REST API 전용 보안 설정 클래스
 *
 * 역할:
 * 1. REST API 경로에 대한 JWT 토큰 인증 설정
 * 2. 기존 MVC 보안 설정과 분리하여 독립적으로 관리
 * 3. Stateless 세션 정책 적용 (JWT 토큰 기반)
 *
 * @Order(1): 기존 SecurityConfig보다 우선 적용
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(1)  // 기존 SecurityConfig보다 먼저 적용
public class RestSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * REST API 전용 보안 필터 체인
     *
     * @param http HttpSecurity 설정 객체
     * @return REST API용 SecurityFilterChain
     */
    @Bean
    SecurityFilterChain restApiFilterChain(HttpSecurity http) throws Exception {
        http
            // REST API 경로만 이 설정 적용
            .securityMatcher("/api/**")

            // 권한 설정
            .authorizeHttpRequests(auth -> auth
            		 // 인증 API는 모든 사용자 접근 허용
            	    .requestMatchers("/api/v1/auth/**").permitAll()

            	    // 관리자 API는 ADMIN 권한만 허용
            	    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
            	    .requestMatchers("/api/v1/users/**").hasRole("ADMIN")

            	    // 게시글 조회는 모든 사용자 허용 (먼저 설정)
            	    .requestMatchers(HttpMethod.GET, "/api/v1/boards").permitAll()  // 목록 조회
            	    .requestMatchers(HttpMethod.GET, "/api/v1/boards/*").permitAll()  // 상세 조회

            	    // 게시글 작성/수정/삭제는 인증된 사용자만
            	    .requestMatchers(HttpMethod.POST, "/api/v1/boards").hasAnyRole("GUEST", "ADMIN")
            	    .requestMatchers(HttpMethod.PUT, "/api/v1/boards/*").hasAnyRole("GUEST", "ADMIN")
            	    .requestMatchers(HttpMethod.DELETE, "/api/v1/boards/*").hasAnyRole("GUEST", "ADMIN")

            	    .requestMatchers(HttpMethod.GET, "/api/v1/boards/*/comments").permitAll()

            	    // 댓글 작성/삭제는 인증된 사용자만
            	    .requestMatchers(HttpMethod.POST, "/api/v1/boards/*/comments").hasAnyRole("GUEST", "ADMIN")
            	    .requestMatchers(HttpMethod.DELETE, "/api/v1/boards/*/comments/*").hasAnyRole("GUEST", "ADMIN")
            	    
            	    // 나머지 API는 인증 필요
            	    .anyRequest().authenticated()
            )

            // CSRF 비활성화 (REST API는 CSRF 토큰 불필요)
            .csrf(csrf -> csrf.disable())

            // 세션 정책: STATELESS (JWT 토큰 기반, 세션 사용 안 함)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // JWT 인증 필터 추가
            // UsernamePasswordAuthenticationFilter 이전에 JWT 필터 실행
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // 폼 로그인 비활성화 (REST API는 JSON 기반 인증)
            .formLogin(form -> form.disable())

            // HTTP Basic 인증 비활성화
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    /**
     * AuthenticationManager Bean 등록
     * JWT 토큰 발급 시 사용자 인증에 필요
     *
     * @param authConfig 인증 설정
     * @return AuthenticationManager
     */
    @Bean
    AuthenticationManager restAuthenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
