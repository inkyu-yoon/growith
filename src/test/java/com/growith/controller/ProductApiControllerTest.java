package com.growith.controller;

import com.google.gson.Gson;
import com.growith.domain.product.dto.ProductAddRequest;
import com.growith.domain.product.dto.ProductResponse;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRole;
import com.growith.global.aop.BindingCheck;
import com.growith.global.config.SecurityConfig;
import com.growith.global.exception.AppException;
import com.growith.global.exception.ErrorCode;
import com.growith.global.util.JwtUtil;
import com.growith.service.ProductService;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ProductApiController.class)
@EnableAspectJAutoProxy
@Import({SecurityConfig.class, BindingCheck.class})
class ProductApiControllerTest {

    @MockBean
    UserDetailsService userDetailsService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    Gson gson;

    @MockBean
    ProductService productService;

    @Value("${jwt.secret}")
    String secretKey;

    String userName;
    String comment;
    String token;
    Cookie cookie;

    Long productId;
    String productName;
    Long productQuantity;
    Long price;
    String productImageUrl;

    ProductAddRequest productAddRequest;
    ProductResponse productResponse;

    @BeforeEach
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        given(userDetailsService.loadUserByUsername(anyString()))
                .willReturn(User.builder()
                        .userName("userName")
                        .email("email")
                        .point(0L)
                        .blog("blog")
                        .githubUrl("githubUrl")
                        .nickName("nickName")
                        .imageUrl("imageUrl")
                        .userRole(UserRole.ROLE_USER)
                        .build());

        productId = 1L;
        productName = "productName";
        productQuantity = 1L;
        price = 10000L;
        productImageUrl = "imageUrl";
        userName = "userName";

        token = JwtUtil.createToken(1L,userName, "ROLE_USER", secretKey, 1000L * 60 * 60);
        cookie = new Cookie("jwt", token);

        productAddRequest = ProductAddRequest.builder()
                .name(productName)
                .quantity(productQuantity)
                .imageUrl(productImageUrl)
                .price(price)
                .build();

        productResponse = ProductResponse.builder()
                .productId(productId)
                .name(productName)
                .build();

    }

    @Nested
    @DisplayName("상품 등록 테스트")
    class AddProductTest {

        @Test
        @DisplayName("상품 등록 성공 테스트")
        void AddProductSuccess() throws Exception {
            given(productService.addProduct(anyString(), any()))
                    .willReturn(productResponse);

            mockMvc.perform(post("/api/v1/products")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(productAddRequest)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.productId").value(1))
                    .andExpect(jsonPath("$.result.name").value(productName));

        }

        private static Stream<Arguments> testCasesOfAddProduct() {
            return Stream.of(
                    Arguments.of(ErrorCode.USER_NOT_FOUND),
                    Arguments.of(ErrorCode.ALLOWED_ONLY_ADMIN)
            );
        }

        @DisplayName("상품 등록 실패 테스트")
        @ParameterizedTest
        @MethodSource("testCasesOfAddProduct")
        void AddProductError(ErrorCode errorCode) throws Exception {
            when(productService.addProduct(anyString(), any()))
                    .thenThrow(new AppException(errorCode));

            mockMvc.perform(post("/api/v1/products")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(productAddRequest)))
                    .andDo(print())
                    .andExpect(status().is(errorCode.getHttpStatus().value()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorCode.getMessage()));

        }

    }


}