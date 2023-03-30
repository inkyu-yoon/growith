package com.growith.controller;

import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.domain.user.dto.UserUpdateRequest;
import com.growith.global.Response;
import com.growith.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserApiController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<Response<UserGetResponse>> get(@PathVariable(name = "userId") Long userId) {
        UserGetResponse response = userService.getUser(userId);

        return ResponseEntity.ok(Response.success(response));
    }


    @GetMapping("/mypage")
    public ResponseEntity<Response<UserGetMyPageResponse>> getMyPage(Authentication authentication) {
        String email = authentication.getName();
        UserGetMyPageResponse response = userService.getMyPageUser(email);

        return ResponseEntity.ok(Response.success(response));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Response<Long>> update(Authentication authentication, @PathVariable(name = "userId") Long userId, @RequestBody UserUpdateRequest requestDto) {
        String email = authentication.getName();
        userService.updateUser(email, userId,requestDto);

        return ResponseEntity.ok(Response.success(userId));
    }
}
