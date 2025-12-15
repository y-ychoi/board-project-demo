package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	// 1. PasswordEncoder Bean 등록
	@Bean
	PasswordEncoder passwordEncoder() {
		// UserService에서 사용할 BCryptPasswordEncoder 객체를 Spring컨테이너에 등록
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // AuthenticationConfiguration 객체는 Spring Security가 자동으로 설정 정보를 담아 주입해 줍니다.
        return authenticationConfiguration.getAuthenticationManager();
    }
	
	// 2. SecurityFilterChain 설정 (로그인/로그아웃 및 권한 설정)
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        	.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
        	    
        	    // 🚨 1. 관리자 전용 경로는 ADMIN 권한만 허용
        	    .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
        	    
        	    // 🚨 2. 게시글 작성 경로는 인증된 사용자에게만 허용
        	    .requestMatchers(new AntPathRequestMatcher("/board/create")).authenticated()
        	    
        	    // 🚨 3. 수정 및 삭제 경로는 인증된 사용자에게만 허용 (추가!)
        	    .requestMatchers(new AntPathRequestMatcher("/board/modify")).authenticated()
        	    .requestMatchers(new AntPathRequestMatcher("/board/delete")).authenticated()

        	    // 4. 나머지 모든 경로는 모두 허용
        	    .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
        	.csrf((csrf) -> csrf.disable())
            
            .formLogin((formLogin) -> formLogin
            	    // 사용자 정의 로그인 페이지 URL (GET 요청)
            	    .loginPage("/user/login") 
            	    
            	    // 🚨🚨🚨 이 코드를 명시적으로 추가합니다. (POST 요청 처리 경로) 🚨🚨🚨
            	    // 폼이 POST될 때, Spring Security가 이 URL을 통해 인증을 처리하도록 합니다.
            	    .loginProcessingUrl("/login") 
            	    
            	    // 로그인 성공 시 기본 이동 경로
            	    .defaultSuccessUrl("/board/list"))
            
            .logout((logout) -> logout
            	    .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout","POST")) // 🚨 로그아웃 URL 설정
            	    .logoutSuccessUrl("/") // 로그아웃 성공 시 목록 페이지로 리다이렉트
            	    .invalidateHttpSession(true)) // 세션 무효화
            ;
        return http.build();
    }

}
