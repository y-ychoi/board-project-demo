package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글 번호를 기준으로 모든 댓글을 조회하는 쿼리 메서드를 정의합니다.
    List<Comment> findAllByBoardNoOrderByCreateDtAsc(Long boardNo);
    
    // 💡 참고: order by CreateDtAsc를 붙여 작성일 순으로 정렬합니다.
    // 2. 🚨🚨🚨 특정 게시글 번호에 해당하는 댓글의 개수를 세는 메서드 추가 🚨🚨🚨
    Long countByBoardNo(Long boardNo);
}