package com.growith.service.webclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.growith.domain.user.oauth.UserProfile;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWireMock
class WebClientServiceTest2 {

    private WireMockServer wireMockServer;

    @Autowired
    private WebClientService webClientService;

    @BeforeEach
    void init(){
        this.wireMockServer = new WireMockServer();
        this.wireMockServer.start();

        String baseUrl = String.format("http://localhost:%s", this.wireMockServer.port());
        webClientService = new WebClientService(WebClient.create(baseUrl));
    }

    @AfterEach
    void shutDown(){
        this.wireMockServer.stop();
    }

    @Nested
    @DisplayName("AccessToken 가져오기 테스트")
    class getToken {

        @Test
        @DisplayName("AccessToken 가져오기 성공 테스트")
        public void Success(){
            wireMockServer.stubFor(post(urlEqualTo("/"))
                    .willReturn(
                    aResponse()
                            .withBody("access_token=token")
            ));
            String code = "code";
            String accessToken = webClientService.getAccessToken(code, String.format("http://localhost:%s/", wireMockServer.port()));
            System.out.println(accessToken);

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

            wireMockServer.stubFor(get(urlEqualTo("/"))
                    .willReturn(
                    aResponse()
                            .withBody("access_token=token")
                            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .withBody(response)
            ));

            String accessToken = "AccessToken";

            UserProfile result = webClientService.getUserInfo(accessToken, String.format("http://localhost:%s/", wireMockServer.port()));

            assertThat(result.getName()).isEqualTo(name);
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getImageUrl()).isEqualTo(imageUrl);
            assertThat(result.getBlog()).isEqualTo(blog);

        }
    }
}