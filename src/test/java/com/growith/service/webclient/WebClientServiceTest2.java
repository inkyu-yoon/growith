package com.growith.service.webclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.gson.Gson;
import com.growith.domain.user.oauth.UserProfile;
import mockwebserver3.MockWebServer;
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
        public void getUserInfo(){
            String response = gson.toJson(new GithubResponse("email", "name", "imageUrl", "blog"));

            wireMockServer.stubFor(get(urlEqualTo("/user"))
                    .willReturn(
                    aResponse()
                            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .withBody(response)
            ));

            String accessToken = "AccessToken";

            UserProfile result = webClientService.getUserInfo(accessToken,"/user");

            assertThat(result.getName()).isEqualTo("name");
            assertThat(result.getEmail()).isEqualTo("email");
            assertThat(result.getImageUrl()).isEqualTo("imageUrl");
            assertThat(result.getBlog()).isEqualTo("blog");

        }
    }
}