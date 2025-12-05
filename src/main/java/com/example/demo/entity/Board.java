package com.example.demo.entity;

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
	
	@Setter
	@Comment("조회수")
	private Integer viewCnt; // 조회수
	
	// User Entity와 연관 관계 설정 (작성자)
	//@ManyToOne(fetch = FetchType.LAZY)
	//private User author;
	@Column(name = "author_no", nullable = false)
	@Comment("작성자 고유번호")
	private Long authorNo;
	
	@Transient
	@Setter
	private User authorUser;
	
	// 게시글 수정을 위한 메서드
	public void update(String title, String content) {
		this.title = title;
		this.content = content;
		
	}

}
