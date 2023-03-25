package com.growith.controller;

import com.growith.service.user.UserJoinService;
import com.growith.domain.user.oauth.UserProfile;
import com.growith.domain.user.oauth.OAuthInfo;
import com.growith.service.webclient.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserLoginController {

    private final WebClientService webClientService;
    private final UserJoinService userJoinService;

    @GetMapping("/oauth2/redirect")
    public String githubLogin(@RequestParam String code) {
        String accessToken = webClientService.getAccessToken(code);
        return "redirect:/githubLogin/success?access_token="+accessToken;
    }

    @GetMapping("/githubLogin/success")
    public String githubLoginSuccess(@RequestParam(name = "access_token") String accessToken) {
        UserProfile userInfo = webClientService.getUserInfo(accessToken);

        userJoinService.login(userInfo);

        return "redirect:/";
    }
}
