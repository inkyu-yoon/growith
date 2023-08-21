package com.growith.controller;

import com.growith.domain.user.oauth.UserProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-profile-feign-client", url = "https://api.github.com")
public interface UserProfileFeignClient {

    @GetMapping("/user")
    UserProfile getUserInfo(@RequestHeader("Authorization") String accessToken);
}
