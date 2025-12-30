package com.example.demo.controller;

import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApiResponseDto;
import com.example.demo.dto.CommentCreateRequestDto;
import com.example.demo.dto.CommentResponseDto;
import com.example.demo.entity.Comment;
import com.example.demo.entity.User;
import com.example.demo.service.CommentService;
import com.example.demo.service.UserService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 댓글 REST API 컨트롤러
 *
 * 역할:
 * 1. 댓글 CRUD API 제공 (목록, 작성, 삭제)
 * 2. JWT 토큰 기반 인증/인가 처리
 * 3. 계층적 URL 구조 (/boards/{boardNo}/comments)
 * 4. JSON 형태의 표준화된 응답 제공
 */
@Tag(name = "💬 Comment", description = "댓글 API")
@RestController
@RequestMapping("/api/v1/boards/{boardNo}/comments")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;
    private final UserService userService;
    /**
     * 댓글 목록 조회 API
     *
     * GET /api/v1/boards/{boardNo}/comments
     *
     * 특징:
     * - 인증 불필요 (모든 사용자 접근 가능)
     * - 생성일시 오름차순 정렬
     * - 특정 게시글의 모든 댓글 조회
     */
    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 댓글 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CommentResponseDto>>> getComments(
            @Parameter(description = "게시글 번호") @PathVariable Long boardNo) {

    	List<CommentResponseDto> comments = commentService.getCommentList(boardNo);
    	return ResponseEntity.ok()
    			.cacheControl(CacheControl.noCache())
                .body(ApiResponseDto.success(comments, "댓글 목록 조회 성공"));
    }

    /**
     * 댓글 작성 API
     *
     * POST /api/v1/boards/{boardNo}/comments
     *
     * 특징:
     * - JWT 인증 필요 (Authorization: Bearer 토큰)
     * - GUEST 또는 ADMIN 권한 필요
     * - 입력값 검증 (@Valid)
     * - HTTP 201 Created 응답
     */
    @Operation(summary = "댓글 작성", description = "게시글에 새로운 댓글을 작성합니다")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "작성 성공")
    @ApiResponse(responseCode = "400", description = "입력값 오류")
    @ApiResponse(responseCode = "401", description = "인증 필요")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    @PreAuthorize("hasRole('GUEST') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponseDto<Comment>> createComment(
            @Parameter(description = "게시글 번호") @PathVariable Long boardNo,
            @Valid @RequestBody CommentCreateRequestDto createRequest,
            Authentication authentication) {

        // JWT 토큰에서 사용자 정보 추출
        String userId = authentication.getName();
        User user = userService.getUserByUserId(userId);

        // 댓글 작성 처리
        Comment comment = commentService.createCommentForApi(boardNo, createRequest, user.getUserNo());

        return ResponseEntity.status(HttpStatus.CREATED)
                .cacheControl(CacheControl.noCache())
                .body(ApiResponseDto.success(comment, "댓글 작성 성공"));
    }
    
    /**
     * 댓글 수정 API
     * PUT /api/v1/boards/{boardNo}/comments/{commentNo}
     */
    @Operation(summary = "댓글 수정", description = "특정 댓글을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 수정 성공")
    @ApiResponse(responseCode = "403", description = "수정 권한 없음")
    @ApiResponse(responseCode = "404", description = "게시글 또는 댓글을 찾을 수 없음")
    @PreAuthorize("hasRole('GUEST') or hasRole('ADMIN')")
    @PutMapping("/{commentNo}")
    public ResponseEntity<ApiResponseDto<Void>> updateComment(
            @Parameter(description = "게시글 번호") @PathVariable Long boardNo,
            @Parameter(description = "댓글 번호") @PathVariable Long commentNo,
            @Valid @RequestBody CommentCreateRequestDto commentRequest,
            Authentication authentication) {

        String userId = authentication.getName();
        User user = userService.getUserByUserId(userId);

        // 댓글 수정 처리 (권한 체크 포함)
        commentService.updateCommentForApi(boardNo, commentNo, commentRequest.getContent(), userId, user.getRole());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(ApiResponseDto.success(null, "댓글 수정 성공"));
    }

    /**
     * 댓글 삭제 API
     *
     * DELETE /api/v1/boards/{boardNo}/comments/{commentNo}
     *
     * 특징:
     * - JWT 인증 필요
     * - 작성자 또는 ADMIN만 삭제 가능
     * - 댓글이 해당 게시글에 속하는지 검증
     * - 계층적 URL 구조로 직관적 설계
     */
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다 (작성자 또는 ADMIN만 가능)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @ApiResponse(responseCode = "401", description = "인증 필요")
    @ApiResponse(responseCode = "403", description = "삭제 권한 없음")
    @ApiResponse(responseCode = "404", description = "게시글 또는 댓글을 찾을 수 없음")
    @PreAuthorize("hasRole('GUEST') or hasRole('ADMIN')")
    @DeleteMapping("/{commentNo}")
    public ResponseEntity<ApiResponseDto<Void>> deleteComment(
            @Parameter(description = "게시글 번호") @PathVariable Long boardNo,
            @Parameter(description = "댓글 번호") @PathVariable Long commentNo,
            Authentication authentication) {

        // JWT 토큰에서 사용자 정보 추출
        String userId = authentication.getName();
        User user = userService.getUserByUserId(userId);

        // 댓글 삭제 처리 (권한 체크 포함)
        commentService.deleteCommentForApi(boardNo, commentNo, userId, user.getRole());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(ApiResponseDto.success(null, "댓글 삭제 성공"));
    }
}
