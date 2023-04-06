package com.growith.controller;

import com.growith.domain.post.dto.PostGetListResponse;
import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.domain.user.dto.UserUpdateRequest;
import com.growith.domain.user.dto.UserUpdateResponse;
import com.growith.global.Response;
import com.growith.service.post.PostService;
import com.growith.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserApiController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping("/{userId}")
    public ResponseEntity<Response<UserGetResponse>> get(@PathVariable(name = "userId") Long userId) {
        UserGetResponse response = userService.getUser(userId);

        return ResponseEntity.ok(Response.success(response));
    }


    @GetMapping("/mypage")
    public ResponseEntity<Response<UserGetMyPageResponse>> getMyPage(Authentication authentication) {
        String userName = authentication.getName();
        UserGetMyPageResponse response = userService.getMyPageUser(userName);

        return ResponseEntity.ok(Response.success(response));
    }

    @GetMapping("/{userId}/posts")
    public ResponseEntity<Response<Page<PostGetListResponse>>> getAllByUser(@PathVariable Long userId, Pageable pageable) {
        UserGetResponse user = userService.getUser(userId);
        Page<PostGetListResponse> response = postService.getAllPostsByUserName(user.getUserName(), pageable);

        return ResponseEntity.ok(Response.success(response));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Response<UserUpdateResponse>> update(Authentication authentication, @PathVariable(name = "userId") Long userId, @Validated @RequestBody UserUpdateRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        UserUpdateResponse response = userService.updateUser(userName, userId, requestDto);

        return ResponseEntity.ok(Response.success(response));
    }
}
