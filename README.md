# growith


## 프로젝트 중 겪었던 이슈 및 고민
<details>
  <summary><h3> WebClient 테스트 코드를 어떻게 작성해야할까? MockWebServer vs WireMock </h3> </summary>
  

## ❓ Mock 서버를 사용하는 이유



WebClient를 사용해서 Github Api로 유저 정보를 받아오는 [WebClientService](https://github.com/inkyu-yoon/growith/blob/main/src/main/java/com/growith/service/webclient/WebClientService.java) 클래스가 있었고, 테스트 코드를 짜기 위해 방법을 찾아보았습니다.

방법을 찾아보니, Mock 웹 서버를 정의해서 테스트를 하는 방법이 있었습니다.

<br>

1. 실제 API 서버를 사용해서 테스트를 하면, 서버 상태에 따라 테스트의 결과가 달라질 수 있습니다.

2. Mock 서버로도 충분히 외부 API가 정상이고 정상적인 값(예상한 값)이 반환된다면, 정상적으로 로직이 작동하는 것을 보여줄 수 있습니다.

3. 로컬에 서버를 띄워 사용하기 때문에 속도도 빠르다는 장점이 있습니다.

<br>

위와 같은 장점이 있는 Mock 웹 서버를 구현하기 위해서 사용하는 라이브러리는 대표적으로 **MockWebServer**와 **WireMock** 이 있습니다.

[stackOverflow의 답변](https://stackoverflow.com/questions/44383325/why-would-i-use-mockwebserver-instead-of-wiremock)에 의하면 **WireMock**이 안드로이드에서 이슈가 있어서 나온 것이 **MockWebServer**라서

안드로이드 환경이 아니라면 **WireMock**이 더 좋다고 합니다.



저는 두 가지 방법 모두 사용해서 테스트 코드를 한번 작성해보았습니다.

두 방식의 공통점은 로컬에 가짜 서버를 띄운 뒤, 그 서버에 특정 요청을 했을 때의 응닶값을 가정한 뒤, 테스트 코드를 작성하는 것입니다.

## 1️⃣ MockWebServer

```
testImplementation group: 'com.squareup.okhttp3', name: 'mockwebserver', version: '5.0.0-alpha.11'
implementation group: 'com.squareup.okhttp3', name: 'okhttp', version: '5.0.0-alpha.11'
```

**mockwebserver** 와 **okhttp** 라이브러리를 추가해야 합니다.

그리고 두 라이브러리의 버전은 일치해야 한다고 합니다.

```java
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
```

먼저, 생성한 MockWebServer에 요청을 보내야 직접 정의한 응답을 받을 수 있을 것입니다.

따라서, WebClient에 입력하는 baseUrl을 MockServer 포트로 변경합니다.

```java
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
```

**enqueue** 메서드를 통해서 mockWebServer로 요청이 들어왔을때 응답 값을 지정해줍니다.

GitHub Api는 사용자가 로그인을 통해 인증하면 받는 **Code**를 `https://github.com/login/oauth/access_token`로 Post 요청을 보내면

```
access_token={값}&expires_in={값}&refresh_token={값}&refresh_token_expires_in={값}&scope=&token_type={값}
```

위와 같은 형식으로 응답합니다.

따라서, 위와 같은 값으로 반환한다고 가정합니다.

그리고 실제로는 `https://github.com/login/oauth/access_token` 로 요청을 보내지만, 직접 생성한 가짜 서버에 요청을 보내야 하므로 uri를 `/login/oauth/access_token`로 입력했습니다. 

테스트 상으로는`localhost:{mockWebServer의 port}/login/oauth/access_token` 로 요청이 보내질 것이고, 설정한 응답값을 기대할 수 있습니다.

[전체 테스트 코드](https://github.com/inkyu-yoon/growith/blob/main/src/test/java/com/growith/service/webclient/WebClientServiceTest.java)

<br>



## 2️⃣ Wireock

```
implementation group: 'org.springframework.cloud', name: 'spring-cloud-starter-contract-stub-runner', version: '4.0.1'
```

WireMock을 사용하기 위해서는 위 라이브러리를 추가합니다.

```java

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

```

WireMock 방식도 MockWebServer 방식과 초기 세팅은 유사합니다.

```java
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
```

테스트 코드도 역시 비슷하지만,

WireMock 방식의 경우 Http Method도 지정할 수 있다는 것이 큰 차이점인 것 같습니다.

실제로 위 코드에서 ` stubFor(post(urlEqualTo("/login/oauth/access_token"))`의 post를 ` stubFor(get(urlEqualTo("/login/oauth/access_token"))` get으로 변경하는 경우 테스트 실패를 하게됩니다.

왜냐하면 `getAccessToken` 메서드에서는, 파라미터에 입력된 url에 post 요청을 보내기 때문입니다.

MockWebServer 보다 구체적인 상황으로 테스트 코드를 작성할 수 있다는 것이 코드가 어떤 역할인지 더 잘 보여줄 수 있는 장점이 있다고 생각이 들었습니다.

[전체 테스트 코드](https://github.com/inkyu-yoon/growith/blob/main/src/test/java/com/growith/service/webclient/WebClientServiceTest2.java)

<br>

> WebClient 관련 테스트 코드를 짜는 방법을 이해하는데 어려움이 있었고 많은 시간을 투자했습니다.
> 
> MockWebServer 와 WireMock에 대해 알게 되어 외부 API를 사용하는 경우 테스트 코드를 짤 수 있게 되어 값진 시간이었던 것 같습니다.
> 
> 그리고 테스트 코드를 작성하려다 보니, 덕분에 **WebClient** 객체를 **Bean**으로 등록한 뒤 DI 받도록 수정할 수 있었고 요청을 보내는 uri도 파라미터로 입력받도록 리팩토링하게 되는 계기가 되었습니다.


 </details>
