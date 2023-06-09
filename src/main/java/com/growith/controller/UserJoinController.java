package com.growith.controller;

import com.growith.domain.user.oauth.UserProfile;
import com.growith.global.util.CookieUtil;
import com.growith.service.UserJoinService;
import com.growith.service.WebClientService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.growith.global.util.constant.CookieConstants.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserJoinController {

    private final WebClientService webClientService;
    private final UserJoinService userJoinService;


    @GetMapping("/oauth2/redirect")
    public String githubLogin(@RequestParam String code) {
        String accessToken = webClientService.getAccessToken(code,"https://github.com/login/oauth/access_token");
        return "redirect:/githubLogin/success?access_token="+accessToken;
    }

    @GetMapping("/githubLogin/success")
    public String githubLoginSuccess(HttpServletResponse response, @RequestParam(name = "access_token") String accessToken) {
        UserProfile userInfo = webClientService.getUserInfo(accessToken,"https://api.github.com/user");

        String jwt = userJoinService.login(userInfo);

        CookieUtil.setCookie(response, JWT_COOKIE_NAME,jwt, JWT_COOKIE_AGE);

        return "redirect:/";
    }
}
