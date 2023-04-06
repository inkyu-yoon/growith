package com.growith.controller;

import com.growith.domain.post.Category;
import com.growith.domain.post.dto.PostGetListResponse;
import com.growith.domain.post.dto.PostGetResponse;
import com.growith.service.post.PostService;
import com.growith.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PostViewController {

    private final UserService userService;

    private final PostService postService;

    @GetMapping("/")
    public String home(Model model) {
        Long numberOfUsers = userService.countUser();
        log.info("{}",numberOfUsers);
        model.addAttribute("numberOfUsers", numberOfUsers);
        return "index";
    }

    @GetMapping("/posts/qna")
    public String postsQNA(Model model, @PageableDefault(size = 10, sort = "{created_date}",direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetListResponse> posts = postService.getAllPostsByCategory(Category.QNA,pageable);

        model.addAttribute("posts", posts);
        model.addAttribute("numberOfPages", posts.getTotalPages());
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());
        return "posts/qna";
    }

    @GetMapping("/posts/community")
    public String postsCommunity(Model model, @PageableDefault(size = 10, sort = "{created_date}",direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetListResponse> posts = postService.getAllPostsByCategory(Category.COMMUNITY,pageable);

        model.addAttribute("posts", posts);
        model.addAttribute("numberOfPages", posts.getTotalPages());
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());
        return "posts/community";
    }

    @GetMapping("/posts/study")
    public String postStudy(Model model, @PageableDefault(size = 10, sort = "{created_date}",direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetListResponse> posts = postService.getAllPostsByCategory(Category.STUDY,pageable);

        model.addAttribute("posts", posts);
        model.addAttribute("numberOfPages", posts.getTotalPages());
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());
        return "posts/study";
    }

    @GetMapping("/posts/notice")
    public String postsNotice(Model model, @PageableDefault(size = 10, sort = "{created_date}",direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostGetListResponse> posts = postService.getAllPostsByCategory(Category.NOTICE,pageable);

        model.addAttribute("posts", posts);
        model.addAttribute("numberOfPages", posts.getTotalPages());
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());
        return "posts/notice";
    }


    @GetMapping("/posts/write")
    public String write() {
        return "posts/write";
    }

    @GetMapping("/posts/{postId}")
    public String read(@PathVariable(name = "postId") Long postId, Model model) {
        PostGetResponse post = postService.getPost(postId);
        model.addAttribute("post", post);
        return "posts/detail";
    }

    @GetMapping("/posts/edit")
    public String edit(@RequestParam Long postId, Model model) {
        PostGetResponse post = postService.getPost(postId);
        model.addAttribute("post", post);
        return "posts/edit";
    }

}
