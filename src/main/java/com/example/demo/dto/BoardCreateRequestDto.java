package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 작성 요청 DTO (REST API용)
 *
 * 역할:
 * 1. 클라이언트에서 보내는 게시글 작성 정보를 받는 객체
 * 2. JSON 형태의 게시글 데이터를 Java 객체로 변환
 * 3. 입력값 검증 (제목, 내용 필수 및 길이 제한)
 */
@Schema(description = "게시글 작성 요청 정보")
@Getter
@Setter
public class BoardCreateRequestDto {

    /**
     * 게시글 제목
     * 최대 300자 제한 (DB 컬럼 길이와 일치)
     */
    @Schema(description = "게시글 제목", example = "안녕하세요 첫 게시글입니다")
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 300, message = "제목은 300자 이하로 입력해주세요")
    private String title;

    /**
     * 게시글 내용
     * 최소 10자 이상 작성 필요
     */
    @Schema(description = "게시글 내용", example = "게시글 내용을 여기에 작성합니다. 최소 10자 이상 입력해주세요.")
    @NotBlank(message = "내용은 필수입니다")
    @Size(min = 10, message = "내용은 10자 이상 입력해주세요")
    private String content;
}