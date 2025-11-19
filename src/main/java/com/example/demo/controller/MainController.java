package com.example.boardproject.controller; // 컨트롤러 패키지

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Spring에게 이 클래스가 요청을 처리하는 컨트롤러임을 알립니다.
public class MainController {
    
    // 사용자가 http://localhost:8080/ (루트 경로)로 GET 요청을 보낼 때 실행됩니다.
    @GetMapping("/")
    public String root() {
        // "index"라는 템플릿 파일(index.html)을 찾아 사용자에게 반환하라는 의미입니다.
        return "index"; 
    }
}