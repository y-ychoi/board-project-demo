package com.example.demo.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.BoardLikeStatusDto;
import com.example.demo.dto.BoardLikeToggleDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.BoardLike;
import com.example.demo.entity.User;
import com.example.demo.repository.BoardLikeRepository;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * 게시글 좋아요 비즈니스 로직 처리 서비스
 */
@Service
@RequiredArgsConstructor
public class BoardLikeService {

    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * 좋아요 토글 (추가/취소)
     *
     * @param boardNo 게시글 번호
     * @param userNo 사용자 번호
     * @return BoardLikeToggleDto 토글 결과 (좋아요 상태, 총 개수)
     */
    @Transactional
    public BoardLikeToggleDto toggleLike(Long boardNo, Long userNo) {
        // 1. 게시글 존재 여부 확인
        Board board = boardRepository.findById(boardNo)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 2. 사용자 존재 여부 확인
        User user = userRepository.findById(userNo)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 3. 기존 좋아요 확인
        Optional<BoardLike> existingLike = boardLikeRepository
            .findByBoard_BoardNoAndUser_UserNo(boardNo, userNo);

        boolean isLiked;
        if (existingLike.isPresent()) {
            // 4-1. 좋아요 취소
            boardLikeRepository.delete(existingLike.get());
            isLiked = false;
        } else {
            // 4-2. 좋아요 추가
            BoardLike newLike = new BoardLike(board, user);
            boardLikeRepository.save(newLike);
            isLiked = true;
        }

        // 5. 실제 좋아요 수 계산 (정확한 값)
        int actualLikeCount = (int) boardLikeRepository.countByBoard_BoardNo(boardNo);

        // 6. 게시글의 좋아요 수 업데이트 (캐시용)
        board.setLikeCount(actualLikeCount);
        boardRepository.save(board);

        return new BoardLikeToggleDto(isLiked, actualLikeCount);
    }

    /**
     * 좋아요 상태 조회
     *
     * @param boardNo 게시글 번호
     * @param userNo 사용자 번호
     * @return BoardLikeStatusDto 현재 좋아요 상태 (좋아요 여부, 총 개수)
     */
    @Transactional(readOnly = true)
    public BoardLikeStatusDto getLikeStatus(Long boardNo, Long userNo) {
        // 1. 현재 사용자의 좋아요 여부 확인
        boolean isLiked = boardLikeRepository
            .existsByBoard_BoardNoAndUser_UserNo(boardNo, userNo);

        // 2. 총 좋아요 수 계산
        int likeCount = (int) boardLikeRepository.countByBoard_BoardNo(boardNo);

        return new BoardLikeStatusDto(isLiked, likeCount);
    }
}