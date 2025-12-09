package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

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
	 * 새로운 회원을 등록하는 메서드
	 * @param userId - 로그인 ID
	 * @param userPw - 평문 비밀번호 (암호화 대상)
	 * @param name - 사용자 이름
	 * @return DB에 저장된 USER 엔티티 객체
	 */
	@Transactional
	public User create(String userId, String userPw, String name) {
		
		// 1. 비밀번호 암호화(BCrypt)
		String encryptedPassword = passwordEncoder.encode(userPw);
		
		// 2. User Entity 객체 생성 (@builder 패턴 활용)
		User user = User.builder()
				.userId(userId)
				.userPw(encryptedPassword)// 암호화된 비밀번호 저장
				.name(name)
				.role("ROLE_USER") // 기본 권한 부여
				.build();
		
		// 3. Repository 를 통해 데이터베이스에 저장
		userRepository.save(user);
		
		return user;
	}	
	
	
	/**
	 * userId(로그인 ID)로 User 엔티티의 PK(userNo)를 조회합니다.
	 */
	@Transactional(readOnly = true)
	public Long getUserNoByUserId(String userId) {
	    // userRepository는 findByUserId()를 제공합니다.
	    // Optional<User>를 받아 게시글 작성자 PK(userNo)를 추출합니다.
	    return userRepository.findByUserId(userId)
	        // 사용자를 찾을 수 없으면 예외 발생
	        .orElseThrow(() -> new UsernameNotFoundException("작성자를 찾을 수 없습니다: " + userId))
	        .getUserNo(); // 👈 조회된 User 객체에서 userNo(PK)를 반환
	}
	/**
	 * userId(로그인 ID)를 기반으로 회원의 이름을 조회합니다.
	 * @param userId 현재 로그인된 사용자의 ID (Principal.getName())
	 * @return 회원의 이름 (name)
	 */
	@Transactional(readOnly = true)
	public String getUserNameByUserId(String userId) {
	    
	    // UserRepository를 사용하여 userId로 User 엔티티를 조회합니다.
	    User user = userRepository.findByUserId(userId)
	        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
	    
	    // 🚨 User 엔티티에서 name 필드만 반환합니다.
	    return user.getName();
	}
	
	/**
	 * 아이디 중복 확인
	 * @param userId 확인할 로그인 ID (String)
	 * @return true: 이미 사용 중, false: 사용 가능
	 */
	@Transactional(readOnly = true)
	public boolean isUserIdDuplicated(String userId) {
	    // Repository 의 findByUserId 메서드를 사용하여 DB에 해당 ID가 있는지 확인합니다.
	    return userRepository.findByUserId(userId).isPresent();
	}

}
