package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 좋아요 토글 결과 응답 DTO
 * 좋아요 버튼 클릭시 서버에서 클라이언트로 전달하는 데이터
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardLikeToggleDto {

    /**
     * 토글 후 좋아요 상태
     * true: 좋아요 추가됨, false: 좋아요 취소됨
     */
    private boolean liked;

    /**
     * 토글 후 총 좋아요 수
     * 실시간으로 업데이트된 정확한 좋아요 수
     */
    private int likeCount;
}