package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.BoardListResponseDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.User;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.UserRepository;

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
	public Page<BoardListResponseDto> getBoardList(int page) {
	    
	    // 페이지 설정 (BoardSpecification 대신 PageRequest 직접 사용)
	    Pageable pageable = PageRequest.of(page, 10, Sort.by("boardNo").descending());
	    
	    Page<Board> boardPaging = boardRepository.findAll(pageable);
	    
	    // 🚨🚨🚨 1. dtoList 변수 선언 및 초기화 (스코프 시작) 🚨🚨🚨
	    List<BoardListResponseDto> dtoList = boardPaging.getContent().stream()
	    		.map(board -> {
	            // 1-1. 게시글 번호로 댓글 개수 조회
	            Long commentCount = commentRepository.countByBoardNo(board.getBoardNo());
	            
	            // 1-2. 작성자 정보 (수동 조인) 로드
	            User authorUser = userRepository.findById(board.getAuthorNo()).orElse(null);
	            // 1-3. DTO 빌더를 사용하여 객체 생성 및 반환
	            return BoardListResponseDto.builder()
	                    .boardNo(board.getBoardNo())
	                    .title(board.getTitle())
	                    .viewCnt(board.getViewCnt())
	                    .createDt(board.getCreateDt())
	                    .commentCount(commentCount) 
	                    .authorUser(authorUser)     
	                    .build();
	        }).collect(Collectors.toList()); // 🚨 2. dtoList 변수 할당 완료

	    // 🚨🚨🚨 3. dtoList 변수를 참조하여 Page 객체로 래핑하여 반환 🚨🚨🚨
	    return new PageImpl<>(dtoList, pageable, boardPaging.getTotalElements());
	}
	
	/**
	 * 게시글 상세 조회
	 * @param boardNo 조회할 게시글 번호
	 * @return Board 엔티티
	 */
	@Transactional
	public Board getBoardDetail(Long boardNo) {
	    
	    Board board = boardRepository.findById(boardNo)
	            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. (ID: " + boardNo + ")"));
	    

	    // 수동 조인 로직 (작성자 정보 로드) 
	    Long authorNo = board.getAuthorNo();
	    Optional<User> authorOptional = userRepository.findById(authorNo); 
	    
	    if (authorOptional.isPresent()) {
	        User author = authorOptional.get();
	        board.setAuthorUser(author); 
	    }
	    
	    // 3. 메서드가 종료될 때 @Transactional에 의해 UPDATE 쿼리가 실행됩니다.
	    return board;
	}
	
	/**
     * 특정 게시글의 조회수를 1 증가시킵니다.
     * @param board 조회수를 증가시킬 Board 엔티티
     */
    @Transactional // 🚨 데이터 변경이 일어나므로 @Transactional이 필요합니다.
    public void increaseViewCount(Board board) {
        // Board 엔티티의 viewCnt 필드를 1 증가시킵니다.
        board.setViewCnt(board.getViewCnt() + 1);
        
        // save() 메서드를 명시적으로 호출하지 않아도, 
        // @Transactional이 트랜잭션 종료 시 변경된 엔티티를 자동으로 DB에 반영합니다 (Dirty Checking).
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
    
	
}