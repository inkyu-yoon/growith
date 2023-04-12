package com.growith.service.user;

import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.domain.user.UserRole;
import com.growith.domain.user.oauth.UserProfile;
import com.growith.global.util.JwtUtil;
import com.growith.service.UserJoinServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserJoinServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User mockUser;

    @InjectMocks
    private UserJoinServiceImpl userJoinService;

    @Nested
    @DisplayName("회원 회원가입 · 로그인 테스트")
    class userJoinLoginTest{

        @Mock
        UserProfile userProfile;

        String userName = "userName";

        @Test
        @DisplayName("로그인 성공 (회원가입 안되어있는 경우 회원 가입 후 jwt토큰 발급)")
        public void userLoginSuccess(){

            MockedStatic<JwtUtil> jwtUtilMockedStatic = mockStatic(JwtUtil.class);

            given(userProfile.getUserName())
                    .willReturn(userName);
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.empty());
            given(userRepository.saveAndFlush(userProfile.toEntity()))
                    .willReturn(mockUser);
            given(mockUser.getUserRole())
                    .willReturn(UserRole.ROLE_USER);
            given(JwtUtil.createToken(anyString(), anyString(), anyString(), anyLong())).
                    willReturn("jwt");


            assertDoesNotThrow(() -> userJoinService.login(userProfile));

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(userRepository, atLeastOnce()).saveAndFlush(userProfile.toEntity());

            jwtUtilMockedStatic.close();
        }
        @Test
        @DisplayName("로그인 성공 (회원가입 되어있는 경우 jwt토큰 발급)")
        public void userLoginSuccess1(){

            MockedStatic<JwtUtil> jwtUtilMockedStatic = mockStatic(JwtUtil.class);

            given(userProfile.getUserName())
                    .willReturn(userName);
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));

            given(mockUser.getUserRole())
                    .willReturn(UserRole.ROLE_USER);
            given(JwtUtil.createToken(anyString(), anyString(), anyString(), anyLong())).
                    willReturn("jwt");


            assertDoesNotThrow(() -> userJoinService.login(userProfile));

            verify(userRepository, atLeastOnce()).findByUserName(userName);

            jwtUtilMockedStatic.close();
        }
    }

}