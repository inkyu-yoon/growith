package com.growith.service.user;

import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.domain.user.oauth.UserProfile;
import com.growith.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserJoinJoinServiceImpl implements UserJoinService {

    private final long TOKEN_VALID_MILLIS = 1000L * 60 * 60;

    @Value("${jwt.secret}")
    private String secretKey;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public String login(UserProfile userProfile) {
        User foundUser = userRepository.findByEmail(userProfile.getEmail())
                .orElseGet(() -> join(userProfile));

        return JwtUtil.createToken(foundUser.getEmail(), foundUser.getUserRole().toString(), secretKey, TOKEN_VALID_MILLIS);

    }

    @Override
    @Transactional
    public User join(UserProfile userProfile) {
        return userRepository.saveAndFlush(userProfile.toEntity());
    }


}