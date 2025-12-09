package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.BoardDetailResponseDto;
import com.example.demo.dto.BoardListResponseDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.User;
import com.example.demo.exception.UnauthorizedAccessException;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.MaskingUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	

	/**
	 * 게시글 전체 목록 조회 및 페이징 처리
	 * @param page 현재 페이지 번호 (0부터 시작)
	 * @return Page<Board> 객체 (게시글 목록 및 페이징 정보 포함)
	 */
	@Transactional(readOnly = true)
	// 🚨🚨🚨 currentUserId 파라미터를 사용합니다. 🚨🚨🚨
	public Page<BoardListResponseDto> getBoardList(int page, String currentUserId) { 
	    
	    // 페이지 설정 (PageRequest 직접 사용)
	    Pageable pageable = PageRequest.of(page, 20, Sort.by("boardNo").descending());
	    
	    Page<Board> boardPaging = boardRepository.findAll(pageable);
	    
	    List<BoardListResponseDto> dtoList = boardPaging.getContent().stream()
	            .map(board -> {
	                Long commentCount = commentRepository.countByBoardNo(board.getBoardNo());
	                
	                // 1. 작성자 정보 로드 (User Entity는 Service 내부에서만 사용)
	                User authorUser = userRepository.findById(board.getAuthorNo()).orElse(null);
	                
	                // 탈퇴 회원 처리 및 원본 데이터 준비
	                String originalName = authorUser != null ? authorUser.getName() : "탈퇴 회원";
	                String originalUserId = authorUser != null ? authorUser.getUserId() : "deleted";
	               
	                String finalName = MaskingUtil.maskName(originalName);
	                String finalUserId = MaskingUtil.maskUserId(originalUserId);

	                // 3. DTO 빌더를 사용하여 객체 생성 및 반환
	                return BoardListResponseDto.builder()
	                        .boardNo(board.getBoardNo())
	                        .title(board.getTitle())
	                        .viewCnt(board.getViewCnt())
	                        .createDt(board.getCreateDt())
	                        .commentCount(commentCount) 
	                        
	                        //finalName/finalUserId 주입
	                        .authorName(finalName) 
	                        .authorUserId(finalUserId)
	                        .authorNo(board.getAuthorNo())
	                        .build();
	            })
	            .collect(Collectors.toList()); 

	    return new PageImpl<>(dtoList, pageable, boardPaging.getTotalElements());
	}
	
	/**
	 * 게시글 상세 조회 및 DTO 변환
	 * @param boardNo 조회할 게시글 번호
	 * @return BoardDetailResponseDto
	 */
	@Transactional // 조회수 증가 로직 때문에 @Transactional 유지
	public BoardDetailResponseDto getBoardDetail(Long boardNo) {
		
		// 1. Board 엔티티 로드 (조회수 증가 로직을 위해)
		Board board = boardRepository.findById(boardNo)
	 			.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. (ID: " + boardNo + ")"));
		
		// 2. 조회수 증가 (Dirty Checking)
		board.setViewCnt(board.getViewCnt() + 1);
		
		// 3. 작성자 정보 로드
		Long authorNo = board.getAuthorNo();
	 	User authorUser = userRepository.findById(authorNo).orElse(null);
		
		// 4. 탈퇴 회원 처리 및 마스킹
		String originalName = authorUser != null ? authorUser.getName() : "탈퇴 회원";
		String originalUserId = authorUser != null ? authorUser.getUserId() : "deleted";
		
        // 🚨🚨🚨 마스킹 적용 🚨🚨🚨
        // 상세 페이지도 목록 페이지와 마찬가지로 보안 일관성을 위해 무조건 마스킹을 적용합니다.
		String finalName = MaskingUtil.maskName(originalName);
		String finalUserId = MaskingUtil.maskUserId(originalUserId);

		
		// 5. DTO로 변환하여 반환
		return BoardDetailResponseDto.builder()
				.boardNo(board.getBoardNo())
				.title(board.getTitle())
				.content(board.getContent())
				// 🚨 증가된 viewCnt 사용
				.viewCnt(board.getViewCnt()) 
				.createDt(board.getCreateDt())
				.modifyDt(board.getModifyDt())
				
				// 🚨 마스킹된 작성자 정보 주입
				.authorName(finalName)
				.authorUserId(finalUserId)
				.authorNo(board.getAuthorNo())
				.build();
	}
	
	/**
     * 새로운 게시글을 생성하고 저장합니다.
     * @param title 제목
     * @param content 내용
     * @param authorNo 현재 로그인한 사용자(작성자)의 userNo
     */
    @Transactional // 🚨 DB에 저장하는 작업이므로 트랜잭션을 적용합니다.
    public void createPost(String title, String content, Long authorNo) {
        
        // 1. Board 엔티티 객체 생성 (Lombok @Builder 활용)
        Board board = Board.builder()
                .title(title)
                .content(content)
                .authorNo(authorNo) // 👈 작성자 ID (userNo) 저장
                .viewCnt(0) // 조회수는 0으로 초기화
                // createDt와 modifyDt는 BaseEntity Auditing으로 자동 처리됨
                .build();
        
        // 2. Repository를 통해 MySQL DB에 저장
        this.boardRepository.save(board);
    }
    
    @Transactional // 🚨 DB 수정 작업이므로 트랜잭션을 적용합니다.
    public void modifyPost(Board board, String title, String content) {
        
        // Board 엔티티의 update 메서드를 호출하여 필드를 변경합니다.
        board.update(title, content); 
    }
    
    /**
     * 게시글을 삭제합니다.
     * @param board 삭제할 Board 엔티티 객체
     */
    @Transactional // 🚨 DB 삭제 작업이므로 트랜잭션을 적용합니다.
    public void deletePost(Board board) {
        // Repository를 사용하여 해당 Board 엔티티를 삭제합니다.
        this.boardRepository.delete(board);
        // 트랜잭션 종료 시 DELETE 쿼리가 실행됩니다.
    }
    /**
     * 게시글 수정/삭제를 위해 엔티티를 로드하고, 권한을 확인합니다.
     * @param boardNo 수정할 게시글 번호
     * @param currentUserNo 현재 로그인한 사용자의 PK
     * @return Board 엔티티 (권한이 확인된 경우)
     */
    @Transactional(readOnly = true)
    public Board getAuthorizedBoard(Long boardNo, Long currentUserNo) {
        // 1. Board 엔티티 로드
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. (ID: " + boardNo + ")"));

        // 2. 🚨 권한 확인
        if (!board.getAuthorNo().equals(currentUserNo)) {
            // 현재 사용자의 PK와 게시글 작성자의 PK가 다르면 예외 발생
            throw new UnauthorizedAccessException("수정/삭제 권한이 없습니다.");
        }
        
        return board;
    }
	
}