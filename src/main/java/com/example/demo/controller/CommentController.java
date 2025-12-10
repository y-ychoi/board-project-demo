package com.example.demo.controller;

import com.example.demo.entity.Board;
import com.example.demo.exception.UnauthorizedAccessException;
import com.example.demo.repository.BoardRepository;
import com.example.demo.service.CommentService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CommentController {

    // 🚨 Service 주입 (권한 확인 및 삭제 로직을 위임)
    private final CommentService commentService;
    private final UserService userService;
    private final BoardRepository boardRepository;

    /**
     * 댓글 삭제 요청 처리 (GET 방식 사용)
     */
    @GetMapping("/comment/delete")
    public String commentDelete(@RequestParam("commentNo") Long commentNo, 
                                @RequestParam("boardNo") Long boardNo,
                                Principal principal) {

        // 1. 비로그인 상태 확인 (Principal 객체가 null이면 로그인 페이지로 유도)
        if (principal == null) {
            return "redirect:/user/login"; 
        }
        
        // 2. 현재 로그인 사용자 PK 조회
        Long currentUserNo = userService.getUserNoByUserId(principal.getName());

        try {
            // 3. Service 호출: 권한 확인 및 삭제 처리 (가장 중요한 부분)
            commentService.deleteComment(commentNo, currentUserNo);
            
        } catch (UnauthorizedAccessException e) {
            // 4. 권한 없음 예외 처리 (별도의 메시지 없이 상세 페이지로 리다이렉트)
            System.out.println("댓글 삭제 권한 없음: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // 5. 댓글이 존재하지 않는 경우 (예: 이미 삭제됨)
            System.out.println("댓글 조회 오류: " + e.getMessage());
        }
        
        // 6. 처리가 완료되면 게시글 상세 페이지로 돌아갑니다.
        return "redirect:/board/detail?id=" + boardNo;
    }
    

    /**
     * 1. 댓글 수정 처리 (POST - AJAX 요청을 받음)
     * 인라인 수정의 핵심: 클라이언트가 JS를 통해 수정된 내용을 POST 요청으로 보냅니다.
     * @param commentNo 수정할 댓글 PK
     * @param boardNo 리다이렉트용 게시글 PK
     * @param newContent 수정된 내용
     */
    @PostMapping("/comment/modify")
    // @ResponseBody를 사용하여 응답 상태(성공/실패)만 클라이언트에게 반환합니다.
    public @ResponseBody String commentModifyProcess(@RequestParam("commentNo") Long commentNo,
                                   @RequestParam("newContent") String newContent, 
                                   Principal principal) {
        
        if (principal == null) {
            // 401 Unauthorized 에러 코드 대신, 문자열 메시지 반환
            return "ERROR: LOGIN_REQUIRED"; 
        }
        
        Long currentUserNo = userService.getUserNoByUserId(principal.getName());

        try {
            // Service 호출: 권한 확인 및 내용 수정
            commentService.modifyComment(commentNo, newContent, currentUserNo);
            
        } catch (UnauthorizedAccessException e) {
            // 권한 없음
            return "ERROR: UNAUTHORIZED_ACCESS";
        } catch (IllegalArgumentException e) {
            // 댓글을 찾을 수 없음
            return "ERROR: COMMENT_NOT_FOUND";
        }
        
        // 수정 성공 메시지 반환
        return "SUCCESS";
    }
        

    /**
     * 댓글 생성 요청 처리 (POST)
     * URL 예시: /comment/create/123
     */
    @PostMapping("/comment/create/{boardNo}")
    public String createComment(@PathVariable("boardNo") Long boardNo,
                                @RequestParam("content") String content,
                                Principal principal) {
        
        // 1. 로그인 확인 (기존 로직 유지)
        if (principal == null) {
            return "redirect:/user/login"; 
        }
        
        // 2. 현재 로그인 사용자 PK 조회 (기존 로직 유지)
        Long authorNo = userService.getUserNoByUserId(principal.getName());

        // 🚨 3. Board 엔티티 조회 🚨
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글 번호에 해당하는 게시글을 찾을 수 없습니다."));

        // 🚨 4. Service 호출: Board 객체를 전달하도록 수정 🚨
        commentService.createComment(board, content, authorNo);

        // 5. 상세 페이지로 리다이렉트 (기존 로직 유지)
        return "redirect:/board/detail?id=" + boardNo;
    }
}