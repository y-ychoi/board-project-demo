package com.example.demo.service;

import com.example.demo.dto.CommentCreateRequestDto;
import com.example.demo.dto.CommentResponseDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.UnauthorizedAccessException;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.MaskingUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    /**
     * 댓글을 저장합니다.
     */
    @Transactional
    public Comment createComment(Board board, String content, Long authorNo) {
        
        Comment comment = Comment.builder()
                .board(board) // 게시글 번호
                .content(content) // 내용
                .authorNo(authorNo) // 작성자 번호
                .build();

        return commentRepository.save(comment);
    }
    
    /**
     * 특정 게시글의 댓글 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentList(Long boardNo) {
        
    	List<Comment> commentList = commentRepository.findAllByBoardBoardNoOrderByCreateDtAsc(boardNo);
        
        // 🚨🚨🚨 DTO 변환 및 작성자 정보 조회 로직 🚨🚨🚨
        List<CommentResponseDto> dtoList = commentList.stream()
        	    // 🚨🚨🚨 map의 람다 함수 전체를 중괄호 {}로 감싸야 합니다. 🚨🚨🚨
        	    .map(comment -> { // <--- 람다 시작 중괄호 추가
        	        
        	        // 1. 작성자 번호로 User 엔티티 조회 (authorNo)
        	        User authorUser = userRepository.findById(comment.getAuthorNo()).orElse(null);
        	        
        	        String originalName = authorUser != null ? authorUser.getName() : "탈퇴 회원";
        	        String originalUserId = authorUser != null ? authorUser.getUserId() : "deleted";
        	        Long authorPk = comment.getAuthorNo();
        	        
        	        // 💡 MaskingUtil을 사용하여 마스킹 처리
        	        String maskedName = MaskingUtil.maskName(originalName); 
        	        String maskedUserId = MaskingUtil.maskUserId(originalUserId);
        	        
        	        
        	        // 2. DTO 빌더를 사용하여 Comment 및 User 정보 조합
        	        // 🚨🚨🚨 return 키워드를 반드시 포함해야 합니다. 🚨🚨🚨
        	        return CommentResponseDto.builder()
        	                .commentNo(comment.getCommentNo())
        	                .content(comment.getContent())
        	                .createDt(comment.getCreateDt())
        	                .modifyDt(comment.getModifyDt())
        	                .authorName(maskedName)
        	                .authorUserId(maskedUserId)
        	                .authorNo(authorPk)
        	                .build();
        	    }) // <--- 람다 끝 중괄호 (중괄호가 이 위치에 있어야 함)
        	    .collect(Collectors.toList());


        return dtoList;
    }
    
    @Transactional
    public void deleteComment(Long commentNo, Long currentUserNo) {
        
        // 1. 댓글 엔티티 로드
        // 댓글이 없으면 IllegalArgumentException 발생 (Controller에서 처리됨)
        Comment comment = commentRepository.findById(commentNo)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. (ID: " + commentNo + ")"));
        
        // 2. 🚨🚨 보안 검사: 권한 확인 🚨🚨
        // 댓글 작성자의 PK와 현재 사용자의 PK를 비교합니다.
        if (!comment.getAuthorNo().equals(currentUserNo)) {
            // 권한이 없으면 사용자 정의 예외 발생
            throw new UnauthorizedAccessException("댓글 삭제 권한이 없습니다.");
        }
        
        // 3. 댓글 삭제
        commentRepository.delete(comment);
    }
    /**
     * 댓글 수정 로직 및 권한 확인
     * @param commentNo 수정할 댓글 PK
     * @param newContent 새로운 댓글 내용
     * @param currentUserNo 현재 로그인 사용자의 PK
     */
    @Transactional
    public void modifyComment(Long commentNo, String newContent, Long currentUserNo) {
        
        // 1. 댓글 엔티티 로드
        Comment comment = commentRepository.findById(commentNo)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. (ID: " + commentNo + ")"));
        
        // 2. 🚨 권한 확인: 현재 사용자 PK와 댓글 작성자 PK 비교 🚨
        if (!comment.getAuthorNo().equals(currentUserNo)) {
            // 권한이 없으면 Unchecked Exception 발생
            throw new UnauthorizedAccessException("댓글 수정 권한이 없습니다.");
        }
        
        // 3. 내용 업데이트 (Dirty Checking을 이용)
        comment.updateContent(newContent); 
        // Comment 엔티티에 updateContent(String content) 메서드가 정의되어 있어야 합니다.
        
        // (Transactional 어노테이션 덕분에 save() 호출 없이 트랜잭션 종료 시 자동 반영됨)
    }
    
    /**
     * REST API용 댓글 목록 조회
     *
     * @param boardNo 게시글 번호
     * @return List<Comment> 댓글 목록 (생성일시 오름차순)
     */
    @Transactional(readOnly = true)
    public List<Comment> getCommentsForApi(Long boardNo) {
        return commentRepository.findAllByBoardBoardNoOrderByCreateDtAsc(boardNo);
    }

    /**
     * REST API용 댓글 작성
     *
     * @param boardNo 게시글 번호
     * @param createRequest 댓글 작성 요청 DTO
     * @param authorNo 작성자 번호 (JWT에서 추출)
     * @return Comment 생성된 댓글 엔티티
     */
    @Transactional
    public Comment createCommentForApi(Long boardNo, CommentCreateRequestDto createRequest, Long authorNo) {
        // 게시글 존재 확인
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + boardNo));

        // 댓글 엔티티 생성
        Comment comment = Comment.builder()
                .content(createRequest.getContent())
                .board(board)
                .authorNo(authorNo)
                .build();

        return commentRepository.save(comment);
    }
    /**
     * REST API용 댓글 수정
     */
    public void updateCommentForApi(Long boardNo, Long commentNo, String content, String userId, Role userRole) {
        Comment comment = commentRepository.findById(commentNo)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 게시글 존재 확인
        if (!comment.getBoard().getBoardNo().equals(boardNo)) {
            throw new IllegalArgumentException("잘못된 게시글 번호입니다.");
        }

        // 권한 확인: 작성자만 수정 가능 (ADMIN도 수정 불가)
        User currentUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long currentUserNo = currentUser.getUserNo();
        if (!comment.getAuthorNo().equals(currentUserNo)) {
            throw new UnauthorizedAccessException("댓글 수정 권한이 없습니다.");
        }

        comment.setContent(content);
        commentRepository.save(comment);
    }

    /**
     * REST API용 댓글 삭제 (권한 체크 포함)
     *
     * @param boardNo 게시글 번호
     * @param commentNo 댓글 번호
     * @param currentUserId 현재 로그인한 사용자 ID
     * @param currentUserRole 현재 로그인한 사용자 권한
     */
    @Transactional
    public void deleteCommentForApi(Long boardNo, Long commentNo, String currentUserId, Role currentUserRole) {
        // 게시글 존재 확인
        boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + boardNo));

        // 댓글 존재 확인
        Comment comment = commentRepository.findById(commentNo)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + commentNo));

        // 댓글이 해당 게시글에 속하는지 확인
        if (!comment.getBoard().getBoardNo().equals(boardNo)) {
            throw new IllegalArgumentException("댓글이 해당 게시글에 속하지 않습니다");
        }

        // 권한 체크: 작성자 또는 ADMIN만 삭제 가능
        User author = userRepository.findById(comment.getAuthorNo())
                .orElseThrow(() -> new IllegalArgumentException("작성자를 찾을 수 없습니다"));

        boolean isAuthor = author.getUserId().equals(currentUserId);
        boolean isAdmin = currentUserRole == Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new IllegalStateException("댓글 삭제 권한이 없습니다");
        }

        commentRepository.delete(comment);
    }
}