package com.growith.domain.user;

import com.growith.domain.user.oauth.UserProfile;

public interface UserJoinService {
    public void login(UserProfile userProfile);

    public User join(UserProfile userProfile);
}
