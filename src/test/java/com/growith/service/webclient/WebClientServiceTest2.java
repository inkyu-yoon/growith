package com.growith.service.webclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.gson.Gson;
import com.growith.domain.user.oauth.UserProfile;
import com.growith.service.WebClientService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWireMock
class WebClientServiceTest2 {

    private static WireMockServer wireMockServer;

    @Autowired
    private WebClientService webClientService;
    @BeforeAll
    public static void setUp() throws IOException {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
    }

    @BeforeEach
    void init(){
        String baseUrl = String.format("http://localhost:%s", wireMockServer.port());
        webClientService = new WebClientService(WebClient.create(baseUrl));
    }

    @AfterAll
    public static void stop(){
        wireMockServer.stop();
    }

    @Nested
    @DisplayName("AccessToken 가져오기 테스트")
    class getToken {

        @Test
        @DisplayName("AccessToken 가져오기 성공 테스트")
        public void Success(){
            String expectedToken = "token";
            String expectedResponse = String.format("access_token=%s&expires_in={값}&refresh_token={값}&refresh_token_expires_in={값}&scope=&token_type={값}", expectedToken);

            wireMockServer.stubFor(post(urlEqualTo("/login/oauth/access_token"))
                    .willReturn(
                    aResponse()
                            .withBody(expectedResponse)
            ));
            String code = "code";
            String accessToken = webClientService.getAccessToken(code, "/login/oauth/access_token");
            System.out.println(accessToken);

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
        public void getUserInfo(){
            String response = gson.toJson(new GithubResponse("userName", "imageUrl", "blog", "githubUrl"));

            wireMockServer.stubFor(get(urlEqualTo("/user"))
                    .willReturn(
                    aResponse()
                            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .withBody(response)
            ));

            String accessToken = "AccessToken";

            UserProfile result = webClientService.getUserInfo(accessToken,"/user");

            assertThat(result.getUserName()).isEqualTo("userName");
            assertThat(result.getImageUrl()).isEqualTo("imageUrl");
            assertThat(result.getBlog()).isEqualTo("blog");
            assertThat(result.getGithubUrl()).isEqualTo("githubUrl");

        }
    }
}