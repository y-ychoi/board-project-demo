package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	// JpaRepository를 상속받으면 CRUD 및 페이징/정렬 기능이 자동으로 제공됩니다.
}