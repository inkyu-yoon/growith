package com.growith.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "access-token-feign-client", url = "https://github.com")
public interface AccessTokenFeignClient {

    @PostMapping("/login/oauth/access_token")
    String getAccessToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("code") String code
    );
}
