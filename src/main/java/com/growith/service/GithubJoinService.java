package com.growith.service;

import com.growith.domain.user.oauth.UserProfile;

public interface GithubJoinService {

    String getAccessToken(String clientId, String clientSecret, String code);


    UserProfile getUserInfo(String accessToken);
}
