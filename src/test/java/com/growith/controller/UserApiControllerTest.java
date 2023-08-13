package com.growith.controller;

import com.google.gson.Gson;
import com.growith.domain.alarm.dto.AlarmGetListResponse;
import com.growith.domain.post.dto.PostGetListResponse;
import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.domain.user.dto.UserUpdateRequest;
import com.growith.domain.user.dto.UserUpdateResponse;
import com.growith.fixture.AlarmFixture;
import com.growith.fixture.PostFixture;
import com.growith.fixture.UserFixture;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
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
@EnableAspectJAutoProxy(proxyTargetClass = true)
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
    UserUpdateRequest userUpdateRequest;
    UserUpdateResponse userUpdateResponse;

    @BeforeEach
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        given(userDetailsService.loadUserByUsername(anyString()))
                .willReturn(UserFixture.createUser());

        userId = 1L;
        userName = "userName";
        postId = 1L;
        alarmId = 1L;

        token = JwtUtil.createToken(1L, userName, "ROLE_USER", secretKey, 1000L * 60 * 60);
        cookie = new Cookie("jwt", token);
        gson = new Gson();
        userUpdateRequest = UserFixture.createUserUpdateRequest();
        userUpdateResponse = UserFixture.createUserUpdateResponse();
    }

    @Nested
    @DisplayName("회원 조회")
    class getUserTest {

        @Test
        @DisplayName("성공")
        void success() throws Exception {

            UserGetResponse userGetResponse = UserFixture.createUserGetResponse();


            given(userService.getUser(userId))
                    .willReturn(userGetResponse);

            mockMvc.perform(get("/api/v1/users/" + userId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").exists())
                    .andExpect(jsonPath("$.result.userName").exists())
                    .andExpect(jsonPath("$.result.imageUrl").exists())
                    .andExpect(jsonPath("$.result.nickName").exists())
                    .andExpect(jsonPath("$.result.email").exists())
                    .andExpect(jsonPath("$.result.blog").exists())
                    .andExpect(jsonPath("$.result.githubUrl").exists());

        }

        @Test
        @DisplayName("실패 - userId에 해당하는 회원이 존재하지 않습니다.")
        void error() throws Exception {

            when(userService.getUser(userId))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/users/" + userId))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value("가입된 회원이 아닙니다."));

        }
    }

    @Nested
    @DisplayName("회원 마이페이지 조회")
    class getUserMyPageTest {


        @Test
        @DisplayName("성공")
        void success() throws Exception {

            UserGetMyPageResponse userGetMyPageResponse = UserFixture.createUserGetMyPageResponse();

            given(userService.getMyPageUser(userName))
                    .willReturn(userGetMyPageResponse);


            mockMvc.perform(get("/api/v1/users/mypage")
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").exists())
                    .andExpect(jsonPath("$.result.userName").exists())
                    .andExpect(jsonPath("$.result.imageUrl").exists())
                    .andExpect(jsonPath("$.result.nickName").exists())
                    .andExpect(jsonPath("$.result.email").exists())
                    .andExpect(jsonPath("$.result.blog").exists())
                    .andExpect(jsonPath("$.result.point").exists())
                    .andExpect(jsonPath("$.result.githubUrl").exists())
                    .andExpect(jsonPath("$.result.roadNameAddress").exists())
                    .andExpect(jsonPath("$.result.detailedAddress").exists())
                    .andExpect(jsonPath("$.result.postalCode").exists());

        }

        @Test
        @DisplayName("실패 - jwt claim 에 있는 회원 정보로 회원을 찾을 수 없습니다.")
        void error() throws Exception {

            when(userService.getMyPageUser(userName))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/users/mypage")
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value("가입된 회원이 아닙니다."));

        }
    }

    @Nested
    @DisplayName("회원 별 작성한 게시글 조회")
    class getPostsByUserTest {


        @Test
        @DisplayName("성공")
        void getPostsByUserSuccess() throws Exception {


            UserGetResponse userGetResponse = UserFixture.createUserGetResponse();
            PostGetListResponse postGetListResponse = PostFixture.createPostGetListResponse();
            List<PostGetListResponse> posts = new ArrayList<>();
            posts.add(postGetListResponse);
            Page<PostGetListResponse> postsPage = new PageImpl<>(posts);
            Pageable pageable = PageRequest.of(0, 20);

            given(userService.getUser(userId))
                    .willReturn(userGetResponse);
            given(postService.getAllPostsByUserName(userGetResponse.getUserName(), pageable))
                    .willReturn(postsPage);

            mockMvc.perform(get("/api/v1/users/" + userId + "/posts"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.content").exists())
                    .andExpect(jsonPath("$.result.content[0].postId").exists())
                    .andExpect(jsonPath("$.result.content[0].title").exists())
                    .andExpect(jsonPath("$.result.content[0].content").exists())
                    .andExpect(jsonPath("$.result.content[0].date").exists())
                    .andExpect(jsonPath("$.result.content[0].nickName").exists())
                    .andExpect(jsonPath("$.result.content[0].imageUrl").exists())
                    .andExpect(jsonPath("$.result.content[0].view").exists())
                    .andExpect(jsonPath("$.result.content[0].numOfComments").exists())
                    .andExpect(jsonPath("$.result.content[0].numOfLikes").exists());
        }

        @Test
        @DisplayName("실패 - userId에 해당하는 회원이 존재하지 않습니다.")
        void getPostsByUserError() throws Exception {

            when(userService.getUser(userId))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/users/" + userId + "/posts"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value("가입된 회원이 아닙니다."));

        }

    }

    @Nested
    @DisplayName("회원 정보 수정 테스트")
    class updateUserTest {


        @Test
        @DisplayName("회원 정보 수정 성공 테스트")
        void success() throws Exception {
            given(userService.updateUser(anyString(), anyLong(), any(UserUpdateRequest.class)))
                    .willReturn(userUpdateResponse);

            mockMvc.perform(patch("/api/v1/users/" + userId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(userUpdateRequest)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.id").exists())
                    .andExpect(jsonPath("$.result.nickName").exists())
                    .andExpect(jsonPath("$.result.blog").exists())
                    .andExpect(jsonPath("$.result.email").exists());

        }

        private static Stream<Arguments> UpdateUserFailScenarios() {
            return Stream.of(
                    Arguments.of(USER_NOT_FOUND, 404, "가입된 회원이 아닙니다."),
                    Arguments.of(DUPLICATE_NICKNAME, 409, "이미 존재하는 닉네임입니다."),
                    Arguments.of(USER_NOT_MATCH, 401, "본인만 접근할 수 있습니다.")
            );
        }


        @ParameterizedTest(name = "[{index}] - {2}")
        @MethodSource("UpdateUserFailScenarios")
        @DisplayName("실패")
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

        private static Stream<Arguments> UpdateUserBindingFailScenarios() {
            return Stream.of(
                    Arguments.of(UserUpdateRequest.builder().nickName(null).email("email@email.com").roadNameAddress("roadNameAddress").detailedAddress("detailedAddress").postalCode("postalCode").build(),
                            "닉네임은 존재해야합니다.",
                            "nickname",
                            "공백인 경우"),
                    Arguments.of(UserUpdateRequest.builder().nickName("nickName").email("email").roadNameAddress("roadNameAddress").detailedAddress("detailedAddress").postalCode("postalCode").build(),
                            "올바른 이메일 형식이 아닙니다.",
                            "email",
                            "올바른 이메일 형식이 아닌 경우"),
                    Arguments.of(UserUpdateRequest.builder().nickName("nickName").email("email@email.com").roadNameAddress(null).detailedAddress("detailedAddress").postalCode("postalCode").build(),
                            "도로명 주소는 존재해야합니다.",
                            "roadNameAddress",
                            "공백인 경우"),
                    Arguments.of(UserUpdateRequest.builder().nickName("nickName").email("email@email.com").roadNameAddress("roadNameAddress").detailedAddress(null).postalCode("postalCode").build(),
                            "상세 주소는 존재해야합니다.",
                            "detailedAddress",
                            "공백인 경우"),
                    Arguments.of(UserUpdateRequest.builder().nickName("nickName").email("email@email.com").roadNameAddress("roadNameAddress").detailedAddress("detailedAddress").postalCode(null).build(),
                            "우편번호는 존재해야합니다.",
                            "postalCode",
                            "공백인 경우")
            );
        }

        @ParameterizedTest(name = "[{index}] - {2}이(가) {3}")
        @MethodSource("UpdateUserBindingFailScenarios")
        @DisplayName("실패 - Binding Error 발생")
        void error4(UserUpdateRequest request,String ErrorMessage,String property, String cause) throws Exception {


            mockMvc.perform(patch("/api/v1/users/" + userId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(ErrorMessage));

        }
    }

    @Nested
    @DisplayName("회원 알림 조회")
    class getAlarmsTest {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            AlarmGetListResponse alarmGetListResponse = AlarmFixture.createAlarmGetListResponse();

            given(alarmService.getAlarms(userName))
                    .willReturn(List.of(alarmGetListResponse));

            mockMvc.perform(get("/api/v1/users/alarms")
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result[0].alarmId").exists())
                    .andExpect(jsonPath("$.result[0].fromUserNickName").exists())
                    .andExpect(jsonPath("$.result[0].postName").exists())
                    .andExpect(jsonPath("$.result[0].postId").exists())
                    .andExpect(jsonPath("$.result[0].text").exists())
                    .andExpect(jsonPath("$.result[0].createdAt").exists());

        }

        @Test
        @DisplayName("실패 - jwt claim 에 있는 회원 정보로 회원을 찾을 수 없습니다.")
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
    @DisplayName("회원 알림 삭제")
    class deleteAlarmTest {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            willDoNothing()
                    .given(alarmService)
                    .delete(userName, alarmId);

            mockMvc.perform(delete("/api/v1/users/alarms/" + alarmId)
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").value("complete"));

        }

        private static Stream<Arguments> testCasesOfDeleteAlarm() {
            return Stream.of(
                    Arguments.of(USER_NOT_FOUND, 404, "가입된 회원이 아닙니다."),
                    Arguments.of(ALARM_NOT_FOUND, 404, "알림 데이터를 찾을 수 없습니다.")
            );
        }

        @ParameterizedTest(name = "[{index}] - {2}")
        @MethodSource("testCasesOfDeleteAlarm")
        @DisplayName("실패")
        void error1(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {

            doThrow(new AppException(errorCode))
                    .when(alarmService)
                    .delete(userName, alarmId);

            mockMvc.perform(delete("/api/v1/users/alarms/" + alarmId)
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