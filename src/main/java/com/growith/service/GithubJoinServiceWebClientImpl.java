package com.growith.service;

import com.growith.domain.user.oauth.AccessTokenRequest;
import com.growith.domain.user.oauth.UserProfile;
import com.growith.global.util.TextParsingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Primary
public class GithubJoinServiceWebClientImpl implements GithubJoinService {

    private final WebClient webClient;

    @Override
    public String getAccessToken(String clientId, String clientSecret, String code) {

        String response = webClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(AccessTokenRequest.createAccessTokenRequest(clientId, clientSecret, code))
                .retrieve()
                .toEntity(String.class)
                .block()
                .getBody();


        return TextParsingUtil.parsingFormData(response).get("access_token");
    }

    @Override
    public UserProfile getUserInfo(String accessToken) {

        return webClient.get()
                .uri("https://api.github.com/user")
                .header("Authorization", String.format("Bearer %s",accessToken))
                .retrieve()
                .toEntity(UserProfile.class)
                .block()
                .getBody();

    }

}
