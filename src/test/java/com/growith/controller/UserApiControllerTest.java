package com.growith.controller;

import com.google.gson.Gson;
import com.growith.domain.alarm.dto.AlarmGetListResponse;
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
import com.growith.global.exception.ErrorCode;
import com.growith.global.util.JwtUtil;
import com.growith.service.AlarmService;
import com.growith.service.PostService;
import com.growith.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

import static com.growith.global.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    Long userId;
    Long postId;
    Long alarmId;

    String userName;
    String token;
    Cookie cookie;
    Gson gson;
    UserGetResponse userGetResponse;
    UserGetMyPageResponse userGetMyPageResponse;

    UserUpdateRequest userUpdateRequest;

    UserUpdateResponse userUpdateResponse;
    PostGetListResponse postGetListResponse;
    AlarmGetListResponse alarmGetListResponse;

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

        userId = 1L;
        userName = "userName";
        postId = 1L;
        alarmId = 1L;

        token = JwtUtil.createToken(1L,userName, "ROLE_USER", secretKey, 1000L * 60 * 60);
        cookie = new Cookie("jwt", token);
        gson = new Gson();
        userGetResponse = new UserGetResponse(userId, "userName", "imageUrl", "nickName", "email", "blog", "githubUrl");
        userGetMyPageResponse = new UserGetMyPageResponse(1L, userName, "imageUrl", "nickName", "email", "blog", 0L, "githubUrl");
        userUpdateRequest = new UserUpdateRequest("nickName", "blog", "email@email.com");
        userUpdateResponse = new UserUpdateResponse(userId, "nickName", "blog", "email");
        postGetListResponse = PostGetListResponse
                .builder()
                .postId(postId)
                .title("title")
                .content("content")
                .date("date")
                .nickName("nickName")
                .view(0L)
                .build();

        alarmGetListResponse = AlarmGetListResponse.builder()
                .alarmId(alarmId)
                .postId(postId)
                .createdAt("createdAt")
                .fromUserNickName("fromUserNickName")
                .text("text")
                .postName("postName")
                .build();
    }

    @Nested
    @DisplayName("회원 조회 테스트")
    class getUserTest {

        @Test
        @DisplayName("회원 조회 성공 테스트")
        void success() throws Exception {

            given(userService.getUser(userId))
                    .willReturn(userGetResponse);

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


        @Test
        @DisplayName("회원 마이페이지 조회 성공 테스트")
        void success() throws Exception {
            given(userService.getMyPageUser(userName))
                    .willReturn(userGetMyPageResponse);


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

        List<PostGetListResponse> posts = new ArrayList<>();

        @Test
        @DisplayName("회원 별 게시글 조회 성공")
        void getPostsByUserSuccess() throws Exception {

            posts.add(postGetListResponse);
            Page<PostGetListResponse> postsPage = new PageImpl<>(posts);


            given(userService.getUser(userId))
                    .willReturn(userGetResponse);
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


        @Test
        @DisplayName("회원 정보 수정 성공 테스트")
        void success() throws Exception {
            given(userService.updateUser(eq(userName), eq(userId), any(UserUpdateRequest.class)))
                    .willReturn(userUpdateResponse);

            mockMvc.perform(patch("/api/v1/users/" + userId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(userUpdateRequest)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").value(1L))
                    .andExpect(jsonPath("$.result.nickName").value("nickName"))
                    .andExpect(jsonPath("$.result.blog").value("blog"))
                    .andExpect(jsonPath("$.result.email").value("email"));

        }
        private static Stream<Arguments> testCasesOfUpdateUser() {
            return Stream.of(
                    Arguments.of(USER_NOT_FOUND,404,"가입된 회원이 아닙니다."),
                    Arguments.of(DUPLICATE_NICKNAME,409,"이미 존재하는 닉네임입니다."),
                    Arguments.of(USER_NOT_MATCH,401,"본인만 접근할 수 있습니다.")
            );
        }



        @ParameterizedTest
        @MethodSource("testCasesOfUpdateUser")
        @DisplayName("회원 정보 수정 실패 테스트 (회원이 존재하지 않는 경우)")
        void error1(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {

            doThrow(new AppException(errorCode))
                    .when(userService).updateUser(anyString(), anyLong(), any(UserUpdateRequest.class));


            mockMvc.perform(patch("/api/v1/users/" + userId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(userUpdateRequest)))
                    .andDo(print())
                    .andExpect(status().is(responseStatus))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

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

    @Nested
    @DisplayName("회원 알림 조회 테스트")
    class getAlarmsTest{

        @Test
        @DisplayName("알림 조회 성공 테스트")
        void success() throws Exception {
            given(alarmService.getAlarms(userName))
                    .willReturn(List.of(alarmGetListResponse));

            mockMvc.perform(get("/api/v1/users/alarms")
                    .cookie(cookie))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result[0].alarmId").value(alarmId))
                    .andExpect(jsonPath("$.result[0].fromUserNickName").value("fromUserNickName"))
                    .andExpect(jsonPath("$.result[0].postName").value("postName"))
                    .andExpect(jsonPath("$.result[0].postId").value(postId))
                    .andExpect(jsonPath("$.result[0].text").value("text"))
                    .andExpect(jsonPath("$.result[0].createdAt").value("createdAt"));

        }

        @Test
        @DisplayName("알림 조회 실패 테스트 (가입된 회원을 찾을 수 없는 경우)")
        void error() throws Exception {

            when(alarmService.getAlarms(userName))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/users/alarms")
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").value("가입된 회원이 아닙니다."));

        }
    }

    @Nested
    @DisplayName("회원 알림 삭제 테스트")
    class deleteAlarmTest{

        @Test
        @DisplayName("알림 삭제 성공 테스트")
        void success() throws Exception {
            willDoNothing()
                    .given(alarmService)
                            .delete(userName,alarmId);

            mockMvc.perform(delete("/api/v1/users/alarms/"+alarmId)
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").value("complete"));

        }
        private static Stream<Arguments> testCasesOfDeleteAlarm() {
            return Stream.of(
                    Arguments.of(USER_NOT_FOUND,404,"가입된 회원이 아닙니다."),
                    Arguments.of(ALARM_NOT_FOUND,404,"알림 데이터를 찾을 수 없습니다.")
            );
        }

        @ParameterizedTest
        @MethodSource("testCasesOfDeleteAlarm")
        @DisplayName("알림 삭제 실패 테스트 ")
        void error1(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {

            doThrow(new AppException(errorCode))
                    .when(alarmService)
                    .delete(userName, alarmId);

            mockMvc.perform(delete("/api/v1/users/alarms/"+alarmId)
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(status().is(responseStatus))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

        }

    }

}