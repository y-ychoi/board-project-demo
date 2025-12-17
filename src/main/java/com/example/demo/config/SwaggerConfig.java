package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 3.0 설정 클래스
 *
 * 역할:
 * 1. API 문서 자동 생성 설정
 * 2. JWT 토큰 인증 스키마 정의
 * 3. API 정보 및 보안 설정
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 설정을 정의하는 Bean
     *
     * @return OpenAPI 설정 객체
     */
    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                // API 기본 정보 설정
                .info(new Info()
                        .title("게시판 REST API")                    // API 제목
                        .description("Spring Boot 게시판 REST API 문서")  // API 설명
                        .version("1.0.0"))                          // API 버전

                // JWT 토큰 보안 스키마 정의
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",           // 보안 스키마 이름
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)     // HTTP 인증 방식
                                        .scheme("bearer")                   // Bearer 토큰 방식
                                        .bearerFormat("JWT")))              // JWT 형식 명시

                // 전역 보안 요구사항 설정 (모든 API에 JWT 토큰 필요)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}