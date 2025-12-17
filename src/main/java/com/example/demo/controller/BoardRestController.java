package com.example.demo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApiResponseDto;
import com.example.demo.dto.BoardCreateRequestDto;
import com.example.demo.dto.BoardUpdateRequestDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.User;
import com.example.demo.service.BoardService;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 게시판 REST API 컨트롤러
 *
 * 역할:
 * 1. 게시글 CRUD API 제공 (목록, 상세, 작성, 수정, 삭제)
 * 2. JWT 토큰 기반 인증/인가 처리
 * 3. Swagger API 문서 자동 생성
 * 4. JSON 형태의 표준화된 응답 제공
 */
@Tag(name = "📝 Board", description = "게시판 API")  // Swagger 문서에서 API 그룹 이름
@RestController  // REST API 컨트롤러임을 명시 (JSON 응답)
@RequestMapping("/api/v1/boards")  // 모든 메서드의 기본 URL 경로
@RequiredArgsConstructor  // final 필드에 대한 생성자 자동 생성
public class BoardRestController {

    // 게시글 비즈니스 로직 처리를 위한 서비스
    private final BoardService boardService;

    // 사용자 정보 조회를 위한 서비스 (JWT에서 추출한 userId로 User 엔티티 조회)
    private final UserService userService;

    /**
     * 게시글 목록 조회 API
     *
     * GET /api/v1/boards?page=0&size=10
     *
     * 특징:
     * - 인증 불필요 (모든 사용자 접근 가능)
     * - 페이징 처리 지원
     * - 최신 게시글 순으로 정렬
     */
    @Operation(summary = "게시글 목록 조회", description = "페이징된 게시글 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping  // GET /api/v1/boards
    public ResponseEntity<ApiResponseDto<Page<Board>>> getBoards(
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,  // 기본값: 첫 번째 페이지

            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "20") int size) {  // 기본값: 20개씩

        // Spring Data JPA의 Pageable 객체 생성 (페이지 번호, 크기 설정)
        Pageable pageable = PageRequest.of(page, size);

        // BoardService에서 페이징된 게시글 목록 조회
        Page<Board> boards = boardService.getBoardsForApi(pageable);

        // 표준화된 JSON 응답 형식으로 반환
        return ResponseEntity.ok(ApiResponseDto.success(boards, "게시글 목록 조회 성공"));
    }

    /**
     * 게시글 상세 조회 API
     *
     * GET /api/v1/boards/{boardNo}
     *
     * 특징:
     * - 인증 불필요
     * - 조회 시 조회수 자동 증가
     * - 존재하지 않는 게시글 시 404 오류
     */
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다 (조회수 증가)")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    @GetMapping("/{boardNo}")  // GET /api/v1/boards/1
    public ResponseEntity<ApiResponseDto<Board>> getBoard(
            @Parameter(description = "게시글 번호")
            @PathVariable Long boardNo) {  // URL 경로에서 게시글 번호 추출

        // BoardService에서 게시글 조회 및 조회수 증가 처리
        Board board = boardService.getBoardForApi(boardNo);

        return ResponseEntity.ok(ApiResponseDto.success(board, "게시글 조회 성공"));
    }

    /**
     * 게시글 작성 API
     *
     * POST /api/v1/boards
     *
     * 특징:
     * - JWT 인증 필요 (Authorization: Bearer 토큰)
     * - USER 또는 ADMIN 권한 필요
     * - 입력값 검증 (@Valid)
     * - HTTP 201 Created 응답
     */
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다")
    @SecurityRequirement(name = "bearerAuth")  // Swagger에서 JWT 토큰 입력 UI 표시
    @ApiResponse(responseCode = "201", description = "작성 성공")
    @ApiResponse(responseCode = "400", description = "입력값 오류")
    @ApiResponse(responseCode = "401", description = "인증 필요")
    @PreAuthorize("hasRole('GUEST') or hasRole('ADMIN')")  // 메서드 실행 전 권한 체크
    @PostMapping  // POST /api/v1/boards
    public ResponseEntity<ApiResponseDto<Board>> createBoard(
            @Valid @RequestBody BoardCreateRequestDto createRequest,  // JSON → DTO 변환 및 검증
            Authentication authentication) {  // Spring Security에서 현재 로그인 사용자 정보 주입

        // JWT 토큰에서 추출한 사용자 ID
        String userId = authentication.getName();

        // 사용자 ID로 User 엔티티 조회 (userNo 필요)
        User user = userService.getUserByUserId(userId);

        // BoardService에서 게시글 생성 처리
        Board board = boardService.createBoardForApi(createRequest, user.getUserNo());

        // HTTP 201 Created 상태코드와 함께 생성된 게시글 정보 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(board, "게시글 작성 성공"));
    }

    /**
     * 게시글 수정 API
     *
     * PUT /api/v1/boards/{boardNo}
     *
     * 특징:
     * - JWT 인증 필요
     * - 작성자만 수정 가능 (ADMIN도 수정 불가!)
     * - 권한 없으면 403 Forbidden 오류
     */
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다 (작성자만 가능)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "400", description = "입력값 오류")
    @ApiResponse(responseCode = "401", description = "인증 필요")
    @ApiResponse(responseCode = "403", description = "수정 권한 없음")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    @PreAuthorize("hasRole('GUEST') or hasRole('ADMIN')")
    @PutMapping("/{boardNo}")  // PUT /api/v1/boards/1
    public ResponseEntity<ApiResponseDto<Board>> updateBoard(
            @Parameter(description = "게시글 번호")
            @PathVariable Long boardNo,

            @Valid @RequestBody BoardUpdateRequestDto updateRequest,
            Authentication authentication) {

        String userId = authentication.getName();

        // BoardService에서 권한 체크 및 수정 처리
        // 작성자가 아니면 IllegalStateException 발생 → RestExceptionHandler에서 403 처리
        Board board = boardService.updateBoardForApi(boardNo, updateRequest, userId);

        return ResponseEntity.ok(ApiResponseDto.success(board, "게시글 수정 성공"));
    }

    /**
     * 게시글 삭제 API
     *
     * DELETE /api/v1/boards/{boardNo}
     *
     * 특징:
     * - JWT 인증 필요
     * - 작성자 또는 ADMIN만 삭제 가능
     * - ADMIN은 모든 게시글 삭제 권한 보유
     */
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다 (작성자 또는 ADMIN만 가능)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @ApiResponse(responseCode = "401", description = "인증 필요")
    @ApiResponse(responseCode = "403", description = "삭제 권한 없음")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    @PreAuthorize("hasRole('GUEST') or hasRole('ADMIN')")
    @DeleteMapping("/{boardNo}")  // DELETE /api/v1/boards/1
    public ResponseEntity<ApiResponseDto<Void>> deleteBoard(
            @Parameter(description = "게시글 번호")
            @PathVariable Long boardNo,
            Authentication authentication) {

        String userId = authentication.getName();
        User user = userService.getUserByUserId(userId);

        // BoardService에서 권한 체크 및 삭제 처리
        // userId와 Role을 모두 전달하여 작성자 또는 ADMIN 권한 확인
        boardService.deleteBoardForApi(boardNo, userId, user.getRole());

        // 삭제는 반환할 데이터가 없으므로 null과 성공 메시지만 반환
        return ResponseEntity.ok(ApiResponseDto.success(null, "게시글 삭제 성공"));
    }
}