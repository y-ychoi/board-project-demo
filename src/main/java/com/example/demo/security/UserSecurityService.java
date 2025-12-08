package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority; 
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // 구현할 핵심 인터페이스
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service 
// UserDetailsService를 구현하여 Spring Security에게 DB에서 사용자 정보를 로드하는 방법을 알려줍니다.
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    // 👈 Spring Security가 로그인 시도 시 사용자 ID를 받아 호출하는 메서드입니다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // 1. UserRepository를 사용하여 DB에서 사용자 ID(username)로 User 엔티티를 조회합니다.
        Optional<User> _user = this.userRepository.findByUserId(username);
        
        // 2. 사용자가 존재하지 않는 경우
        if (_user.isEmpty()) {
            throw new UsernameNotFoundException("사용자 ID: " + username + "을(를) 찾을 수 없습니다.");
        }

        User user = _user.get();
        
        // 3. 사용자 권한(Role) 설정
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 권한 문자열(user.getRole())을 SimpleGrantedAuthority 객체로 변환하여 목록에 추가합니다.
        authorities.add(new SimpleGrantedAuthority(user.getRole()));

        // 4. Spring Security의 UserDetails 객체를 반환합니다.
        // Spring Security는 이 객체의 비밀번호(암호화된 userPw)와 로그인 시 입력된 평문 비밀번호를 비교합니다.
        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getUserPw(), authorities);
    }
}