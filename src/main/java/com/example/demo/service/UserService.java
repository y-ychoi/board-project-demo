package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
		
		// 3. Repository를 통해 데이터베이스에 저장
		this.userRepository.save(user);
		
		return user;
	}		

}
