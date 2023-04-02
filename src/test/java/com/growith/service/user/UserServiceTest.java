package com.growith.service.user;

import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.domain.user.dto.UserUpdateRequest;
import com.growith.global.exception.AppException;
import com.growith.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User mockUser;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("회원 조회 테스트")
    class getUserTest{

        @Test
        @DisplayName("회원 조회 성공")
        public void getUserSuccess(){
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(()->userService.getUser(anyLong()));

            verify(userRepository, atLeastOnce()).findById(anyLong());
        }

        @Test
        @DisplayName("회원 조회 실패 (회원이 존재하지 않는 경우)")
        public void getUserError(){

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> userService.getUser(anyLong()));

            assertThat(appException.getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findById(anyLong());
        }

    }

    @Nested
    @DisplayName("마이페이지 조회 테스트")
    class getMyPageTest{

        @Test
        @DisplayName("마이페이지 조회 성공 테스트")
        public void getMyPageSuccess(){
            given(userRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.getMyPageUser(anyString()));

            verify(userRepository, atLeastOnce()).findByEmail(anyString());
        }

        @Test
        @DisplayName("마이페이지 조회 실패 테스트 (회원이 존재하지 않는 경우) ")
        public void getMyPageError(){
            when(userRepository.findByEmail(anyString()))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> userService.getMyPageUser(anyString()));
            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(userRepository,atLeastOnce()).findByEmail(anyString());
        }
    }

    @Nested
    @DisplayName("회원 정보 수정 테스트")
    class updateUser{
        @Mock
        private UserUpdateRequest userUpdateRequest;

        @Test
        @DisplayName("회원 정보 수정 성공 테스트")
        public void updateUserSuccess(){
            String email = "email";

            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.updateUser(email, anyLong(), userUpdateRequest));

        }

        @Test
        @DisplayName("회원 정보 수정 실패 테스트 (회원이 존재하지 않는 경우)")
        public void updateUserError1(){
            String email = "email";

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> userService.updateUser(email, anyLong(), userUpdateRequest));
            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(userRepository,atLeastOnce()).findById(anyLong());

        }

        @Test
        @DisplayName("회원 정보 수정 실패 테스트 (수정 요청자가 본인이 아닌 경우)")
        public void updateUserError2(){
            String email = "email";

            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(mockUser));

            doThrow(new AppException(ErrorCode.USER_NOT_MATCH))
                    .when(mockUser).checkAuth(email);

            AppException appException = assertThrows(AppException.class, () -> userService.updateUser(email, anyLong(), userUpdateRequest));
            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_MATCH);

            verify(userRepository,atLeastOnce()).findById(anyLong());
            verify(mockUser,atLeastOnce()).checkAuth(email);

        }
    }
}