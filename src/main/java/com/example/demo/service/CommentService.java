package com.example.demo.service;

import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    /**
     * 댓글을 저장합니다.
     */
    @Transactional
    public Comment createComment(Long boardNo, String content, Long authorNo) {
        
        Comment comment = Comment.builder()
                .boardNo(boardNo) // 게시글 번호
                .content(content) // 내용
                .authorNo(authorNo) // 작성자 번호
                .build();

        return commentRepository.save(comment);
    }
    
    /**
     * 특정 게시글의 댓글 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<Comment> getCommentList(Long boardNo) {
        // Repository에서 정의한 쿼리 메서드 사용
        return commentRepository.findAllByBoardNoOrderByCreateDtAsc(boardNo);
    }
}