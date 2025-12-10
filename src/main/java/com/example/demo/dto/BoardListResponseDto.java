package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
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

}