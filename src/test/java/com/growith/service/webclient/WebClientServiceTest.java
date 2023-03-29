package com.growith.service.webclient;

import com.google.gson.Gson;
import com.growith.domain.user.oauth.AccessTokenRequest;
import com.growith.domain.user.oauth.UserProfile;
import com.growith.global.util.TextParsingUtil;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class WebClientServiceTest {


    private WebClientService webClientService;

    public static MockWebServer mockWebServer;

    @BeforeAll
    public static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void init(){
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        webClientService = new WebClientService(WebClient.create(baseUrl));
    }

    @AfterAll
    public static void end() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("AccessToken 가져오기 테스트")
    class getAccessToken {

        @Test
        @DisplayName("AccessToken 가져오기 성공 테스트")
        public void getAccessTokenSuccess(){

            mockWebServer.enqueue(new MockResponse()
                    .setBody("access_token=token"));

            String code = "code";

            String accessToken = webClientService.getAccessToken(code,String.format("http://localhost:%s", mockWebServer.getPort()));

            assertThat(accessToken).isEqualTo("token");

        }
    }

    @Nested
    @DisplayName("github user 정보 가져오기 테스트")
    class getUserInfo {

        @Test
        @DisplayName("github user 정보 가져오기 성공 테스트")
        public void getUserInfo(){
            String email = "email";
            String name = "name";
            String imageUrl = "imageUrl";
            String blog = "blog";
            String response = String.format("{\"email\":\"%s\",\"name\":\"%s\",\"avatar_url\":\"%s\",\"blog\":\"%s\"}",email,name,imageUrl,blog);

            System.out.println(response);
            mockWebServer.enqueue(new MockResponse()
                            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .setBody(response));

            String accessToken = "AccessToken";

            UserProfile result = webClientService.getUserInfo(accessToken, String.format("http://localhost:%s", mockWebServer.getPort()));

            assertThat(result.getName()).isEqualTo(name);
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getImageUrl()).isEqualTo(imageUrl);
            assertThat(result.getBlog()).isEqualTo(blog);

        }
    }
}