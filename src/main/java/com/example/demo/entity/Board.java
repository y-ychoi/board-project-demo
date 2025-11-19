package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder // 게시글 생성 시 사용
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Board {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 게시글 번호(PK)
	
	@Column(length =200, nullable =false)
	private String title; // 제목
	
	@Column(columnDefinition = "TEXT", nullable = false)
	private String content; //내용
	
	private LocalDateTime createDate; // 작성일시
	
	// User Entity와 연관 관계 설정 (작성자)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id") // DB테이블의 외래키 컬럼 이름
	private User author;
	
	// 게시글 수정을 위한 메서드
	public void update(String title, String content) {
		this.title = title;
		this.author content = content;
		
	}

}
