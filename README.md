# growith

[배포 주소(제작중)](http://ec2-35-78-175-91.ap-northeast-1.compute.amazonaws.com:8080/)

<br>

## 프로젝트 중 겪었던 이슈 및 고민
<details>
  <summary><h3> WebClient 테스트 코드를 어떻게 작성해야할까? MockWebServer vs WireMock </h3> </summary>
  

## ❓ Mock 서버를 사용하는 이유



***WebClient***를 사용해서 Github Api로 유저 정보를 받아오는 [WebClientService](https://github.com/inkyu-yoon/growith/blob/main/src/main/java/com/growith/service/webclient/WebClientService.java) 클래스가 있었고, 테스트 코드를 짜기 위해 방법을 찾아보았습니다.

방법을 찾아보니, ***Mock*** 웹 서버를 정의해서 테스트를 하는 방법이 있었습니다.

<br>

1. 실제 API 서버를 사용해서 테스트를 하면, 서버 상태에 따라 테스트의 결과가 달라질 수 있습니다.

2. Mock 서버로도 충분히 외부 API가 정상이고 정상적인 값(예상한 값)이 반환된다면, 정상적으로 로직이 작동하는 것을 보여줄 수 있습니다.

3. 로컬에 서버를 띄워 사용하기 때문에 속도도 빠르다는 장점이 있습니다.

<br>

위와 같은 장점이 있는 ***Mock*** 웹 서버를 구현하기 위해서 사용하는 라이브러리는 대표적으로 ***MockWebServer***와 ***WireMock*** 이 있습니다.

[stackOverflow의 답변](https://stackoverflow.com/questions/44383325/why-would-i-use-mockwebserver-instead-of-wiremock)에 의하면 ***WireMock***이 안드로이드에서 이슈가 있어서 나온 것이 ***MockWebServer***라서

안드로이드 환경이 아니라면 ***WireMock***이 더 좋다고 합니다.



저는 두 가지 방법 모두 사용해서 테스트 코드를 한번 작성해보았습니다.

두 방식의 공통점은 로컬에 가짜 서버를 띄운 뒤, 그 서버에 특정 요청을 했을 때의 응닶값을 가정한 뒤, 테스트 코드를 작성하는 것입니다.

## 1️⃣ MockWebServer

```
testImplementation group: 'com.squareup.okhttp3', name: 'mockwebserver', version: '5.0.0-alpha.11'
implementation group: 'com.squareup.okhttp3', name: 'okhttp', version: '5.0.0-alpha.11'
```

***mockwebserver*** 와 ***okhttp*** 라이브러리를 추가해야 합니다.

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

먼저, 생성한 ***MockWebServer***에 요청을 보내야 직접 정의한 응답을 받을 수 있을 것입니다.

따라서, ***WebClient***에 입력하는 baseUrl을 ***MockServer*** 포트로 변경합니다.

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

***enqueue()*** 메서드를 통해서 ***mockWebServer***로 요청이 들어왔을때 응답 값을 지정해줍니다.

GitHub Api는 사용자가 로그인을 통해 인증하면 받는 ***Code***를 `https://github.com/login/oauth/access_token`로 Post 요청을 보내면

```
access_token={값}&expires_in={값}&refresh_token={값}&refresh_token_expires_in={값}&scope=&token_type={값}
```

위와 같은 형식으로 응답합니다.

따라서, 위와 같은 값으로 반환한다고 가정합니다.

그리고 실제로는 `https://github.com/login/oauth/access_token` 로 요청을 보내지만, 직접 생성한 가짜 서버에 요청을 보내야 하므로 uri를 `/login/oauth/access_token`로 입력했습니다. 

테스트 상으로는`localhost:{mockWebServer의 port}/login/oauth/access_token` 로 요청이 보내질 것이고, 설정한 응답값을 기대할 수 있습니다.

[전체 테스트 코드](https://github.com/inkyu-yoon/growith/blob/main/src/test/java/com/growith/service/webclient/WebClientServiceTest.java)

<br>



## 2️⃣ WireMock

```
implementation group: 'org.springframework.cloud', name: 'spring-cloud-starter-contract-stub-runner', version: '4.0.1'
```

***WireMock***을 사용하기 위해서는 위 라이브러리를 추가합니다.

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

***WireMock*** 방식도 ***MockWebServer*** 방식과 초기 세팅은 유사합니다.

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

***WireMock*** 방식의 경우 Http Method도 지정할 수 있다는 것이 큰 차이점인 것 같습니다.

실제로 위 코드에서 `stubFor(post(urlEqualTo("/login/oauth/access_token"))`의 post를 ` stubFor(get(urlEqualTo("/login/oauth/access_token"))` get으로 변경하는 경우 테스트 실패를 하게됩니다.

왜냐하면 ***getAccessToken()*** 메서드에서는, 파라미터에 입력된 url에 post 요청을 보내기 때문입니다.

***MockWebServer*** 보다 구체적인 상황으로 테스트 코드를 작성할 수 있다는 것이 코드가 어떤 역할인지 더 잘 보여줄 수 있는 장점이 있다고 생각이 들었습니다.

[전체 테스트 코드](https://github.com/inkyu-yoon/growith/blob/main/src/test/java/com/growith/service/webclient/WebClientServiceTest2.java)

<br>

> ***WebClient*** 관련 테스트 코드를 짜는 방법을 이해하는데 어려움이 있었고 많은 시간을 투자했습니다.
> 
> ***MockWebServer*** 와 ***WireMock***에 대해 알게 되어 외부 API를 사용하는 경우 테스트 코드를 짤 수 있게 되어 값진 시간이었던 것 같습니다.
> 
> 그리고 테스트 코드를 작성하려다 보니, 덕분에 ***WebClient*** 객체를 ***Bean***으로 등록한 뒤 ***DI*** 받도록 수정할 수 있었고 요청을 보내는 uri도 파라미터로 입력받도록 리팩토링하게 되는 계기가 되었습니다.

 </details>


 <details>
  <summary><h3> @RequestParam 문자열을 Converter를 오버라이딩하여 다이렉트로 Enum 타입 매핑하기 </h3> </summary>
  


## 💡 RequestParam으로 요청된 문자열을 Enum 타입으로 매핑하기



***Post***필드에는 ***Enum***으로 관리되는 ***Category***필드가 있습니다.

그리고 저는 카테고리를 Request Parameter로 입력받아 카테고리에 해당하는 게시글들을 반환하는 GET 요청 API를 만들려고 했습니다.

예상하는 uri는 `/api/v1/posts/categories?category=qna` 와 같을 것입니다.

```java
@RestController
@Slf4j
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostApiController {

    @GetMapping("/categories")
    public ResponseEntity<Response<Page<PostGetListResponse>>> getAllByCategory(@RequestParam String requestCategory, Pageable pageable){
        Category category = Category.valueOf(requestCategory)
        Page<PostGetListResponse> response = postService.getAllPostsByCategory(category, pageable);
        return ResponseEntity.ok(Response.success(response));
    }
}

```

따라서 위 코드와 같이 ***RequestParam***은 ***String***값으로 입력받고, 입력받은 값을 ***Enum***타입으로 변환시켜야 했습니다.

<br>

이 변환 과정 때문에 코드가 많이 추가되어 길어지는 것은 아니지만, 

입력받음과 동시에 처리된다면 코드 가독성 향상과 핵심 로직만 메서드 안에 담을 수 있을 것이라 생각되어 방법을 찾아봤습니다.

<br>

```java
public enum Category {
    QNA, COMMUNITY, STUDY, NOTICE;

    public static Category create(String requestCategory) {
        for (Category value : Category.values()) {
            if (value.toString().equals(requestCategory)) {
                return value;
            }
        }
        throw new IllegalStateException("일치하는 카테고리가 존재하지 않습니다.");
    }
}

```

먼저 ***Enum***타입인 ***Category***안에 ***String***타입인 ***requestCategory***를 ***Category***로 만들어주는 메서드를 정의합니다.

<br>

```java
public class PostEnumConverter implements Converter<String, Category> {

    @Override
    public Category convert(String requestCategory) {
        return Category.create(requestCategory.toUpperCase());
    }
}
```

그리고 `import org.springframework.core.convert.converter.Converter;` 에 있는 ***Converter*** 의 ***convert()*** 메서드를 오버라이딩합니다.

Request Parameter로 입력되는 문자열을 ***toUpperCase()*** 를 이용해서 모든 문자를 대문자로 바꿔준 후 넘겨줍니다.

<br>

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new PostEnumConverter());
    }
}
```

그리고 ***WebConfiguer***의 ***addFormatters()*** 메서드를 오버라이딩하여 만든 ***Converter***를 추가해줍니다.

이제 잘 동작하는지 확인을 해봅니다.

`/api/v1/posts/categories?category=QNA` 나 `/api/v1/posts/categories?category=qna` 로 요청했을 때, 잘 동작하는 것이 확인이 되지만,

`/api/v1/posts/categories?category=` 라던지 `/api/v1/posts/categories?category=hi` 와 같은 잘못된 요청 시, 에러 핸들링이 되지 않음을 확인했습니다.

<br>

로그를 보니 `/api/v1/posts/categories?category=`  요청처럼 존재하지 않는 문자열로 요청을 하는 경우에는 ***MissingServletRequestParameterException*** 에러가, 

 `/api/v1/posts/categories?category=hi`  요청처럼 ***Enum*** 으로 존재하지 않는 값을 요청하는 경우에는 ***MethodArgumentTypeMismatchException*** 에러가 발생하는 것을 확인했습니다.

```java
@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> converterExceptionHandler(MissingServletRequestParameterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error(ErrorCode.REQUEST_PARAM_NOT_MATCH.getMessage()));
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> converterSecondExceptionHandler(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error(ErrorCode.REQUEST_PARAM_NOT_MATCH.getMessage()));
    }
}
```

따라서 위와 같이 에러를 핸들링하는 ***ExceptionManager*** 를 통해 에러 처리를 진행했습니다.

<div align="center">
<img src="https://raw.githubusercontent.com/buinq/imageServer/main/img/image-20230401173431514.png" alt="image-20230401173431514" height="300" />
</div>

정상적으로 에러 응답을 확인할 수 있게 되었습니다.

```java
    @GetMapping("/categories")
    public ResponseEntity<Response<Page<PostGetListResponse>>> getAllByCategory(@RequestParam Category category, Pageable pageable) {
        Page<PostGetListResponse> response = postService.getAllPostsByCategory(category, pageable);
        return ResponseEntity.ok(Response.success(response));
    }
```

<br>

> 덕분에 Controller의 메서드가 훨씬 깔끔해져 가독성이 향상되었고, 핵심 로직에만 집중할 수 있게 된 것 같습니다. 


 </details>

<details>
  <summary><h3> Request Dto에 있는 Enum 타입 필드 validation 예외처리 개선하기  </h3> </summary>
 
 ## 💡 Request Dto에 있는 Enum 타입 필드 validation 예외처리 개선하기

<br>

```
implementation 'org.springframework.boot:spring-boot-starter-validation'
```



위 ***validation*** 라이브러리를 사용하면

Controller에서 ***@RequestBody*** 어노테이션으로 매핑하는 request dto의 필드 유효성을 검사할 수 있습니다.



```java
public class PostCreateRequest {
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;
    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;
    @NotNull(message = "유효하지 않은 카테고리가 입력되었습니다.")
    private Category category;
}
```



위와 같이 매핑되는 필드에 ***validation***에서 제공하는 어노테이션을 사용하면 됩니다.

예를 들어, 위 ***Category*** 필드에 사용한 ***@NotNull*** 어노테이션의 경우 ***null***이 입력되는 것을 허용하지 않습니다.

<br>

```
Resolved [org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Cannot deserialize value of type `com.growith.domain.post.Category` from String "category": not one of the values accepted for Enum class: [NOTICE, STUDY, QNA, COMMUNITY]
```

그리고 ***BindingError***를 확인하기 위해 테스트 코드를 작성한 뒤, 실행시켜보니 문제가 발생했습니다.

<br>

```java
public enum Category {
    QNA, COMMUNITY, STUDY, NOTICE;
    }
}
```

카테고리에는 다음과 같은 상수들이 존재하는데,

<br>

***null*** 값이 입력되는 상황은 처리할 수 있었지만, Enum으로 정의되지 않은 문자열(위 예시에선 category:"category")이 입력됐을 때는 처리가 되지 않은 상황이었습니다.

또한, "qna" 와 같이 소문자로 입력되는 경우에도 처리되지 않았습니다.

그래서 대소문자는 구별하지 않고 일치하면 정상적으로 로직이 실행되고, 

null이거나 enum으로 정의하지 않은 값이 들어오는 경우만 예외처리가 되도록 구현하였습니다.

<br>

```java
public enum Category {
    QNA, COMMUNITY, STUDY, NOTICE;
    }

    @JsonCreator
    public static Category parsing(String inputValue) {
        return Stream.of(Category.values())
                .filter(category -> category.toString().equals(inputValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
```

방법은 enum 객체 안에 ***@JsonCreator*** 어노테이션과 생성자를 구현하여 Json데이터를 역직렬화 하는 과정을 수동 설정하는 것입니다.

category 를 키값으로 하는 value값을 파라미터로서 ***inputValue***값으로 받아와서 로직이 수행됩니다.

입력되는 ***inputValue***값을 ***toUpperCase()*** 메서드로 대문자로 변환 후, 

일치하는 Category가 있다면 반환하고 없다면 ***null***을 반환하도록 했습니다.

null이 반환되면 validation으로 설정한 ***@NotNull***어노테이션에 의해 ***BindingError***가 발생할 것입니다.

### 결과

<div align="center">
<img src="https://raw.githubusercontent.com/buinq/imageServer/main/img/image-20230404192343858.png" alt="image-20230404192343858" style="zoom: 50%;" />
</div>

> 목표했던 대로 예외처리가 된 것을 확인할 수 있었고, 에러 응답을 통해 어떤 에러가 발생했는지 명시할 수 있게 되었습니다.


 </details>
