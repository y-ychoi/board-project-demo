package com.example.demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.BoardLike;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    /**
     * 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
     * 게시글 상세 페이지 로딩시 좋아요 버튼 상태 표시용
     *
     * @param boardNo 게시글 번호
     * @param userNo 사용자 번호
     * @return boolean 좋아요 여부 (true: 좋아요함, false: 안함)
     */
    boolean existsByBoard_BoardNoAndUser_UserNo(Long boardNo, Long userNo);

    /**
     * 특정 사용자가 특정 게시글에 누른 좋아요를 조회
     * 좋아요 토글 기능에서 기존 좋아요 삭제를 위해 객체 조회용
     *
     * @param boardNo 게시글 번호
     * @param userNo 사용자 번호
     * @return Optional<BoardLike> 좋아요 정보 (없으면 empty)
     */
    Optional<BoardLike> findByBoard_BoardNoAndUser_UserNo(Long boardNo, Long userNo);

    /**
     * 특정 게시글의 총 좋아요 수를 계산
     * 게시글 상세 페이지에서 좋아요 수 표시용
     *
     * @param boardNo 게시글 번호
     * @return long 해당 게시글의 좋아요 수
     */
    long countByBoard_BoardNo(Long boardNo);

    /**
     * 특정 게시글의 모든 좋아요를 삭제
     * 게시글 삭제시 관련 좋아요 데이터 정리용
     *
     * @param boardNo 게시글 번호
     */
    void deleteByBoard_BoardNo(Long boardNo);
}
