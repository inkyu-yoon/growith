package com.growith.domain.user;

import com.growith.domain.user.oauth.GithubProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;


    @Transactional
    public void login(GithubProfile userInfo) {
        User foundUser = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> join(userInfo));

    }

    @Transactional
    public User join(GithubProfile userInfo) {
        return userRepository.saveAndFlush(userInfo.toEntity());
    }
}
