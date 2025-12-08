package com.example.demo.dto;

import com.example.demo.entity.User;
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

    // 🚨 DTO의 핵심: 계산된 댓글 개수 필드
    private Long commentCount;

    // 💡 작성자 정보를 담기 위한 User 객체 (수동 조인 결과)
    private User authorUser;

    // BoardService에서 Entity를 DTO로 변환할 때 사용할 생성자/메서드 등을 추가할 수 있습니다.
}