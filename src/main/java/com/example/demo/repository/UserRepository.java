package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//JpaRepository<Entity타입, PK타입> 상속
public class UserRepository extends JpaRepository<User, Long> {
	
	// 사용자 ID(username)로 User 정보를 찾아오는 메서드 정의
	Optional<User> findByUsername(String username);
}
