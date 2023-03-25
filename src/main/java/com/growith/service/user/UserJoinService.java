package com.growith.service.user;

import com.growith.domain.user.User;
import com.growith.domain.user.oauth.UserProfile;

public interface UserJoinService {
    public void login(UserProfile userProfile);

    public User join(UserProfile userProfile);
}
