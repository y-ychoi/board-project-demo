package com.example.demo.entity;

/**
 * 회원 권한을 나타내는 열거형(Enum)
 * GUEST: 일반 회원 (기본값)
 * ADMIN: 관리자 회원 (회원 관리 페이지 접근 가능)
 */
public enum Role {
    GUEST,  // 일반 회원
    ADMIN   // 관리자
}
