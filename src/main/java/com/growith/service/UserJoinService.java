package com.growith.service;

import com.growith.domain.user.User;
import com.growith.domain.user.oauth.UserProfile;

public interface UserJoinService {
    public String login(UserProfile userProfile);

    public User join(UserProfile userProfile);
}
