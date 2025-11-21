package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.User;


//JpaRepository<Entity타입, PK타입> 상속
public interface UserRepository extends JpaRepository<User, Long> {
	
	// 추상 메서드는 세미콜론(;)으로 끝납니다.
	Optional<User> findByUserId(String userId); 
}