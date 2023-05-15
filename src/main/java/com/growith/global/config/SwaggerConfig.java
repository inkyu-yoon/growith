package com.growith.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi all() {
        String[] paths = {"/api/v1/**"};

        return GroupedOpenApi.builder()
                .group("All API of Growith")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi user() {
        String[] paths = {"/api/v1/users/**"};

        return GroupedOpenApi.builder()
                .group("User API v1")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi product() {
        String[] paths = {"/api/v1/posts/**"};

        return GroupedOpenApi.builder()
                .group("Post API v1")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .version("v1.0.0")
                .title("Growith App API Test Swagger")
                .description("권한이 필요한 API는 로그인 후, 쿠키에 JWT가 등록되고 사용 가능합니다. <br>[깃허브 로그인 후 JWT 권한 받기](https://github.com/login/oauth/authorize?client_id=Iv1.a2583258e956bcda) <br> [배포 주소](http://49.50.162.219:8080/)");



        return new OpenAPI()
                .info(info);
    }
}