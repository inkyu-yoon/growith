package com.growith.controller;

import com.growith.domain.comment.dto.*;
import com.growith.domain.likes.dto.LikeResponse;
import com.growith.domain.post.Category;
import com.growith.domain.post.dto.*;
import com.growith.global.Response;
import com.growith.global.specification.PostApiSpecification;
import com.growith.service.CommentService;
import com.growith.service.PostLikeService;
import com.growith.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.cloud.contract.spec.internal.HttpStatus.CREATED;

@RestController
@Slf4j
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostApiController implements PostApiSpecification {

    private final PostService postService;
    private final CommentService commentService;
    private final PostLikeService postLikeService;

    @GetMapping
    public ResponseEntity<Response<Page<PostGetResponse>>> getAll(Pageable pageable) {
        Page<PostGetResponse> response = postService.getAllPosts(pageable);
        return ResponseEntity.ok(Response.success(response));
    }


    @PostMapping
    public ResponseEntity<Response<PostResponse>> create(Authentication authentication, @Validated @RequestBody PostCreateRequest requestDto, BindingResult br) {
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
    public ResponseEntity<Response<PostResponse>> update(Authentication authentication, @PathVariable(name = "postId") Long postId, @Validated @RequestBody PostUpdateRequest requestDto, BindingResult br) {
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
    public ResponseEntity<Response<List<CommentGetResponse>>> getAllComments(@PathVariable(name = "postId") Long postId) {
        List<CommentGetResponse> response = commentService.getAllComments(postId);

        return ResponseEntity.ok(Response.success(response));
    }


    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Response<CommentResponse>> deleteComments(Authentication authentication, @PathVariable(name = "postId") Long postId, @PathVariable(name = "commentId") Long commentId) {
        String userName = authentication.getName();
        CommentResponse response = commentService.deleteComment(postId, userName, commentId);

        return ResponseEntity.ok(Response.success(response));
    }


    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Response<CommentResponse>> updateComments(Authentication authentication, @PathVariable(name = "postId") Long postId, @PathVariable(name = "commentId") Long commentId, @Validated @RequestBody CommentUpdateRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        CommentResponse response = commentService.updateComment(postId, userName, commentId, requestDto);

        return ResponseEntity.ok(Response.success(response));
    }


    @PostMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Response<CommentReplyResponse>> createCommentReply(Authentication authentication, @PathVariable(name = "postId") Long postId, @PathVariable(name = "commentId") Long commentId, @Validated @RequestBody CommentCreateRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        CommentReplyResponse response = commentService.createCommentReply(postId, userName, commentId, requestDto);

        return ResponseEntity.ok(Response.success(response));
    }


    @PostMapping("/{postId}/likes")
    public ResponseEntity<Response<LikeResponse>> changePostLike(Authentication authentication, @PathVariable(name = "postId") Long postId) {
        String userName = authentication.getName();
        LikeResponse response = postLikeService.addLike(userName, postId);

        return ResponseEntity.ok(Response.success(response));
    }
}
