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
            private String login;
            private String avatar_url;
            private String blog;
            private String html_url;

            public GithubResponse(String login, String avatar_url, String blog, String html_url) {
                this.login = login;
                this.avatar_url = avatar_url;
                this.blog = blog;
                this.html_url = html_url;
            }
        }
        @Test
        @DisplayName("github user 정보 가져오기 성공 테스트")
        public void getUserInfoSuccess() {

            String response = gson.toJson(new GithubResponse("userName", "imageUrl", "blog", "githubUrl"));

            mockWebServer.enqueue(new MockResponse()
                    .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .setBody(response));

            String accessToken = "AccessToken";

            UserProfile result = webClientService.getUserInfo(accessToken, "/user");

            assertThat(result.getUserName()).isEqualTo("userName");
            assertThat(result.getImageUrl()).isEqualTo("imageUrl");
            assertThat(result.getBlog()).isEqualTo("blog");
            assertThat(result.getGithubUrl()).isEqualTo("githubUrl");

        }
    }
}