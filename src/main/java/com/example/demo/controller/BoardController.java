package com.example.demo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.dto.BoardDetailResponseDto;
import com.example.demo.dto.BoardListResponseDto;
import com.example.demo.dto.CommentResponseDto;
import com.example.demo.entity.Board;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.service.UserService;


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
	        
	        // Model 에 currentUserName을 담아 View로 전달
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
	    
	    // 💡 1. 현재 로그인된 사용자의 ID( user name )를 가져옵니다. 
	    // Service 에서 마스킹 예외 처리에 사용됩니다.
	    String currentUserId = principal != null ? principal.getName() : null;

	    // 2. Service 호출 시 현재 사용자 ID를 전달합니다.
	    Page<BoardListResponseDto> paging = boardService.getBoardList(page, 20,currentUserId); // 🚨 currentUserId 파라미터 추가

	    // 3. View 에서 사용할 현재 로그인 사용자의 이름을 Model 에 담습니다. (유지)
	    if (principal != null) {
	        // String userId = principal.getName(); // 이미 위에서 currentUserId로 조회했습니다.
	        String userName = userService.getUserNameByUserId(currentUserId); 
	        model.addAttribute("currentUserName", userName); 
	    }
	    
	    // 4. 중복 없이 페이징 객체를 Model 에 담습니다.
	    model.addAttribute("boardPaging", paging);
	    
	    return "board/list"; // board/list.html 반환
	}
	
	/**
	 * 게시글 상세 조회 화면
	 * @param boardNo 조회할 게시글 번호 (URL 쿼리 파라미터 id 로 받음)
	 * @param model Thymeleaf로 데이터를 전달하는 객체
	 * @return board/detail.html 템플릿 이름
	 */
	// BoardController.java (getBoardDetail 메서드 수정)

	@GetMapping("/detail")
	public String getBoardDetail(@RequestParam("id") Long boardNo, Model model, Principal principal) { 
	    
	    // 1. 게시글 조회 (DTO 반환, Service 내부에서 조회수 증가까지 처리됨)
	    BoardDetailResponseDto boardDetail = boardService.getBoardDetail(boardNo); // 🚨 DTO 반환

	    // 2. 🚨 조회수 증가 호출 제거 🚨
	    //    boardService.increaseViewCount(board); // <-- 이 줄은 삭제합니다.
	    

		// 3. 댓글 목록 조회 (기존 로직 유지)
	    //    댓글은 BoardDetailResponseDto에 포함시키지 않고 별도로 조회합니다.
	    List<CommentResponseDto> commentList = commentService.getCommentList(boardNo);
	    
	    // 모델에 담기
	    model.addAttribute("board", boardDetail); // 🚨 DTO를 'board'라는 이름으로 Model에 담습니다.
	    model.addAttribute("commentList", commentList);
	    
	    // 4. 현재 로그인 사용자 정보도 Model 에 담기 (기존 로직 유지)
	    if (principal != null) {
	        String userId = principal.getName();
            
            // 🚨🚨🚨 UserService를 통해 현재 로그인 사용자의 PK(userNo)를 조회 🚨🚨🚨
	        Long currentUserNo = userService.getUserNoByUserId(userId); 

            // Model에 PK를 담아 View로 전달
	        model.addAttribute("currentUserNo", currentUserNo); 
            
            // 기존에 Model에 currentUserId를 담는 로직이 있었다면 유지
            model.addAttribute("currentUserId", userId); 
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
	    Long authorNo = userService.getUserNoByUserId(userId); 

	    // 3. Service에 authorNo를 전달하여 게시글을 저장합니다.
	    boardService.createPost(title, content, authorNo);

	    return "redirect:/board/list";
	}
	@GetMapping("/modify")
	public String boardModify(@RequestParam("id") Long boardNo, Principal principal, Model model) {
	    // ... (권한 확인 로직 생략)
	    
	    // 🚨 폼을 보여주기 위해 Model에 데이터를 담습니다. 🚨
		BoardDetailResponseDto boardDetail = boardService.getBoardDetail(boardNo);
	    model.addAttribute("board", boardDetail);
	    
	    // 폼을 반환합니다.
	    return "board/modify_form"; 
	}
    
    // 2. 📝 수정 데이터를 전송할 때 (폼 제출 시) -> POST 요청 처리

	@PostMapping("/modify")
	public String boardModifyProcess(@RequestParam("id") Long boardNo, 
	                                 @RequestParam("title") String title,
	                                 @RequestParam("content") String content,
	                                 Principal principal) {
	    
	    // 1. 현재 로그인한 사용자의 PK를 조회합니다.
	    Long currentAuthorNo = userService.getUserNoByUserId(principal.getName());

	    // 2. 🚨🚨 수정: DTO 대신 Service의 권한 검사 메서드를 호출하여 Board 엔티티를 로드 🚨🚨
	    //    이 시점에서 이미 권한 검사가 완료되며, 권한이 없으면 예외(UnauthorizedAccessException) 발생
	    Board board = boardService.getAuthorizedBoard(boardNo, currentAuthorNo); 
	    
	    // 3. 💡 BoardService 호출: DB에 수정 내용을 반영합니다.
	    boardService.modifyPost(board, title, content); // DB UPDATE 실행
	    
	    // 4. 성공 후 수정된 상세 페이지로 리다이렉트합니다.
	    return "redirect:/board/detail?id=" + boardNo;
	}
	
	@GetMapping("/delete")
	public String boardDelete(@RequestParam("id") Long boardNo, Principal principal) {
	    
	    // 1. 현재 로그인한 사용자의 PK를 조회합니다.
	    //    Principal 객체가 null이 될 수 있으므로, Spring Security 설정에 따라 처리 필요
	    if (principal == null) {
	        // 비로그인 상태일 경우 로그인 페이지 또는 상세 페이지로 리다이렉트
	        return "redirect:/board/detail?id=" + boardNo; 
	    }
	    Long currentUserNo = userService.getUserNoByUserId(principal.getName());

	    // 2. 🚨🚨 수정: Service의 권한 검사 메서드를 호출하여 Board 엔티티를 로드 🚨🚨
	    //    이 시점에서 Service가 권한을 검사하고, 권한이 없으면 UnauthorizedAccessException을 던집니다.
	    //    컴파일 오류(Type mismatch)가 해결됩니다.
	    Board board = boardService.getAuthorizedBoard(boardNo, currentUserNo);
	    
	    // 3. 💡 BoardService 호출: DB에서 게시글을 삭제합니다.
	    boardService.deletePost(board);
	    
	    // 4. 성공 후 목록 페이지로 리다이렉트합니다.
	    return "redirect:/board/list";
	}
	
	
}