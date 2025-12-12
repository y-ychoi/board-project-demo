package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.User;


//JpaRepository<Entity타입, PK타입> 상속
public interface UserRepository extends JpaRepository<User, Long> {

	// 사용자 ID로 회원 조회 (로그인용)
    Optional<User> findByUserId(String userId);

    // 회원 목록을 생성일 기준 내림차순으로 조회 (최신 가입자가 위로)
    // 관리자 페이지에서 회원 목록을 보여줄 때 사용
    List<User> findAllByOrderByCreateDtDesc();

}