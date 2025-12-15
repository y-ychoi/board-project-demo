package com.example.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.UserSignupDto;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor // UserService를 주입받기 위해
public class UserController {
	
	private final UserService userService; // 회원가입 로직을 가진 서비스 주입
	
	//Get 요청 : 회원가입 폼 화면
	@GetMapping("/user/signup")
	public String signUp() {
		// templates 폴더의 "signup_form.html"을 찾아 반환
		return "signup_form";
	}
	
	// UserController.java (POST /user/signup) 수정

	@PostMapping("/user/signup")
	public String signup(
			@Valid @ModelAttribute UserSignupDto signupDto,  // DTO로 변경
	        BindingResult bindingResult,  // 검증 결과를 담는 객체
	        @RequestParam(name="userPw2") String userPw2,  // 비밀번호 확인은 별도로 받음
	        RedirectAttributes redirectAttributes 
	) {
	    // ----------------------------------------------------
	    // 1차 검증: 아이디 길이 및 형식 검증 (서버 측)
	    // ----------------------------------------------------
		// 1. 입력값 검증 오류가 있는 경우
	    if (bindingResult.hasErrors()) {
	        // 첫 번째 오류 메시지를 가져옴
	        String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
	        redirectAttributes.addFlashAttribute("signupError", errorMessage);
	        redirectAttributes.addFlashAttribute("prevUserId", signupDto.getUserId());
	        redirectAttributes.addFlashAttribute("prevName", signupDto.getName());
	        redirectAttributes.addFlashAttribute("prevEmail", signupDto.getEmail());
	        return "redirect:/user/signup";
	    }

	 // 2. 아이디 형식 검증 (영문/숫자 3~15자)
	    String regex = "^[a-zA-Z0-9]*$";
	    if (signupDto.getUserId().length() < 3 || signupDto.getUserId().length() > 15
	            || !signupDto.getUserId().matches(regex)) {
	        redirectAttributes.addFlashAttribute("signupError", "아이디는 영문/숫자 3~15자만 사용 가능합니다.");
	        redirectAttributes.addFlashAttribute("prevUserId", signupDto.getUserId());
	        redirectAttributes.addFlashAttribute("prevName", signupDto.getName());
	        redirectAttributes.addFlashAttribute("prevEmail", signupDto.getEmail());
	        return "redirect:/user/signup";
	    }

	    // 3. 비밀번호 일치 확인
	    if (!signupDto.getUserPw().equals(userPw2)) {
	        redirectAttributes.addFlashAttribute("signupError", "비밀번호와 비밀번호 확인 값이 일치하지 않습니다.");
	        redirectAttributes.addFlashAttribute("prevUserId", signupDto.getUserId());
	        redirectAttributes.addFlashAttribute("prevName", signupDto.getName());
	        redirectAttributes.addFlashAttribute("prevEmail", signupDto.getEmail());
	        return "redirect:/user/signup";
	    }
	    
	    try {
	    	// 4. 회원가입 처리 (DTO를 Service로 전달)
	        userService.create(signupDto);

	    } catch (IllegalStateException e) {
	        // 중복된 아이디인 경우
	        redirectAttributes.addFlashAttribute("signupError", e.getMessage());
	        redirectAttributes.addFlashAttribute("prevUserId", signupDto.getUserId());
	        redirectAttributes.addFlashAttribute("prevName", signupDto.getName());
	        redirectAttributes.addFlashAttribute("prevEmail", signupDto.getEmail());
	        return "redirect:/user/signup";
	    }

	    // 5. 성공 시 로그인 페이지로 이동
	    return "redirect:/user/login"; 
	    }
	   

	@GetMapping("/user/login")
    public String login() {
        // templates 폴더의 "login_form.html"을 찾아 반환합니다.
        return "login_form"; 
    }
	
	/**
	 * 아이디 중복 확인 API (AJAX 호출용)
	 * @param userId 확인할 로그인 ID
	 * @return "true" (중복) 또는 "false" (사용 가능)
	 */
	@GetMapping("/user/checkId")
	@ResponseBody // 👈 메서드의 반환 값이 View 이름이 아닌, HTTP 응답 본문(Body)으로 직접 사용됨을 명시
	public String checkUserIdDuplication(@RequestParam("userId") String userId) {
	    
	    boolean isDuplicated = userService.isUserIdDuplicated(userId);
	    
	    // 결과를 문자열 ("true" 또는 "false")로 반환합니다.
	    return String.valueOf(isDuplicated);
	}

}
