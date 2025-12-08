package com.example.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.service.UserService;
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
	        @RequestParam(name="userId") String userId,
	        @RequestParam(name="userPw") String userPw,
	        @RequestParam(name="userPw2") String userPw2, // 🚨 userPw2 필드 추가
	        @RequestParam(name="name") String name,
	        RedirectAttributes redirectAttributes 
	) {
	    // ----------------------------------------------------
	    // 1차 검증: 아이디 길이 및 형식 검증 (서버 측)
	    // ----------------------------------------------------
	    String regex = "^[a-zA-Z0-9]*$"; // 영문/숫자만 허용
	    
	    if (userId.length() < 3 || userId.length() > 15 || !userId.matches(regex)) {
	        redirectAttributes.addFlashAttribute("signupError", "아이디는 영문/숫자 3~15자만 사용 가능합니다.");
	        redirectAttributes.addFlashAttribute("prevUserId", userId);
	        redirectAttributes.addFlashAttribute("prevName", name);
	        return "redirect:/user/signup";
	    }

	    // 2차 검증: 비밀번호 일치 확인 (서버 측)
	    if (!userPw.equals(userPw2)) {
	        redirectAttributes.addFlashAttribute("signupError", "비밀번호와 비밀번호 확인 값이 일치하지 않습니다.");
	        redirectAttributes.addFlashAttribute("prevUserId", userId);
	        redirectAttributes.addFlashAttribute("prevName", name);
	        return "redirect:/user/signup";
	    }

	    // 🚨 3차 검증: 중복 확인 필수 조건 (클라이언트 측에서 isIdChecked 변수로 관리)
	    // 서버 측에서는 중복 확인 없이 통과되면 DB에서 최종적으로 Duplicate entry 오류가 발생합니다.
	    // 하지만, 현재 로직에서는 클라이언트가 중복 확인을 했다고 가정하고 서버는 최종 저장(create)만 시도합니다.
	    
	    try {
	        // 4차 검증: 아이디 중복 확인 및 저장 (UserService 내부에서 중복 체크 후 저장)
	        userService.create(userId, userPw, name);

	    } catch (IllegalStateException e) {
	        // 🚨 DB에 이미 존재하는 ID라면 (서버 측 중복 체크 실패)
	        redirectAttributes.addFlashAttribute("signupError", e.getMessage());
	        redirectAttributes.addFlashAttribute("prevUserId", userId);
	        redirectAttributes.addFlashAttribute("prevName", name);
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
