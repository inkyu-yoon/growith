package com.growith.controller;

import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.service.user.UserService;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.global.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
