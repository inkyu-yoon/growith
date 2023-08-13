package com.growith.controller;

import com.growith.domain.alarm.dto.AlarmGetListResponse;
import com.growith.domain.post.dto.PostGetListResponse;
import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.domain.user.dto.UserUpdateRequest;
import com.growith.domain.user.dto.UserUpdateResponse;
import com.growith.global.Response;
import com.growith.global.specification.UserApiSpecification;
import com.growith.service.AlarmService;
import com.growith.service.PostService;
import com.growith.service.UserService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserApiController implements UserApiSpecification {

    private final UserService userService;
    private final PostService postService;
    private final AlarmService alarmService;

    @GetMapping("/{userId}")
    public ResponseEntity<Response<UserGetResponse>> get(@PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(Response.success(userService.getUser(userId)));
    }


    @GetMapping("/mypage")
    public ResponseEntity<Response<UserGetMyPageResponse>> getMyPage(Authentication authentication) {
        String userName = authentication.getName();
        return ResponseEntity.ok(Response.success(userService.getMyPageUser(userName)));
    }


    @GetMapping("/{userId}/posts")
    public ResponseEntity<Response<Page<PostGetListResponse>>> getAllByUser(@PathVariable Long userId,  Pageable pageable) {
        UserGetResponse user = userService.getUser(userId);
        return ResponseEntity.ok(Response.success(postService.getAllPostsByUserName(user.getUserName(), pageable)));
    }


    @PatchMapping("/{userId}")
    public ResponseEntity<Response<UserUpdateResponse>> update(Authentication authentication, @PathVariable(name = "userId") Long userId, @Validated @RequestBody UserUpdateRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        return ResponseEntity.ok(Response.success(userService.updateUser(userName, userId, requestDto)));
    }


    @GetMapping("/alarms")
    public ResponseEntity<Response<List<AlarmGetListResponse>>> getAlarms(Authentication authentication){
        String userName = authentication.getName();
        return ResponseEntity.ok(Response.success(alarmService.getAlarms(userName)));
    }


    @DeleteMapping("/alarms/{alarmId}")
    public ResponseEntity<Response<String>> deleteAlarm(Authentication authentication, @PathVariable(name = "alarmId") Long alarmId) {
        String userName = authentication.getName();
        alarmService.delete(userName,alarmId);

        return ResponseEntity.ok(Response.success("complete"));
    }

}
