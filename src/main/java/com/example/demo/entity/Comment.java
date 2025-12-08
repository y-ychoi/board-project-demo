package com.example.demo.entity;

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
    
    // 🚨🚨🚨 외래키 없이 단순 ID 저장 🚨🚨🚨
    @Column(name = "board_no", nullable = false)
    private Long boardNo; // 댓글이 달린 게시글 번호 (FK 역할)

    @Column(name = "author_no", nullable = false)
    private Long authorNo; // 댓글 작성자 번호 (FK 역할)
    
    // @Transient 필드는 필요하지 않습니다. 댓글은 즉시 작성자 ID를 사용할 것입니다.
}