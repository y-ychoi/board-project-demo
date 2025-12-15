package com.example.demo.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor; // Lombok의 기본 생성자 어노테이션은 사용

@Entity
@Table(name = "TB_USER")
@Comment("회원 테이블")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용 시 entity의 안정성 확보를 위해 사용되는 표준패턴
@AllArgsConstructor

public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("회원 고유번호")
    private Long userNo; // PK

    @Column(unique = true,nullable=false)
    @Comment("로그인 ID")
    private String userId; // 로그인 ID
    
    @Column(nullable=false)
    @Comment("로그인 비밀번호")
    private String userPw; 

    @Column(nullable = false, length = 100)  // nullable = false 추가: 이름은 필수 입력
    @Comment("회원 이름")
    private String name;
    
    @Column(nullable = false)  // 이메일은 필수 입력
    @Comment("회원 이메일")
    private String email;

    @Enumerated(EnumType.STRING)  // Enum을 문자열로 DB에 저장 (GUEST, ADMIN)
    @Column(nullable = false)
    @Comment("회원 권한")
    private Role role;  // 회원의 권한 (GUEST 또는 ADMIN)

    // @AllArgsConstructor 대신, @Builder를 통해 생성 시 모든 필드를 인자로 받도록 처리합니다.
    // @Builder 어노테이션은 내부적으로 모든 필드를 받는 생성자를 자동으로 생성해 줍니다.
    
    // 비밀번호 업데이트 편의 메서드
    public void updatePassword(String userPw) {
        this.userPw = userPw;
    }
    public void updateRole(Role role) {
        this.role = role;
    }
}