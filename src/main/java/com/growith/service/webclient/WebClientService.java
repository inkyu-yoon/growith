package com.growith.service.webclient;

import com.growith.domain.user.oauth.AccessTokenRequest;
import com.growith.domain.user.oauth.UserProfile;
import com.growith.global.util.TextParsingUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WebClientService {
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;


    public String getAccessToken(String code) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://github.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        AccessTokenRequest requestBody = AccessTokenRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .build();

        String response = webClient.post()
                .uri("/login/oauth/access_token")
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(String.class)
                .block().getBody();


        return TextParsingUtil.parsingFormData(response).get("access_token");
    }
    public UserProfile getUserInfo(String accessToken) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .build();

        return webClient.get()
                .uri("/user")
                .header("Authorization", "token " + accessToken)
                .retrieve()
                .toEntity(UserProfile.class)
                .block().getBody();

    }


}
