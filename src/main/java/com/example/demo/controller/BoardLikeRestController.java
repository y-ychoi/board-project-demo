package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.ApiResponseDto;
import com.example.demo.dto.BoardLikeStatusDto;
import com.example.demo.dto.BoardLikeToggleDto;
import com.example.demo.entity.User;
import com.example.demo.service.BoardLikeService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 게시글 좋아요 REST API 컨트롤러
 */
@Tag(name = "❤️ BoardLike", description = "게시글 좋아요 API")
@RestController
@RequestMapping("/api/v1/boards/{boardNo}/like")
@RequiredArgsConstructor
public class BoardLikeRestController {

    private final BoardLikeService boardLikeService;
    private final UserService userService;

    /**
     * 좋아요 토글 (추가/취소)
     *
     * @param boardNo 게시글 번호
     * @param authentication JWT 인증 정보
     * @return BoardLikeToggleDto 토글 결과
     */
    @Operation(summary = "좋아요 토글",
               description = "게시글 좋아요 추가/취소. 이미 좋아요한 경우 취소, 안한 경우 추가")
    @ApiResponse(responseCode = "200", description = "토글 성공")
    @ApiResponse(responseCode = "401", description = "인증 필요")
    @ApiResponse(responseCode = "404", description = "게시글 또는 사용자를 찾을 수 없음")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<ApiResponseDto<BoardLikeToggleDto>> toggleLike(
            @Parameter(description = "게시글 번호", example = "1")
            @PathVariable Long boardNo,
            Authentication authentication) {

        // JWT에서 사용자 ID 추출
        String userId = authentication.getName();
        User user = userService.getUserByUserId(userId);

        // 좋아요 토글 처리
        BoardLikeToggleDto result = boardLikeService.toggleLike(boardNo, user.getUserNo());

        return ResponseEntity.ok(ApiResponseDto.success(result));
    }

    /**
     * 좋아요 상태 조회
     *
     * @param boardNo 게시글 번호
     * @param authentication JWT 인증 정보
     * @return BoardLikeStatusDto 현재 좋아요 상태
     */
    @Operation(summary = "좋아요 상태 조회",
               description = "현재 사용자의 좋아요 상태 및 전체 좋아요 수 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "401", description = "인증 필요")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<ApiResponseDto<BoardLikeStatusDto>> getLikeStatus(
            @Parameter(description = "게시글 번호", example = "1")
            @PathVariable Long boardNo,
            Authentication authentication) {

        // JWT에서 사용자 ID 추출
        String userId = authentication.getName();
        User user = userService.getUserByUserId(userId);

        // 좋아요 상태 조회
        BoardLikeStatusDto result = boardLikeService.getLikeStatus(boardNo, user.getUserNo());

        return ResponseEntity.ok(ApiResponseDto.success(result));
    }
}