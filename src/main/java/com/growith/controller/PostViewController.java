package com.growith.controller;

import com.growith.domain.post.Category;
import com.growith.domain.post.dto.PostGetListResponse;
import com.growith.domain.post.dto.PostGetResponse;
import com.growith.domain.user.oauth.UserProfile;
import com.growith.global.util.CookieUtil;
import com.growith.service.post.PostService;
import com.growith.service.user.UserJoinService;
import com.growith.service.webclient.WebClientService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.growith.global.util.constant.CookieConstants.COOKIE_AGE;
import static com.growith.global.util.constant.CookieConstants.JWT_COOKIE_NAME;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PostViewController {

    private final PostService postService;

    @GetMapping("/posts/qna")
    public String postsQNA(Model model, @PageableDefault(size = 10, sort = "{created_date}",direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetListResponse> posts = postService.getAllPostsByCategory(Category.QNA,pageable);

        model.addAttribute("posts", posts);
        model.addAttribute("numberOfPages", posts.getTotalPages());
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());
        return "posts/qna";
    }


    @GetMapping("/posts/write")
    public String write() {

        return "posts/write";
    }


}
