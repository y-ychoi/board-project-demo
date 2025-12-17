package com.example.demo.dto;

import com.example.demo.entity.Board;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor 
@AllArgsConstructor 
public class BoardListResponseDto {

    private Long boardNo;
    private String title;
    private Integer viewCnt;
    private LocalDateTime createDt;
    private LocalDateTime modifyDt;

    // 🚨 DTO의 핵심: 계산된 댓글 개수 필드
    private int commentCount;

    // 💡💡💡 마스킹된 작성자 정보를 담을 필드를 String 타입으로 추가 💡💡💡
    private String authorName;
    private String authorUserId;

    private Long authorNo;

    /**
     * 게시글 내용 미리보기 (REST API용)
     */
    private String contentPreview;

    /**
     * Board 엔티티와 UserService를 받는 생성자 (REST API용)
     *
     * @param board Board 엔티티 객체
     * @param userService 작성자 정보 조회용 서비스
     */
    public BoardListResponseDto(Board board, UserService userService) {
        this.boardNo = board.getBoardNo();
        this.title = board.getTitle();
        this.contentPreview = truncateContent(board.getContent(), 100);
        this.viewCnt = board.getViewCnt();
        this.createDt = board.getCreateDt();
        this.modifyDt = board.getModifyDt();
        this.authorNo = board.getAuthorNo();

        // UserService를 통해 작성자 정보 조회
        try {
            User author = userService.getUserByUserNo(board.getAuthorNo());
            this.authorName = author.getName();
            this.authorUserId = author.getUserId();
        } catch (Exception e) {
            // 작성자 정보 조회 실패 시 기본값
            this.authorName = "알 수 없음";
            this.authorUserId = "";
        }

        // 댓글 수는 일단 0으로 설정
        this.commentCount = 0;
    }

    /**
     * 정적 팩토리 메서드: Board 엔티티를 DTO로 변환
     *
     * @param board Board 엔티티
     * @param userService 작성자 정보 조회용 서비스
     * @return BoardListResponseDto 객체
     */
    public static BoardListResponseDto from(Board board, UserService userService) {
        return new BoardListResponseDto(board, userService);
    }

    /**
     * 내용을 지정된 길이로 자르고 "..." 추가
     *
     * @param content 원본 내용
     * @param maxLength 최대 길이
     * @return 잘린 내용
     */
    private String truncateContent(String content, int maxLength) {
        if (content == null) {
            return "";
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
