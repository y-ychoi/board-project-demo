package com.example.demo.service;

import com.example.demo.dto.UserListDto;
import com.example.demo.dto.UserSignupDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class UserService {

	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 새로운 회원을 등록하는 메서드 (수정됨)
     * @param signupDto 회원가입 정보 (아이디, 비밀번호, 이름, 이메일)
     * @return DB에 저장된 USER 엔티티 객체
     */
    @Transactional
    public User create(UserSignupDto signupDto) {

            // 1. 비밀번호 암호화(BCrypt)
            String encryptedPassword = passwordEncoder.encode(signupDto.getUserPw());

            // 2. User Entity 객체 생성 (@builder 패턴 활용)
            User user = User.builder()
                            .userId(signupDto.getUserId())
                            .userPw(encryptedPassword)  // 암호화된 비밀번호 저장
                            .name(signupDto.getName())
                            .email(signupDto.getEmail())
                            .role(Role.GUEST)  // 기본 권한은 GUEST로 설정
                            .build();

            // 3. Repository를 통해 데이터베이스에 저장
            userRepository.save(user);

            return user;
    }

    /**
     * 모든 회원 목록을 조회 (관리자 페이지용)
     * @return 회원 목록 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<UserListDto> getAllUsers() {
            // 1. Repository에서 모든 회원을 가입일 기준 최신순으로 조회
            List<User> users = userRepository.findAllByOrderByCreateDtDesc();

            // 2. Entity 리스트를 DTO 리스트로 변환
            // stream(): 리스트를 스트림으로 변환
            // map(): 각 User를 UserListDto로 변환
            // collect(): 다시 리스트로 모음
            return users.stream()
                            .map(UserListDto::new)  // user -> new UserListDto(user)와 동일
                            .collect(Collectors.toList());
    }

    /**
     * 회원의 권한을 변경 (관리자 페이지용)
     * @param userNo 권한을 변경할 회원 번호
     * @param role 변경할 권한 (GUEST 또는 ADMIN)
     */
    @Transactional
    public void updateUserRole(Long userNo, Role role) {
            // 1. 회원 번호로 회원 조회
            User user = userRepository.findById(userNo)
                            .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

            // 2. 권한 변경 (toBuilder()를 사용하여 불변 객체 수정)
            User updatedUser = user.toBuilder()
                            .role(role)
                            .build();

            // 3. 변경된 회원 정보 저장
            userRepository.save(updatedUser);
    }

    /**
     * userId(로그인 ID)로 User 엔티티의 PK(userNo)를 조회합니다.
     */
    @Transactional(readOnly = true)
    public Long getUserNoByUserId(String userId) {
            return userRepository.findByUserId(userId)
                            .orElseThrow(() -> new UsernameNotFoundException("작성자를 찾을 수 없습니다: " + userId))
                            .getUserNo();
    }

    /**
     * userId(로그인 ID)를 기반으로 회원의 이름을 조회합니다.
     */
    @Transactional(readOnly = true)
    public String getUserNameByUserId(String userId) {
            User user = userRepository.findByUserId(userId)
                            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

            return user.getName();
    }

    /**
     * 아이디 중복 확인
     */
    @Transactional(readOnly = true)
    public boolean isUserIdDuplicated(String userId) {
            return userRepository.findByUserId(userId).isPresent();
    }
}