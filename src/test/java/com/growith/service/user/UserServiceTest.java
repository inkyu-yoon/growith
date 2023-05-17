package com.growith.service.user;

import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.domain.user.dto.UserUpdateRequest;
import com.growith.global.exception.AppException;
import com.growith.global.exception.ErrorCode;
import com.growith.service.UserService;
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

        @Test
        @DisplayName("회원수 카운트 조회 성공")
        public void countUser(){
            given(userRepository.count())
                    .willReturn(1L);

            assertDoesNotThrow(()->userService.countUser());

            verify(userRepository, atLeastOnce()).count();

        }

    }

    @Nested
    @DisplayName("마이페이지 조회 테스트")
    class getMyPageTest{

        @Test
        @DisplayName("마이페이지 조회 성공 테스트")
        public void getMyPageSuccess(){
            given(userRepository.findByUserName(anyString()))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.getMyPageUser(anyString()));

            verify(userRepository, atLeastOnce()).findByUserName(anyString());
        }

        @Test
        @DisplayName("마이페이지 조회 실패 테스트 (회원이 존재하지 않는 경우) ")
        public void getMyPageError(){
            when(userRepository.findByUserName(anyString()))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> userService.getMyPageUser(anyString()));
            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(userRepository,atLeastOnce()).findByUserName(anyString());
        }
    }

    @Nested
    @DisplayName("회원 정보 수정 테스트")
    class updateUser{
        @Mock
        private UserUpdateRequest userUpdateRequest;

        String userName = "userName";

        @Test
        @DisplayName("회원 정보 수정 성공 테스트")
        public void updateUserSuccess(){

            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(mockUser));

            assertDoesNotThrow(() -> userService.updateUser(userName, anyLong(), userUpdateRequest));

            verify(userRepository,atLeastOnce()).findById(anyLong());

        }

        @Test
        @DisplayName("회원 정보 수정 성공 테스트 (이미 본인이 쓰고있는 닉네임으로 요청시 성공)")
        public void updateUserSuccess2(){

            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(mockUser));
            given(mockUser.checkNickName(userUpdateRequest))
                    .willReturn(true);

            assertDoesNotThrow(() -> userService.updateUser(userName, anyLong(), userUpdateRequest));

            verify(userRepository,atLeastOnce()).findById(anyLong());

        }

        @Test
        @DisplayName("회원 정보 수정 실패 테스트 (회원이 존재하지 않는 경우)")
        public void updateUserError1(){
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> userService.updateUser(userName, anyLong(), userUpdateRequest));
            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(userRepository,atLeastOnce()).findById(anyLong());

        }

        @Test
        @DisplayName("회원 정보 수정 실패 테스트 (수정 요청자가 본인이 아닌 경우)")
        public void updateUserError2(){
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(mockUser));

            doThrow(new AppException(ErrorCode.USER_NOT_MATCH))
                    .when(mockUser).checkAuth(userName);

            AppException appException = assertThrows(AppException.class, () -> userService.updateUser(userName, anyLong(), userUpdateRequest));
            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_MATCH);

            verify(userRepository,atLeastOnce()).findById(anyLong());
            verify(mockUser,atLeastOnce()).checkAuth(userName);

        }

        @Test
        @DisplayName("회원 정보 수정 실패 테스트 (중복된 닉네임으로 변경 요청하는 경우)")
        public void updateUserError3(){

            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(mockUser));
            given(userUpdateRequest.getNickName())
                    .willReturn("nickName");
            given(mockUser.checkNickName(userUpdateRequest))
                    .willReturn(false);
            when(userRepository.existsByNickName("nickName"))
                    .thenReturn(true);

            AppException appException = assertThrows(AppException.class, () -> userService.updateUser(userName, anyLong(), userUpdateRequest));
            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_NICKNAME);

            verify(userRepository,atLeastOnce()).findById(anyLong());
            verify(userRepository,atLeastOnce()).existsByNickName(anyString());

        }
    }
}