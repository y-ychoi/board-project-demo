package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDto;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.dto.SignupRequestDto;
import com.example.demo.entity.User;
import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 REST API 컨트롤러
 *
 * 역할:
 * 1. 로그인 API - JWT 토큰 발급
 * 2. 회원가입 API - 새 사용자 등록
 * 3. 로그아웃 API - 토큰 무효화 (향후 구현)
 *
 * @RestController: @Controller + @ResponseBody
 * 모든 메서드가 JSON 응답을 반환
 */
@RestController
@RequestMapping("/api/v1/auth")  // 기본 URL: /api/v1/auth
@RequiredArgsConstructor
public class AuthRestController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * 로그인 API
     *
     * @param loginRequest 로그인 요청 정보 (아이디, 비밀번호)
     * @param bindingResult 입력값 검증 결과
     * @return JWT 토큰과 사용자 정보가 포함된 응답
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto loginRequest,
            BindingResult bindingResult) {

        // 1. 입력값 검증 오류 확인
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(errorMessage, "VALIDATION_ERROR", "입력값을 확인해주세요."));
        }

        try {
            // 2. Spring Security를 통한 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserId(),
                            loginRequest.getPassword()
                    )
            );

            // 3. 인증 성공 시 사용자 정보 조회
            User user = userService.getUserByUserId(loginRequest.getUserId());

            // 4. JWT 토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(authentication);
            String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

            // 5. 응답 DTO 생성
            LoginResponseDto loginResponse = new LoginResponseDto(
                    accessToken,
                    refreshToken,
                    3600, // 1시간 (초 단위)
                    user.getUserNo(),
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole()
            );

            // 6. 성공 응답 반환
            return ResponseEntity.ok(
                    ApiResponseDto.success(loginResponse, "로그인이 성공적으로 완료되었습니다.")
            );

        } catch (Exception e) {
            // 7. 인증 실패 시 오류 응답
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(
                            "아이디 또는 비밀번호가 올바르지 않습니다.",
                            "INVALID_CREDENTIALS",
                            "로그인 정보를 다시 확인해주세요."
                    ));
        }
    }

    /**
     * 회원가입 API
     *
     * @param signupRequest 회원가입 요청 정보
     * @param bindingResult 입력값 검증 결과
     * @return 회원가입 결과 응답
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<String>> signup(
            @Valid @RequestBody SignupRequestDto signupRequest,
            BindingResult bindingResult) {

        // 1. 입력값 검증 오류 확인
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(errorMessage, "VALIDATION_ERROR", null));
        }

        // 2. 아이디 형식 검증 (영문/숫자 3~15자)
        String regex = "^[a-zA-Z0-9]{3,15}$";
        if (!signupRequest.getUserId().matches(regex)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(
                            "아이디는 영문/숫자 3~15자만 사용 가능합니다.",
                            "INVALID_USER_ID_FORMAT",
                            null
                    ));
        }

        // 3. 비밀번호 일치 확인
        if (!signupRequest.getPassword().equals(signupRequest.getPasswordConfirm())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(
                            "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
                            "PASSWORD_MISMATCH",
                            null
                    ));
        }

        try {
            // 4. 회원가입 처리 (REST API 전용 메서드 사용)
            userService.create(signupRequest);  // SignupRequestDto를 직접 전달
            // 5. 성공 응답 반환
            return ResponseEntity.ok(
                    ApiResponseDto.success("회원가입이 성공적으로 완료되었습니다.")
            );

        } catch (IllegalStateException e) {
            // 6. 중복 아이디 등 비즈니스 로직 오류
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(
                            e.getMessage(),
                            "DUPLICATE_USER_ID",
                            "다른 아이디를 사용해주세요."
                    ));
        } catch (Exception e) {
            // 7. 기타 서버 오류
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error(
                            "회원가입 처리 중 오류가 발생했습니다.",
                            "INTERNAL_SERVER_ERROR",
                            null
                    ));
        }
    }
}

