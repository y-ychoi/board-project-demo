package com.example.demo.dto;

import com.example.demo.entity.Board;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 상세 조회 응답 DTO (REST API용)
 *
 * 역할:
 * 1. 게시글 상세 조회 API에서 JSON 응답으로 사용
 * 2. 게시글의 모든 정보를 포함 (제목, 전체 내용, 작성자 등)
 * 3. 댓글 목록은 별도 API로 조회하므로 포함하지 않음
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDetailResponseDto {

    /**
     * 게시글 번호 (PK)
     */
    private Long boardNo;

    /**
     * 게시글 제목
     */
    private String title;

    /**
     * 게시글 전체 내용
     */
    private String content;

    /**
     * 조회수
     */
    private Integer viewCnt;

    /**
     * 작성자 이름
     */
    private String authorName;

    /**
     * 작성자 ID
     */
    private String authorId;

    /**
     * 작성자 번호
     */
    private Long authorNo;

    /**
     * 게시글 작성일
     */
    private LocalDateTime createDt;

    /**
     * 게시글 수정일
     */
    private LocalDateTime modifyDt;

    /**
     * 현재 로그인한 사용자가 작성자인지 여부
     * 수정/삭제 버튼 표시 여부 결정용	
     */
    private boolean isAuthor;

    /**
     * 생성자: Board 엔티티와 현재 사용자 정보를 받아서 DTO로 변환
     *
     * @param board Board 엔티티 객체
     * @param userService 작성자 정보 조회용 서비스
     * @param currentUserId 현재 로그인한 사용자 ID (권한 체크용)
     */
    public BoardDetailResponseDto(Board board, UserService userService, String currentUserId) {
        this.boardNo = board.getBoardNo();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.viewCnt = board.getViewCnt();
        this.createDt = board.getCreateDt();
        this.modifyDt = board.getModifyDt();
        this.authorNo = board.getAuthorNo();

        // 작성자 정보 조회 (final 필드는 한 번만 할당)
        User author = null;
        try {
            author = userService.getUserByUserNo(board.getAuthorNo());
        } catch (Exception e) {
            // 조회 실패 시 author는 null로 유지
        }

        // final 필드들을 한 번만 할당
        if (author != null) {
            this.authorName = author.getName();
            this.authorId = author.getUserId();
            this.isAuthor = currentUserId != null && currentUserId.equals(author.getUserId());
        } else {
            this.authorName = "알 수 없음";
            this.authorId = "";
            this.isAuthor = false;
        }
    }

    /**
     * 정적 팩토리 메서드: Board 엔티티를 DTO로 변환
     *
     * @param board Board 엔티티
     * @param userService 작성자 정보 조회용 서비스
     * @param currentUserId 현재 로그인한 사용자 ID
     * @return BoardDetailResponseDto 객체
     */
    public static BoardDetailResponseDto from(Board board, UserService userService, String currentUserId) {
        return new BoardDetailResponseDto(board, userService, currentUserId);
    }

    /**
     * 비로그인 사용자용 정적 팩토리 메서드
     *
     * @param board Board 엔티티
     * @param userService 작성자 정보 조회용 서비스
     * @return BoardDetailResponseDto 객체 (isAuthor = false)
     */
    public static BoardDetailResponseDto from(Board board, UserService userService) {
        return new BoardDetailResponseDto(board, userService, null);
    }
}