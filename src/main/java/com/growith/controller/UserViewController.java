package com.growith.controller;

import com.growith.domain.post.dto.PostGetListResponse;
import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.service.PostService;
import com.growith.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserViewController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping("/users/mypage")
    public String myPage(HttpServletRequest request, Model model,@RequestParam(required = false) Long page,@PageableDefault(size = 10, sort = "{created_date}", direction = Sort.Direction.DESC) Pageable pageable) {

        HttpSession session = request.getSession(true);
        String userName = session.getAttribute("userName").toString();
        UserGetMyPageResponse user = userService.getMyPageUser(userName);

        Page<PostGetListResponse> posts = postService.getAllPostsByUserName(userName, pageable);

        if (page != null) {
            model.addAttribute("myPostBtn", true);
        } else {
            model.addAttribute("myPostBtn", false);
        }

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("numberOfPages", posts.getTotalPages());
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());
        return "users/mypage";
    }
}
