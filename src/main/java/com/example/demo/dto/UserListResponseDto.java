package com.example.demo.dto;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 회원 목록 조회 응답 DTO (REST API용)
 *
 * 역할:
 * 1. 관리자 페이지에서 회원 목록을 JSON으로 반환
 * 2. 기존 UserListDto와 유사하지만 REST API 전용
 * 3. 민감한 정보(비밀번호 등) 제외하고 필요한 정보만 포함
 */
@Getter
public class UserListResponseDto {

    /**
     * 사용자 번호 (PK)
     */
    private final Long userNo;

    /**
     * 로그인 아이디
     */
    private final String userId;

    /**
     * 사용자 이름
     */
    private final String name;

    /**
     * 이메일 주소
     */
    private final String email;

    /**
     * 사용자 권한 (GUEST/ADMIN)
     */
    private final Role role;

    /**
     * 계정 생성일
     */
    private final LocalDateTime createDt;

    /**
     * 계정 수정일
     */
    private final LocalDateTime modifyDt;

    /**
     * 생성자: User 엔티티를 받아서 DTO로 변환
     *
     * @param user User 엔티티 객체
     */
    public UserListResponseDto(User user) {
        this.userNo = user.getUserNo();
        this.userId = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createDt = user.getCreateDt();
        this.modifyDt = user.getModifyDt();
    }

    /**
     * 정적 팩토리 메서드: User 엔티티를 DTO로 변환
     *
     * @param user User 엔티티
     * @return UserListResponseDto 객체
     */
    public static UserListResponseDto from(User user) {
        return new UserListResponseDto(user);
    }
}