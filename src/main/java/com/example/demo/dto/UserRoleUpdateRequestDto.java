package com.example.demo.dto;

import com.example.demo.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 권한 변경 요청 DTO
 *
 * 역할:
 * 1. 관리자가 다른 사용자의 권한을 변경할 때 사용
 * 2. JSON 요청 데이터를 Java 객체로 변환
 * 3. 권한 값 검증 (GUEST 또는 ADMIN만 허용)
 */
@Getter
@Setter
public class UserRoleUpdateRequestDto {

    /**
     * 변경할 권한
     * @NotNull: null 값 불허 (GUEST 또는 ADMIN만 가능)
     */
    @NotNull(message = "권한은 필수입니다")
    private Role role;
}