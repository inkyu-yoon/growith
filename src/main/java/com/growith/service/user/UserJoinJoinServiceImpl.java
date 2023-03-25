package com.growith.service.user;

import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.domain.user.oauth.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserJoinJoinServiceImpl implements UserJoinService {
    private final UserRepository userRepository;


    @Override
    @Transactional
    public void login(UserProfile userProfile) {
        User foundUser = userRepository.findByEmail(userProfile.getEmail())
                .orElseGet(() -> join(userProfile));

    }

    @Override
    @Transactional
    public User join(UserProfile userProfile) {
        return userRepository.saveAndFlush(userProfile.toEntity());
    }


}
