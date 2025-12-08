package com.example.demo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.dto.BoardListResponseDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.service.UserService;
import com.example.demo.util.MaskingUtil;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
	
	private final BoardService boardService;
	private final UserService userService;
	private final CommentService commentService;
	@GetMapping("/") 
	public String root(Principal principal, Model model) {
	    if (principal != null) {
	        String userId = principal.getName();
	        
	        // 🚨 UserService를 통해 DB에서 이름(name)을 조회하는 로직 추가
	        String userName = userService.getUserNameByUserId(userId); 
	        
	        // Model에 currentUserName을 담아 View로 전달
	        model.addAttribute("currentUserName", userName);
	    }
	    return "index"; // index.html 반환
	}

    // -------------------------------------------------------------------
    // 2. 목록 조회 ("/board/list") 처리 (list.html에 이름 표시용)
    // -------------------------------------------------------------------
	@GetMapping("/list")
	public String getBoardList(Model model, @RequestParam(value="page", defaultValue="0") int page, 
	                           Principal principal) { 
	    
	    // 1. 게시글 페이징 데이터 조회 및 작성자 정보 주입
	    Page<BoardListResponseDto> paging = boardService.getBoardList(page);
	    
	    // 2. 🚨 이름 조회 및 Model에 추가 로직 🚨
	    if (principal != null) {
	        String userId = principal.getName(); 
	        String userName = userService.getUserNameByUserId(userId); 
	        // View에서 사용할 현재 로그인 사용자의 이름을 Model에 담습니다.
	        model.addAttribute("currentUserName", userName); 
	    }
	    
	    // 3. 💡 MaskingUtil 클래스를 View에서 static 메서드로 호출할 수 있도록 Model에 추가
	    model.addAttribute("MaskingUtil", MaskingUtil.class);
	    
	    // 4. 🚨 중복 없이 페이징 객체를 Model에 담습니다.
	    model.addAttribute("boardPaging", paging);
	    
	    return "board/list"; // board/list.html 반환
	}
	
	/**
	 * 게시글 상세 조회 화면
	 * @param boardNo 조회할 게시글 번호 (URL 쿼리 파라미터 id로 받음)
	 * @param model Thymeleaf로 데이터를 전달하는 객체
	 * @return board/detail.html 템플릿 이름
	 */
	// BoardController.java (getBoardDetail 메서드 수정)

	@GetMapping("/detail")
	public String getBoardDetail(@RequestParam("id") Long boardNo, Model model, Principal principal) { 
	    
	    // 1. 게시글 조회 (board 엔티티를 영속성 컨텍스트에 로드)
	    Board board = boardService.getBoardDetail(boardNo); 
	    
	    // 2. 🚨 조회수 증가 호출 (별도의 트랜잭션으로 처리) 🚨
	    //    이 시점에 board 엔티티가 변경됨
	    boardService.increaseViewCount(board); 
	    
	    // 3. 댓글 목록 조회
	    List<Comment> commentList = commentService.getCommentList(boardNo); 
	    
	    //모델에 담기
	    model.addAttribute("board", board);
	    model.addAttribute("commentList", commentList);
	    
	    // 3. 현재 로그인 사용자 정보도 Model에 담기 (선택적)
	    if (principal != null) {
	        model.addAttribute("currentUserId", principal.getName());
	    }
	    
	    return "board/detail";
	}

	
	@GetMapping("/create")
	public String boardCreate() {
	    // templates/board/create_form.html 파일을 반환합니다.
	    return "board/create_form"; 
	}
	/**
     * 게시글 작성 폼에서 전송된 데이터를 DB에 저장합니다.
     * @param title 폼 데이터 (제목)
     * @param content 폼 데이터 (내용)
     * @param principal Spring Security가 제공하는 현재 로그인 사용자 객체
     * @return 저장 후 게시글 목록으로 리다이렉트
     */
	@PostMapping("/create")
	public String createPost(
	        @RequestParam("title") String title,
	        @RequestParam("content") String content,
	        Principal principal // 현재 로그인된 사용자 정보
	) {
	    // 1. Principal 객체에서 현재 로그인된 사용자의 ID (문자열)를 가져옵니다.
	    String userId = principal.getName(); 
	    
	    // 2. 🚨 UserService를 통해 userId를 사용하여 실제 userNo(PK)를 가져옵니다.
	    //    이 코드로 인해 이전 경고가 사라지고 작성자 연결이 완성됩니다.
	    Long authorNo = userService.getAuthorNoByUserId(userId); 

	    // 3. Service에 authorNo를 전달하여 게시글을 저장합니다.
	    boardService.createPost(title, content, authorNo);

	    return "redirect:/board/list";
	}
	@GetMapping("/modify")
	public String boardModify(@RequestParam("id") Long boardNo, Principal principal, Model model) {
	    // ... (권한 확인 로직 생략)
	    
	    // 🚨 폼을 보여주기 위해 Model에 데이터를 담습니다. 🚨
	    Board board = boardService.getBoardDetail(boardNo);
	    model.addAttribute("board", board);
	    
	    // 폼을 반환합니다.
	    return "board/modify_form"; 
	}
    
    // 2. 📝 수정 데이터를 전송할 때 (폼 제출 시) -> POST 요청 처리

	@PostMapping("/modify")
	public String boardModifyProcess(@RequestParam("id") Long boardNo, 
	                                 @RequestParam("title") String title,
	                                 @RequestParam("content") String content,
	                                 Principal principal) {
	    
	    // 1. 게시글 조회 (권한 검사 및 수정 대상 엔티티 가져오기)
	    Board board = boardService.getBoardDetail(boardNo);
	    Long currentAuthorNo = userService.getAuthorNoByUserId(principal.getName());

	    // 2. 🚨🚨 보안 검사: 로그인 사용자와 작성자 비교
	    if (!board.getAuthorNo().equals(currentAuthorNo)) {
	        // 권한이 없으면 상세 페이지로 리다이렉트
	        return "redirect:/board/detail?id=" + boardNo; 
	    }
	    
	    // 3. 💡 BoardService 호출: DB에 수정 내용을 반영합니다.
	    boardService.modifyPost(board, title, content); // DB UPDATE 실행
	    
	    // 4. 성공 후 수정된 상세 페이지로 리다이렉트합니다.
	    return "redirect:/board/detail?id=" + boardNo;
	    // 🚨 이전의 두 개의 return 문은 삭제되었습니다.
	}
	
	@GetMapping("/delete")
	public String boardDelete(@RequestParam("id") Long boardNo, Principal principal) {
	    
	    // 1. 게시글 조회 (권한 검사를 위해 엔티티를 가져옵니다.)
	    Board board = boardService.getBoardDetail(boardNo);
	    
	    // 2. 현재 로그인 사용자 확인
	    Long currentAuthorNo = userService.getAuthorNoByUserId(principal.getName());

	    // 3. 🚨🚨 보안 검사: 권한 없음 (로그인 사용자와 작성자 비교)
	    if (!board.getAuthorNo().equals(currentAuthorNo)) {
	        // 권한이 없으면 상세 페이지로 리다이렉트
	        return "redirect:/board/detail?id=" + boardNo; 
	    }
	    
	    // 4. 💡 BoardService 호출: DB에서 게시글을 삭제합니다.
	    boardService.deletePost(board);
	    
	    // 5. 성공 후 목록 페이지로 리다이렉트합니다.
	    return "redirect:/board/list";
	}
	
	@PostMapping("/comment/create/{boardNo}")
    public String createComment(
            @PathVariable("boardNo") Long boardNo, // URL 경로에서 게시글 번호를 받음
            @RequestParam("content") String content,
            Principal principal) {
        
        // 1. 로그인 사용자 확인 (로그인 필수)
        if (principal == null) {
            return "redirect:/user/login"; 
        }

        // 2. 작성자 userNo 조회
        Long authorNo = userService.getAuthorNoByUserId(principal.getName());
        
        // 3. Service 호출 및 저장
        commentService.createComment(boardNo, content, authorNo);

        // 4. 저장 후 해당 게시글 상세 페이지로 리다이렉트
        return "redirect:/board/detail?id=" + boardNo;
    }
}