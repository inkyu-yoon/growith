package com.growith.controller;

import com.google.gson.Gson;
import com.growith.domain.post.dto.PostGetListResponse;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRole;
import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.domain.user.dto.UserUpdateRequest;
import com.growith.domain.user.dto.UserUpdateResponse;
import com.growith.global.aop.BindingCheck;
import com.growith.global.config.SecurityConfig;
import com.growith.global.exception.AppException;
import com.growith.global.util.JwtUtil;
import com.growith.service.AlarmService;
import com.growith.service.PostService;
import com.growith.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.growith.global.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserApiController.class)
@EnableAspectJAutoProxy
@Import({SecurityConfig.class, BindingCheck.class})
class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @MockBean
    UserService userService;

    @MockBean
    UserDetailsService userDetailsService;

    @MockBean
    PostService postService;
    @MockBean
    AlarmService alarmService;


    @Value("${jwt.secret}")
    String secretKey;

    @BeforeEach
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        given(userDetailsService.loadUserByUsername(anyString()))
                .willReturn(User.builder()
                        .userName("userName")
                        .email("email")
                        .point(0L)
                        .blog("blog")
                        .githubUrl("githubUrl")
                        .nickName("nickName")
                        .imageUrl("imageUrl")
                        .userRole(UserRole.ROLE_USER)
                        .build());
    }

    @Nested
    @DisplayName("회원 조회 테스트")
    class getUserTest {
        Long userId = 1L;
        UserGetResponse response = new UserGetResponse(userId, "userName", "imageUrl", "nickName", "email", "blog", "githubUrl");

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
                    .andExpect(jsonPath("$.result.userName").value("userName"))
                    .andExpect(jsonPath("$.result.imageUrl").value("imageUrl"))
                    .andExpect(jsonPath("$.result.nickName").value("nickName"))
                    .andExpect(jsonPath("$.result.email").value("email"))
                    .andExpect(jsonPath("$.result.blog").value("blog"))
                    .andExpect(jsonPath("$.result.githubUrl").value("githubUrl"));

        }

        @Test
        @DisplayName("회원 조회 실패 테스트 (회원이 존재하지 않는 경우)")
        @WithMockUser
        void error() throws Exception {

            when(userService.getUser(userId))
                    .thenThrow(new AppException(USER_NOT_FOUND));

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
    class getUserMyPageTest {
        String userName = "userName";

        UserGetMyPageResponse response = new UserGetMyPageResponse(1L, userName, "imageUrl", "nickName", "email", "blog", 0L, "githubUrl");

        String token = JwtUtil.createToken(userName, "ROLE_USER", secretKey, 1000L * 60 * 60);
        Cookie cookie = new Cookie("jwt", token);


        @Test
        @DisplayName("회원 마이페이지 조회 성공 테스트")
        void success() throws Exception {
            given(userService.getMyPageUser(userName))
                    .willReturn(response);


            mockMvc.perform(get("/api/v1/users/mypage")
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(1L))
                    .andExpect(jsonPath("$.result.userName").value(userName))
                    .andExpect(jsonPath("$.result.imageUrl").value("imageUrl"))
                    .andExpect(jsonPath("$.result.nickName").value("nickName"))
                    .andExpect(jsonPath("$.result.email").value("email"))
                    .andExpect(jsonPath("$.result.blog").value("blog"))
                    .andExpect(jsonPath("$.result.point").value(0L))
                    .andExpect(jsonPath("$.result.githubUrl").value("githubUrl"));

        }

        @Test
        @DisplayName("회원 마이페이지 조회 실패 테스트 (회원이 존재하지 않는 경우)")
        void error() throws Exception {

            when(userService.getMyPageUser(userName))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/users/mypage")
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("회원 별 게시글 조회 테스트")
    class getPostsByUserTest {
        Long userId = 1L;
        Long postId = 1L;
        String userName = "userName";

        UserGetResponse user = new UserGetResponse(userId, userName, "imageUrl", "nickName", "email", "blog", "githubUrl");

        PostGetListResponse postGetListResponse = PostGetListResponse
                .builder()
                .postId(postId)
                .title("title")
                .content("content")
                .date("date")
                .nickName("nickName")
                .view(0L)
                .build();
        List<PostGetListResponse> posts = new ArrayList<>();
        @Test
        @DisplayName("회원 별 게시글 조회 성공")
        void getPostsByUserSuccess() throws Exception {

            posts.add(postGetListResponse);
            Page<PostGetListResponse> postsPage = new PageImpl<>(posts);


            given(userService.getUser(userId))
                    .willReturn(user);
            given(postService.getAllPostsByUserName(any(), any()))
                    .willReturn(postsPage);

            mockMvc.perform(get("/api/v1/users/" + userId + "/posts"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.content").exists())
                    .andExpect(jsonPath("$.result.content[0].postId").value(1))
                    .andExpect(jsonPath("$.result.content[0].title").value("title"))
                    .andExpect(jsonPath("$.result.content[0].content").value("content"))
                    .andExpect(jsonPath("$.result.content[0].date").value("date"))
                    .andExpect(jsonPath("$.result.content[0].nickName").value("nickName"));
        }

        @Test
        @DisplayName("회원 별 게시글 조회 실패 (가입된 회원이 아닌 경우)")
        void getPostsByUserError() throws Exception {

            when(userService.getUser(userId))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/users/" + userId + "/posts"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

    }

    @Nested
    @DisplayName("회원 정보 수정 테스트")
    class updateUserTest {
        Long userId = 1L;
        UserUpdateRequest request = new UserUpdateRequest("nickName", "blog", "email@email.com");

        UserUpdateResponse response = new UserUpdateResponse(userId, "nickName", "blog", "email");
        String userName = "userName";
        String token = JwtUtil.createToken(userName, "ROLE_USER", secretKey, 1000L * 60 * 60);
        Cookie cookie = new Cookie("jwt", token);

        Gson gson = new Gson();


        @Test
        @DisplayName("회원 정보 수정 성공 테스트")
        void success() throws Exception {
            given(userService.updateUser(eq(userName), eq(userId), any(UserUpdateRequest.class)))
                    .willReturn(response);

            mockMvc.perform(patch("/api/v1/users/" + userId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(1L))
                    .andExpect(jsonPath("$.result.nickName").value("nickName"))
                    .andExpect(jsonPath("$.result.blog").value("blog"))
                    .andExpect(jsonPath("$.result.email").value("email"));

        }

        @Test
        @DisplayName("회원 정보 수정 실패 테스트 (회원이 존재하지 않는 경우)")
        void error1() throws Exception {

            doThrow(new AppException(USER_NOT_FOUND))
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

            doThrow(new AppException(USER_NOT_MATCH))
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

            doThrow(new AppException(DUPLICATE_NICKNAME))
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
        @DisplayName("회원 정보 수정 실패 테스트 (Binding Error 발생)")
        void error4() throws Exception {

            UserUpdateRequest request = new UserUpdateRequest(null, "blog", "email");

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