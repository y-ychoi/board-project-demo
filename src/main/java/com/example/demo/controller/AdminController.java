package com.example.demo.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.UserListDto;
import com.example.demo.entity.Role;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    // GET /admin/users - 회원 관리 페이지
    @GetMapping("/users")
    public String userList(Model model) {
        List<UserListDto> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/user_list";
    }

    // POST /admin/users/{userNo}/role - 권한 변경
    @PostMapping("/users/{userNo}/role")
    public String updateUserRole(@PathVariable Long userNo,
                                @RequestParam Role role,
                                RedirectAttributes redirectAttributes) {
        userService.updateUserRole(userNo, role);
        // 이 줄을 제거: redirectAttributes.addFlashAttribute("message", "권한이 변경되었습니다.");
        return "redirect:/admin/users";
    }
}