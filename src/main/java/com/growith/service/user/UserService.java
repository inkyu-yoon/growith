package com.growith.service.user;

import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.domain.user.dto.UserUpdateRequest;
import com.growith.global.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.growith.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserGetResponse getUser(Long userId) {

        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        return foundUser.toUserGetResponse();
    }

    public UserGetMyPageResponse getMyPageUser(String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        return foundUser.toUserGetMyPageResponse();
    }

    @Transactional
    public void updateUser(String email, Long userId, UserUpdateRequest requestDto) {
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        if (isExistsByNickName(requestDto)) {
            throw new AppException(DUPLICATE_NICKNAME);
        }

        foundUser.checkAuth(email);

        foundUser.updateUserInfo(requestDto);
    }

    private boolean isExistsByNickName(UserUpdateRequest requestDto) {
        return userRepository.existsByNickName(requestDto.getNickName());
    }
}
