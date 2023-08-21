package com.growith.controller;

import com.growith.domain.user.oauth.UserProfile;
import com.growith.global.util.CookieUtil;
import com.growith.global.util.TextParsingUtil;
import com.growith.service.GithubJoinService;
import com.growith.service.UserJoinService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.growith.global.util.constant.CookieConstants.JWT_COOKIE_AGE;
import static com.growith.global.util.constant.CookieConstants.JWT_COOKIE_NAME;

@Controller
@RequiredArgsConstructor
public class UserJoinController {

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    private final GithubJoinService githubJoinService;
    private final UserJoinService userJoinService;

    private final AccessTokenFeignClient accessTokenFeignClient;
    private final UserProfileFeignClient userProfileFeignClient;

    @GetMapping("/oauth2/redirect")
    public String githubLogin(@RequestParam String code) {
        String response = accessTokenFeignClient.getAccessToken(clientId, clientSecret, code);

        return "redirect:/githubLogin/success?access_token=" + TextParsingUtil.parsingFormData(response).get("access_token");
    }

    @GetMapping("/githubLogin/success")
    public String githubLoginSuccess(HttpServletResponse response, @RequestParam(name = "access_token") String accessToken) {

        UserProfile userProfile = userProfileFeignClient.getUserInfo(String.format("Bearer %s",accessToken));

        String jwt = userJoinService.login(userProfile);

        CookieUtil.setCookie(response, JWT_COOKIE_NAME, jwt, JWT_COOKIE_AGE);

        return "redirect:/";
    }
}
