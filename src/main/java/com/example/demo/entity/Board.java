package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Comment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="TB_BOARD")
@Comment("게시판 테이블")
@Getter
@Setter
@Builder // 게시글 생성 시 사용
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor

public class Board extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("게시글 번호")
	private Long boardNo; // 게시글 번호(PK)
	
	@Column(length =300, nullable = false)
	@Comment("제목")
	private String title; // 제목
	
	@Column(columnDefinition = "TEXT", nullable = false)
	@Comment("내용")
	private String content; //내용
	
	@Comment("조회수")
	private Integer viewCnt; // 조회수
	
	// User Entity 연관 관계 설정 (작성자)
	//@ManyToOne(fetch = FetchType.LAZY)
	//private User author;
	@Column(name = "author_no", nullable = false)
	@Comment("작성자 고유번호")
	private Long authorNo;
	
	@Transient
	private User authorUser;
	
	// 기존 필드들 아래에 추가
	@Column(name = "like_count")
	@Comment("좋아요 수")
	@Builder.Default
	private Integer likeCount = 0; // 좋아요 수 (기본값 0)
	
	
	// 🚨 OneToMany 관계 설정 및 CascadeType.REMOVE, orphanRemoval=true 적용
    @OneToMany(mappedBy = "board", // Comment.java의 private Board board 필드 이름을 지정
               cascade = CascadeType.REMOVE, // 1. Board 삭제 시 Comment도 삭제
               orphanRemoval = true)     // 2. 컬렉션에서 댓글이 제거될 경우 DB에서 삭제
    @Builder.Default
    private List<com.example.demo.entity.Comment>comments = new ArrayList<>();
	
	// 게시글 수정을 위한 메서드
	public void update(String title, String content) {
		this.title = title;
		this.content = content;
		
	}

}
