package com.growith.controller;

import com.growith.service.user.UserService;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.global.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserApiController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<Response<UserGetResponse>> get(@PathVariable(name = "userId") Long userId) {
        UserGetResponse response = userService.getUser(userId);

        return ResponseEntity.ok(Response.success(response));
    }



}
