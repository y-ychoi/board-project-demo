package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;

import lombok.Getter;

/**
 * 회원 목록 조회용 DTO
 * 관리자 페이지에서 회원 목록을 보여줄 때 사용
 */
@Getter
public class UserListDto {

    private Long userNo;        // 회원 번호
    private String userId;      // 로그인 ID
    private String name;        // 회원 이름
    private String email;       // 이메일
    private Role role;          // 권한 (GUEST 또는 ADMIN)
    private LocalDateTime createDt;  // 가입일

    /**
     * Entity를 DTO로 변환하는 생성자
     * @param user User 엔티티
     */
    public UserListDto(User user) {
        this.userNo = user.getUserNo();
        this.userId = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createDt = user.getCreateDt();
    }
}