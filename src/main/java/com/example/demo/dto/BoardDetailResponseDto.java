package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardDetailResponseDto {

    private Long boardNo;
    private String title;
    private String content;
    private Integer viewCnt;
    private LocalDateTime createDt;
    private LocalDateTime modifyDt;
    
    // 🚨 마스킹된 작성자 정보 (String)
    private String authorName;
    private String authorUserId;
    
    // 상세 조회, 수정/삭제 권한 확인을 위해 작성자의 PK(고유번호) 유지
    private Long authorNo; 
    
    
}