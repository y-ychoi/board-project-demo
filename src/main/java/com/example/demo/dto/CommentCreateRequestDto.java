package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 댓글 작성 요청 DTO (REST API용)
 *
 * 역할:
 * 1. 클라이언트에서 보내는 댓글 작성 정보를 받는 객체
 * 2. JSON 형태의 댓글 데이터를 Java 객체로 변환
 * 3. 입력값 검증 (내용 필수 및 길이 제한)
 */
@Schema(description = "댓글 작성 요청 정보")
@Getter
@Setter
public class CommentCreateRequestDto {

    /**
     * 댓글 내용
     * 최소 1자 이상, 최대 1000자 제한
     */
    @Schema(description = "댓글 내용", example = "좋은 게시글 감사합니다!")
    @NotBlank(message = "댓글 내용은 필수입니다")
    @Size(min = 1, max = 1000, message = "댓글은 1자 이상 1000자 이하로 입력해주세요")
    private String content;
}