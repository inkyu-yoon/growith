package com.growith.service.webclient;

import com.google.gson.Gson;
import com.growith.domain.user.oauth.UserProfile;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

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
    void init() {
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        webClientService = new WebClientService(WebClient.create(baseUrl));
    }

    @AfterAll
    public static void shutdown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("AccessToken 가져오기 테스트")
    class getAccessToken {

        @Test
        @DisplayName("AccessToken 가져오기 성공 테스트")
        public void getAccessTokenSuccess() {
            String expectedToken = "token";
            String expectedResponse = String.format("access_token=%s&expires_in={값}&refresh_token={값}&refresh_token_expires_in={값}&scope=&token_type={값}", expectedToken);
            mockWebServer.enqueue(new MockResponse()
                    .setBody(expectedResponse));

            String code = "code";

            String accessToken = webClientService.getAccessToken(code, "/login/oauth/access_token");

            assertThat(accessToken).isEqualTo(expectedToken);

        }
    }

    @Nested
    @DisplayName("github user 정보 가져오기 테스트")
    class getUserInfo {

        Gson gson = new Gson();

        static class GithubResponse {
            private String email;
            private String name;
            private String avatar_url;
            private String blog;

            public GithubResponse(String email, String name, String imageUrl, String blog) {
                this.email = email;
                this.name = name;
                this.avatar_url = imageUrl;
                this.blog = blog;
            }
        }

        @Test
        @DisplayName("github user 정보 가져오기 성공 테스트")
        public void getUserInfo() {

            String response = gson.toJson(new GithubResponse("email", "name", "imageUrl", "blog"));

            mockWebServer.enqueue(new MockResponse()
                    .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .setBody(response));

            String accessToken = "AccessToken";

            UserProfile result = webClientService.getUserInfo(accessToken, "/user");

            assertThat(result.getName()).isEqualTo("name");
            assertThat(result.getEmail()).isEqualTo("email");
            assertThat(result.getImageUrl()).isEqualTo("imageUrl");
            assertThat(result.getBlog()).isEqualTo("blog");

        }
    }
}