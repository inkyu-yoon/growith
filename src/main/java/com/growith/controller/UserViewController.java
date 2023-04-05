package com.growith.controller;

import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserViewController {

    private final UserService userService;

    @GetMapping("/users/mypage")
    public String myPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(true);
        UserGetMyPageResponse user = userService.getMyPageUser(session.getAttribute("userName").toString());
        model.addAttribute("user", user);
        return "/users/mypage";
    }
}
