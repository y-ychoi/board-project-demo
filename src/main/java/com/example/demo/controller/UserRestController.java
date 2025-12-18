package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDto;
import com.example.demo.dto.UserListResponseDto;
import com.example.demo.dto.UserRoleUpdateRequestDto;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 관리 REST API 컨트롤러
 *
 * 역할:
 * 1. 회원 목록 조회 API (관리자 전용)
 * 2. 권한 변경 API (관리자 전용)
 * 3. 내 정보 조회 API (로그인한 사용자)
 *
 * 모든 API는 JWT 토큰 인증 필요
 */
@Tag(name = "👥 User Management", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    /**
     * 회원 목록 조회 API (관리자 전용)
     *
     * @param authentication 현재 로그인한 사용자 정보
     * @return 회원 목록과 현재 사용자를 맨 위로 정렬한 응답
     */
    @Operation(summary = "회원 목록 조회", description = "모든 회원 목록을 조회합니다 (ADMIN 전용)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")  // ADMIN 권한만 접근 가능
    public ResponseEntity<ApiResponseDto<List<UserListResponseDto>>> getAllUsers(
            Authentication authentication) {

        try {
            // 1. 모든 사용자 목록 조회 (기존 Service 메서드 재사용)
            List<User> users = userService.getAllUsersForApi();

            // 2. 현재 로그인한 사용자 ID
            String currentUserId = authentication.getName();

            // 3. User 엔티티를 DTO로 변환하면서 현재 사용자를 맨 위로 정렬
            List<UserListResponseDto> userList = users.stream()
                    .map(UserListResponseDto::from)
                    .sorted((u1, u2) -> {
                        // 현재 사용자를 맨 위로
                        if (u1.getUserId().equals(currentUserId)) return -1;
                        if (u2.getUserId().equals(currentUserId)) return 1;
                        // 나머지는 가입일 기준 내림차순
                        return u2.getCreateDt().compareTo(u1.getCreateDt());
                    })
                    .collect(Collectors.toList());

            // 4. 성공 응답 반환
            return ResponseEntity.ok(
                    ApiResponseDto.success(userList, "회원 목록 조회가 완료되었습니다.")
            );

        } catch (Exception e) {
            // 5. 오류 발생 시 500 응답
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error(
                            "회원 목록 조회 중 오류가 발생했습니다.",
                            "INTERNAL_SERVER_ERROR",
                            null
                    ));
        }
    }

    /**
     * 사용자 권한 변경 API (관리자 전용)
     *
     * @param userNo 권한을 변경할 사용자 번호
     * @param request 변경할 권한 정보
     * @param bindingResult 입력값 검증 결과
     * @param authentication 현재 로그인한 사용자 정보
     * @return 권한 변경 결과 응답
     */
    @Operation(summary = "사용자 권한 변경", description = "사용자의 권한을 변경합니다 (ADMIN 전용)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "변경 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @PutMapping("/{userNo}/role")
    @PreAuthorize("hasRole('ADMIN')")  // ADMIN 권한만 접근 가능
    public ResponseEntity<ApiResponseDto<String>> updateUserRole(
            @PathVariable Long userNo,
            @Valid @RequestBody UserRoleUpdateRequestDto request,
            BindingResult bindingResult,
            Authentication authentication) {

        // 1. 입력값 검증 오류 확인
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(errorMessage, "VALIDATION_ERROR", null));
        }

        try {
            // 2. 자기 자신의 권한 변경 방지
            User targetUser = userService.getUserByUserNo(userNo);
            String currentUserId = authentication.getName();

            if (targetUser.getUserId().equals(currentUserId)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error(
                                "자신의 권한은 변경할 수 없습니다.",
                                "SELF_ROLE_CHANGE_NOT_ALLOWED",
                                "다른 관리자에게 요청하세요."
                        ));
            }

            // 3. 권한 변경 처리 (기존 Service 메서드 재사용)
            userService.updateUserRole(userNo, request.getRole());

            // 4. 성공 응답 반환
            return ResponseEntity.ok(
                    ApiResponseDto.success("권한이 성공적으로 변경되었습니다.")
            );

        } catch (IllegalArgumentException e) {
            // 5. 사용자를 찾을 수 없는 경우
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(
                            "존재하지 않는 사용자입니다.",
                            "USER_NOT_FOUND",
                            "사용자 번호를 확인해주세요."
                    ));
        } catch (Exception e) {
            // 6. 기타 서버 오류
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error(
                            "권한 변경 중 오류가 발생했습니다.",
                            "INTERNAL_SERVER_ERROR",
                            null
                    ));
        }
    }

    /**
     * 내 정보 조회 API (로그인한 사용자)
     *
     * @param authentication 현재 로그인한 사용자 정보
     * @return 현재 사용자의 정보
     */
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "401", description = "인증 필요")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")  // 로그인한 사용자만 접근 가능
    public ResponseEntity<ApiResponseDto<UserListResponseDto>> getMyInfo(
            Authentication authentication) {

        try {
            // 1. 현재 로그인한 사용자 정보 조회
            String currentUserId = authentication.getName();
            User currentUser = userService.getUserByUserId(currentUserId);

            // 2. DTO로 변환
            UserListResponseDto userInfo = UserListResponseDto.from(currentUser);

            // 3. 성공 응답 반환
            return ResponseEntity.ok(
                    ApiResponseDto.success(userInfo, "내 정보 조회가 완료되었습니다.")
            );

        } catch (Exception e) {
            // 4. 오류 발생 시 500 응답
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDto.error(
                            "내 정보 조회 중 오류가 발생했습니다.",
                            "INTERNAL_SERVER_ERROR",
                            null
                    ));
        }
    }
}