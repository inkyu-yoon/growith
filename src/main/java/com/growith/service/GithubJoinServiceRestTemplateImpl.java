package com.growith.service;

import com.growith.domain.user.oauth.UserProfile;
import com.growith.global.util.TextParsingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GithubJoinServiceRestTemplateImpl implements GithubJoinService {

    private final RestTemplate restTemplate;

    @Override
    public String getAccessToken(String clientId, String clientSecret, String code) {
        HttpEntity<String> response = restTemplate.exchange("https://github.com/login/oauth/access_token",
                HttpMethod.POST,
                setParam(clientId, clientSecret, code),
                String.class);

        return TextParsingUtil.parsingFormData(response.getBody()).get("access_token");
    }

    private HttpEntity<MultiValueMap<String, String>> setParam(String clientId, String clientSecret, String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        return new HttpEntity<>(params, new HttpHeaders());
    }

    @Override
    public UserProfile getUserInfo(String accessToken) {

        return restTemplate.exchange("https://api.github.com/user",
                        HttpMethod.GET,
                        setAccessToken(accessToken),
                        UserProfile.class)
                .getBody();
    }

    private HttpEntity<MultiValueMap<String, String>> setAccessToken(String accessToken) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", String.format("Bearer %s",accessToken));
        return new HttpEntity<>(requestHeaders);
    }
}
