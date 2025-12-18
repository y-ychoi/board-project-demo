package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_COMMENT")
@Getter
@Setter // 댓글 내용은 수정될 수 있으므로 @Setter를 허용합니다.
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentNo; // 댓글 번호 (PK)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 댓글 내용
    
    @ManyToOne 
    @JoinColumn(name = "board_no") // DB 테이블의 외래 키 컬럼 이름 (board_no)을 지정합니다.
    @JsonIgnore 
    private Board board;

    @Column(name = "author_no", nullable = false)
    private Long authorNo; // 댓글 작성자 번호 (FK 역할)
    
    public void updateContent(String content) {
        this.content = content;
    }
    // @Transient 필드는 필요하지 않습니다. 댓글은 즉시 작성자 ID를 사용할 것입니다.
}