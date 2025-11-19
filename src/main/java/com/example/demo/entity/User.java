package com.example.boardproject.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor; // Lombok의 기본 생성자 어노테이션은 사용

// Getter와 Builder 패턴은 요구사항대로 사용합니다.
@Entity
@Table(name = "site_user")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자 (필수)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(unique = true)
    private String username; // 로그인 ID
    
    private String password; 

    @Column(unique = true)
    private String email;

    private String role; // 회원의 권한

    // @AllArgsConstructor 대신, @Builder를 통해 생성 시 모든 필드를 인자로 받도록 처리합니다.
    // @Builder 어노테이션은 내부적으로 모든 필드를 받는 생성자를 자동으로 생성해 줍니다.
    
    // 비밀번호 업데이트 편의 메서드
    public void updatePassword(String password) {
        this.password = password;
    }
}