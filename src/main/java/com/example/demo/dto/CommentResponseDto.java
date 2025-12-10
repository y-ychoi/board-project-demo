package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponseDto {

    private Long commentNo;
    private String content;    
    private LocalDateTime createDt;
    private LocalDateTime modifyDt;

    // 마스킹된 작성자 정보 (저희가 이전에 추가한 필드)
    private String authorName;
    private String authorUserId;
    private Long authorNo;
    

}