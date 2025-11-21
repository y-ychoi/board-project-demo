package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name="TB_BOARD")
@Getter
@Builder // 게시글 생성 시 사용
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor

public class Board extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long boardNo; // 게시글 번호(PK)
	
	@Column(length =200, nullable = false)
	private String title; // 제목
	
	@Column(columnDefinition = "TEXT", nullable = false)
	private String content; //내용
	
	private Integer viewCnt; // 작성일시
	
	// User Entity와 연관 관계 설정 (작성자)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userNo", nullable=false) // DB테이블의 외래키 컬럼 이름
	private User author;
	
	// 게시글 수정을 위한 메서드
	public void update(String title, String content) {
		this.title = title;
		this.content = content;
		
	}

}
