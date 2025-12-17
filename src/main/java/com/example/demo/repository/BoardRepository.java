package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	// JpaRepository를 상속받으면 CRUD 및 페이징/정렬 기능이 자동으로 제공됩니다.
	/**
	 * 게시글 목록을 생성일 기준 내림차순으로 페이징 조회
	 *
	 * @param pageable 페이징 정보 (page, size, sort)
	 * @return Page<Board> 페이징된 게시글 목록
	 */
	Page<Board> findAllByOrderByCreateDtDesc(Pageable pageable);
}