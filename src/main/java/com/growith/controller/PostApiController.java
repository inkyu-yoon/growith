package com.growith.controller;

import com.growith.domain.comment.dto.CommentCreateRequest;
import com.growith.domain.comment.dto.CommentGetResponse;
import com.growith.domain.comment.dto.CommentResponse;
import com.growith.domain.post.Category;
import com.growith.domain.post.dto.*;
import com.growith.global.Response;
import com.growith.service.comment.CommentService;
import com.growith.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.cloud.contract.spec.internal.HttpStatus.CREATED;

@RestController
@Slf4j
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<Response<Page<PostGetResponse>>> getAll(Pageable pageable) {
        Page<PostGetResponse> response = postService.getAllPosts(pageable);
        return ResponseEntity.ok(Response.success(response));
    }

    @PostMapping
    public ResponseEntity<Response<PostResponse>> create(Authentication authentication,@Validated @RequestBody PostCreateRequest requestDto,BindingResult br) {
        String userName = authentication.getName();
        PostResponse response = postService.createPost(userName, requestDto);
        return ResponseEntity.status(CREATED).body(Response.success(response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostGetResponse>> get(@PathVariable(name = "postId") Long postId) {
        PostGetResponse response = postService.getPost(postId);
        return ResponseEntity.ok(Response.success(response));
    }

    @GetMapping("/categories")
    public ResponseEntity<Response<Page<PostGetListResponse>>> getAllByCategory(@RequestParam Category category, Pageable pageable) {
        Page<PostGetListResponse> response = postService.getAllPostsByCategory(category, pageable);
        return ResponseEntity.ok(Response.success(response));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> delete(@PathVariable(name = "postId") Long postId, Authentication authentication) {
        String userName = authentication.getName();
        PostResponse response = postService.deletePost(postId, userName);
        return ResponseEntity.ok(Response.success(response));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> update(Authentication authentication,@PathVariable(name = "postId") Long postId, @Validated @RequestBody PostUpdateRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        PostResponse response = postService.updatePost(postId, userName, requestDto);
        return ResponseEntity.ok(Response.success(response));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Response<CommentResponse>> createComment(Authentication authentication, @PathVariable(name = "postId") Long postId, @Validated @RequestBody CommentCreateRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        CommentResponse response = commentService.createComment(postId, userName, requestDto);

        return ResponseEntity.ok(Response.success(response));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<Response<Page<CommentGetResponse>>> getAllComments(@PathVariable(name = "postId") Long postId, Pageable pageable) {
        Page<CommentGetResponse> response = commentService.getAllComments(postId, pageable);

        return ResponseEntity.ok(Response.success(response));
    }


}
