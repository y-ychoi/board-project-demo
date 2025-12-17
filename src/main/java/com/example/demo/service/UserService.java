package com.example.demo.service;

import com.example.demo.dto.SignupRequestDto;
import com.example.demo.dto.UserListDto;
import com.example.demo.dto.UserSignupDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        List<User> users = userRepository.findAllByOrderByCreateDtDesc();

        // 현재 로그인한 사용자 ID 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();

        return users.stream()
                .map(UserListDto::new)
                .sorted((u1, u2) -> {
                    // 현재 사용자를 맨 위로
                    if (u1.getUserId().equals(currentUserId)) return -1;
                    if (u2.getUserId().equals(currentUserId)) return 1;
                    // 나머지는 가입일 기준 내림차순
                    return u2.getCreateDt().compareTo(u1.getCreateDt());
                })
                .collect(Collectors.toList());
    }

    /**
     * 회원의 권한을 변경 (관리자 페이지용)
     * @param userNo 권한을 변경할 회원 번호
     * @param role 변경할 권한 (GUEST 또는 ADMIN)
     */
    @Transactional
    public void updateUserRole(Long userNo, Role role) {
        User user = userRepository.findById(userNo)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
    
        user.updateRole(role); 
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
     * 사용자 ID로 User 엔티티 전체 조회 (REST API용)
     *
     * @param userId 로그인 ID
     * @return User 엔티티 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public User getUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
    }
    /**
     * REST API 회원가입 처리
     *
     * @param signupRequest REST API 회원가입 요청 DTO
     * @return 생성된 User 엔티티
     */
    @Transactional
    public User create(SignupRequestDto signupRequest) {
        // 1. 중복 아이디 체크
        if (userRepository.findByUserId(signupRequest.getUserId()).isPresent()) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }

        // 2. User 엔티티 생성
        User user = User.builder()
                .userId(signupRequest.getUserId())
                .userPw(passwordEncoder.encode(signupRequest.getPassword()))
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .role(Role.GUEST)
                .build();

        // 3. 저장 및 반환
        return userRepository.save(user);
    }
    
    /**
     * REST API용 모든 사용자 목록 조회 (User 엔티티 반환)
     *
     * @return User 엔티티 리스트
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsersForApi() {
        return userRepository.findAllByOrderByCreateDtDesc();
    }

    /**
     * 사용자 번호로 User 엔티티 조회 (REST API용)
     *
     * @param userNo 사용자 번호
     * @return User 엔티티
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public User getUserByUserNo(Long userNo) {
        return userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userNo));
    }

    /**
     * 아이디 중복 확인
     */
    @Transactional(readOnly = true)
    public boolean isUserIdDuplicated(String userId) {
            return userRepository.findByUserId(userId).isPresent();
    }
}