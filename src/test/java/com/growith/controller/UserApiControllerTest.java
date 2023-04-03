package com.growith.controller;

import com.google.gson.Gson;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRole;
import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.domain.user.dto.UserUpdateRequest;
import com.growith.global.config.SecurityConfig;
import com.growith.global.exception.AppException;
import com.growith.global.exception.ErrorCode;
import com.growith.global.util.JwtUtil;
import com.growith.service.user.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserApiController.class)
@Import({SecurityConfig.class})
class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @MockBean
    UserService userService;

    @MockBean
    UserDetailsService userDetailsService;


    @Value("${jwt.secret}")
    String secretKey;

    @BeforeEach
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        given(userDetailsService.loadUserByUsername(anyString()))
                .willReturn(new User(1L, "name", "imageUrl", "nickName", "email", "blog", 0L, UserRole.ROLE_USER));
    }

    @Nested
    @DisplayName("회원 조회 테스트")
    class getUserTest{
        Long userId = 1L;
        UserGetResponse response = new UserGetResponse(userId, "name", "imageUrl", "nickName", "email", "blog");

        @Test
        @DisplayName("회원 조회 성공 테스트")
        void success() throws Exception {

            given(userService.getUser(userId))
                    .willReturn(response);

            mockMvc.perform(get("/api/v1/users/" + userId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(1))
                    .andExpect(jsonPath("$.result.name").value("name"))
                    .andExpect(jsonPath("$.result.imageUrl").value("imageUrl"))
                    .andExpect(jsonPath("$.result.nickName").value("nickName"))
                    .andExpect(jsonPath("$.result.email").value("email"))
                    .andExpect(jsonPath("$.result.blog").value("blog"));

        }

        @Test
        @DisplayName("회원 조회 실패 테스트 (회원이 존재하지 않는 경우)")
        @WithMockUser
        void error() throws Exception {

            when(userService.getUser(userId))
                    .thenThrow(new AppException(ErrorCode.USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/users/" + userId))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());

        }
    }

    @Nested
    @DisplayName("회원 마이페이지 조회 테스트")
    class getUserMyPageTest{
        String email = "email";

        UserGetMyPageResponse response = new UserGetMyPageResponse(1L, "name", "imageUrl", "nickName", email, "blog",0L);

        String token = JwtUtil.createToken(email, "ROLE_USER", secretKey, 1000L * 60 * 60);
        Cookie cookie = new Cookie("jwt", token);


        @Test
        @DisplayName("회원 마이페이지 조회 성공 테스트")
        void success() throws Exception {
            given(userService.getMyPageUser(email))
                    .willReturn(response);


            mockMvc.perform(get("/api/v1/users/mypage")
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(1L))
                    .andExpect(jsonPath("$.result.name").value("name"))
                    .andExpect(jsonPath("$.result.imageUrl").value("imageUrl"))
                    .andExpect(jsonPath("$.result.nickName").value("nickName"))
                    .andExpect(jsonPath("$.result.email").value("email"))
                    .andExpect(jsonPath("$.result.blog").value("blog"))
                    .andExpect(jsonPath("$.result.point").value(0L));

        }

        @Test
        @DisplayName("회원 마이페이지 조회 실패 테스트 (회원이 존재하지 않는 경우)")
        void error() throws Exception {

            when(userService.getMyPageUser(email))
                    .thenThrow(new AppException(ErrorCode.USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/users/mypage")
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("회원 정보 수정 테스트")
    class updateUserTest{
        Long userId = 1L;
        UserUpdateRequest request = new UserUpdateRequest("nickName", "blog");
        String email = "email";
        String token = JwtUtil.createToken(email, "ROLE_USER", secretKey, 1000L * 60 * 60);
        Cookie cookie = new Cookie("jwt", token);

        Gson gson = new Gson();


        @Test
        @DisplayName("회원 정보 수정 성공 테스트")
        void success() throws Exception {

            willDoNothing().given(userService)
                    .updateUser(email, userId, request);

            mockMvc.perform(patch("/api/v1/users/" + userId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(1L));

        }

        @Test
        @DisplayName("회원 정보 수정 실패 테스트 (회원이 존재하지 않는 경우)")
        void error1() throws Exception {

            doThrow(new AppException(ErrorCode.USER_NOT_FOUND))
                    .when(userService).updateUser(anyString(), anyLong(), any(UserUpdateRequest.class));


            mockMvc.perform(patch("/api/v1/users/" + userId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());

        }

        @Test
        @DisplayName("회원 정보 수정 실패 테스트 (수정 요청자가 본인이 아닌 경우)")
        void error2() throws Exception {

            doThrow(new AppException(ErrorCode.USER_NOT_MATCH))
                    .when(userService).updateUser(anyString(), anyLong(), any(UserUpdateRequest.class));


            mockMvc.perform(patch("/api/v1/users/" + userId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());

        }

        @Test
        @DisplayName("회원 정보 수정 실패 테스트 (중복된 닉네임으로 변경 요청하는 경우)")
        void error3() throws Exception {

            doThrow(new AppException(ErrorCode.DUPLICATE_NICKNAME))
                    .when(userService).updateUser(anyString(), anyLong(), any(UserUpdateRequest.class));


            mockMvc.perform(patch("/api/v1/users/" + userId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());

        }
    }
}