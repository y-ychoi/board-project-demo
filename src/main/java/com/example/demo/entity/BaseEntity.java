package com.example.demo.entity;

import jakarta.persistence.EntityListeners; //Auditing 기능 활성화 리스너
import jakarta.persistence.MappedSuperclass; //Entity 클래스가 상속받을 수 있도록 지정
import lombok.Getter;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate; // 생성일시 자동 저장 어노테이션
import org.springframework.data.annotation.LastModifiedDate; // 최종 수정일시 자동 저장 어노테이션
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // JPA Auditing 리스너

import java.time.LocalDateTime; // 날짜와 시간정보를 저장하는 자바 타입


@Getter // Lombok: 이 클래스를 상속받는 엔티티에서 필드를 조회할 수 있도록 함.
@MappedSuperclass // JPA: 이 클래스의 필드(createDt, updateDt)들을 자식 엔티티의 테이블컬럼으로 매핑
@EntityListeners(AuditingEntityListener.class)// JPA : Auditing 기능을 활성화하여 날짜를 자동으로 넣어줌

// 상속용 기본 엔티티
public class BaseEntity {
	
	@CreatedDate // Spring Data JPA : 엔티티가 처음 생성될 때 현재 시간을 자동 저장
	@Comment("작성일시")
	private LocalDateTime createDt; // 작성일시
	
	@LastModifiedDate // Spring Data JPA; // Spring Data JPA : 엔티티가 수정될때마다 현재 시간을 자동저장
	@Comment("수정일시")
	private LocalDateTime modifyDt; // 수정일시
	

}
