package com.growith.controller;

import com.growith.domain.comment.dto.CommentGetResponse;
import com.growith.domain.post.Category;
import com.growith.domain.post.dto.PostGetListResponse;
import com.growith.domain.post.dto.PostGetResponse;
import com.growith.service.CommentService;
import com.growith.service.PostService;
import com.growith.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PostViewController {

    private final UserService userService;

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/")
    public String home(Model model, Pageable pageable) {
        Long numberOfUsers = userService.countUser();
        int maxSize = 5;
        Page<PostGetListResponse> allPostsByCategory = postService.getAllPostsByCategory(Category.NOTICE, pageable);
        List<PostGetListResponse> posts = allPostsByCategory.getContent().subList(0, (int) Math.min(maxSize, allPostsByCategory.getTotalElements()));
        List<PostGetListResponse> bests = postService.getBestPosts();
        model.addAttribute("posts", posts);
        model.addAttribute("bests", bests);
        model.addAttribute("numberOfUsers", numberOfUsers);
        return "index";
    }

    @GetMapping("/posts/qna")
    public String postsQNA(Model model, Pageable pageable) {
        Page<PostGetListResponse> posts = postService.getAllPostsByCategory(Category.QNA,pageable);

        model.addAttribute("posts", posts);
        model.addAttribute("numberOfPages", posts.getTotalPages());
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());
        return "posts/qna";
    }

    @GetMapping("/posts/community")
    public String postsCommunity(Model model,Pageable pageable) {
        Page<PostGetListResponse> posts = postService.getAllPostsByCategory(Category.COMMUNITY,pageable);

        model.addAttribute("posts", posts);
        model.addAttribute("numberOfPages", posts.getTotalPages());
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());
        return "posts/community";
    }

    @GetMapping("/posts/study")
    public String postStudy(Model model,Pageable pageable) {
        Page<PostGetListResponse> posts = postService.getAllPostsByCategory(Category.STUDY,pageable);

        model.addAttribute("posts", posts);
        model.addAttribute("numberOfPages", posts.getTotalPages());
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());
        return "posts/study";
    }

    @GetMapping("/posts/notice")
    public String postsNotice(Model model,Pageable pageable) {
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
    public String read(@PathVariable(name = "postId") Long postId, Model model, HttpServletRequest req, HttpServletResponse res) {
        postService.increaseView(postId, req, res);
        PostGetResponse post = postService.getPost(postId);
        List<CommentGetResponse> comments = commentService.getAllComments(postId);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "posts/detail";
    }

    @GetMapping("/posts/edit")
    public String edit(@RequestParam Long postId, Model model) {
        PostGetResponse post = postService.getPost(postId);
        model.addAttribute("post", post);
        return "posts/edit";
    }

}
