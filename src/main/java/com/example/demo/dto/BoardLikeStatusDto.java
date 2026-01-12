package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 좋아요 상태 조회 응답 DTO
 * 게시글 상세 페이지 로딩시 현재 사용자의 좋아요 상태 및 전체 좋아요 수 전달
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardLikeStatusDto {

    /**
     * 현재 사용자의 좋아요 여부
     * true: 이미 좋아요함, false: 좋아요 안함
     */
    private boolean liked;

    /**
     * 해당 게시글의 총 좋아요 수
     * 모든 사용자의 좋아요를 합친 수
     */
    private int likeCount;
}