package com.growith.service.user;

import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.global.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.growith.global.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserGetResponse getUser(Long userId) {

        User foundUser = userRepository.findById(userId).orElseThrow(
                () -> new AppException(USER_NOT_FOUND)
        );

        return foundUser.toGetUserResponse();
    }
}
