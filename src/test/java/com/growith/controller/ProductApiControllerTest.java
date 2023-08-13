package com.growith.controller;

import com.google.gson.Gson;
import com.growith.domain.post.dto.PostGetResponse;
import com.growith.domain.product.dto.ProductAddRequest;
import com.growith.domain.product.dto.ProductGetResponse;
import com.growith.domain.product.dto.ProductResponse;
import com.growith.domain.product.dto.ProductUpdateRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ProductApiController.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
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
    ProductGetResponse productGetResponse;
    ProductUpdateRequest productUpdateRequest;

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
                        .userRole(UserRole.ROLE_ADMIN)
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
                .price(price)
                .build();

        productResponse = ProductResponse.builder()
                .productId(productId)
                .name(productName)
                .build();

        productGetResponse = ProductGetResponse.builder()
                .productId(productId)
                .name(productName)
                .quantity(productQuantity)
                .price(price)
                .imageUrl(productImageUrl)
                .build();

        productUpdateRequest = ProductUpdateRequest.builder()
                .name(productName)
                .price(price)
                .quantity(productQuantity)
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
                    Arguments.of(ErrorCode.USER_NOT_FOUND,404,"가입된 회원이 아닙니다."),
                    Arguments.of(ErrorCode.ALLOWED_ONLY_ADMIN,401,"관리자만 요청할 수 있습니다.")
            );
        }

        @DisplayName("상품 등록 실패 테스트")
        @ParameterizedTest
        @MethodSource("testCasesOfAddProduct")
        void AddProductError(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {
            when(productService.addProduct(anyString(), any()))
                    .thenThrow(new AppException(errorCode));

            mockMvc.perform(post("/api/v1/products")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(productAddRequest)))
                    .andDo(print())
                    .andExpect(status().is(responseStatus))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

        }

    }

    @Nested
    @DisplayName("상품 조회 테스트")
    class GetProductTest {

        @Test
        @DisplayName("상품 페이지 단위 조회 성공 테스트")
        void GetProductsSuccess() throws Exception {
            List<ProductGetResponse> products = new ArrayList<>();
            products.add(productGetResponse);
            Page<ProductGetResponse> productsPage = new PageImpl<>(products);

            given(productService.getProducts(any()))
                    .willReturn(productsPage);

            mockMvc.perform(get("/api/v1/products"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.content").exists())
                    .andExpect(jsonPath("$.result.content[0].productId").value(productId))
                    .andExpect(jsonPath("$.result.content[0].name").value(productName))
                    .andExpect(jsonPath("$.result.content[0].quantity").value(productQuantity))
                    .andExpect(jsonPath("$.result.content[0].imageUrl").value(productImageUrl));
        }
        @Test
        @DisplayName("상품 단건 조회 성공 테스트")
        void GetProductSuccess() throws Exception {
            given(productService.getProduct(productId))
                    .willReturn(productGetResponse);

            mockMvc.perform(get("/api/v1/products/" + productId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.productId").value(productId))
                    .andExpect(jsonPath("$.result.name").value(productName))
                    .andExpect(jsonPath("$.result.quantity").value(productQuantity))
                    .andExpect(jsonPath("$.result.imageUrl").value(productImageUrl));
        }

        @Test
        @DisplayName("상품 등록 실패 테스트 (가입된 회원이 없는 경우)")
        void GetProductError() throws Exception {
            when(productService.getProduct(productId))
                    .thenThrow(new AppException(ErrorCode.USER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/products/" + productId))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value("가입된 회원이 아닙니다."));

        }
    }


    @Nested
    @DisplayName("상품 삭제 테스트")
    class DeleteProductTest {

        @Test
        @DisplayName("상품 삭제 성공 테스트")
        void deleteProductSuccess() throws Exception {
            given(productService.deleteProduct(anyString(), any()))
                    .willReturn(productResponse);

            mockMvc.perform(delete("/api/v1/products/" + productId)
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.productId").value(1))
                    .andExpect(jsonPath("$.result.name").value(productName));

        }

        private static Stream<Arguments> testCasesOfDeleteProduct() {
            return Stream.of(
                    Arguments.of(ErrorCode.USER_NOT_FOUND,404,"가입된 회원이 아닙니다."),
                    Arguments.of(ErrorCode.ALLOWED_ONLY_ADMIN,401,"관리자만 요청할 수 있습니다."),
                    Arguments.of(ErrorCode.PRODUCT_NOT_FOUND,404,"해당 상품 정보를 찾을 수 없습니다.")
            );
        }

        @DisplayName("상품 삭제 실패 테스트")
        @ParameterizedTest
        @MethodSource("testCasesOfDeleteProduct")
        void deleteProductError(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {
            when(productService.deleteProduct(anyString(), any()))
                    .thenThrow(new AppException(errorCode));

            mockMvc.perform(delete("/api/v1/products/" + productId)
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(status().is(responseStatus))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

        }
    }

    @Nested
    @DisplayName("상품 수정 테스트")
    class UpdateProductTest {

        @Test
        @DisplayName("상품 수정 성공 테스트")
        void updateProductSuccess() throws Exception {
            given(productService.updateProduct(any(),anyString(), any()))
                    .willReturn(productResponse);

            mockMvc.perform(put("/api/v1/products/" + productId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(productUpdateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.productId").value(1))
                    .andExpect(jsonPath("$.result.name").value(productName));
        }

        private static Stream<Arguments> testCasesOfUpdateProduct() {
            return Stream.of(
                    Arguments.of(ErrorCode.USER_NOT_FOUND,404,"가입된 회원이 아닙니다."),
                    Arguments.of(ErrorCode.ALLOWED_ONLY_ADMIN,401,"관리자만 요청할 수 있습니다."),
                    Arguments.of(ErrorCode.PRODUCT_NOT_FOUND,404,"해당 상품 정보를 찾을 수 없습니다.")
            );
        }

        @DisplayName("상품 수정 실패 테스트")
        @ParameterizedTest
        @MethodSource("testCasesOfUpdateProduct")
        void deleteProductError(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {
            when(productService.updateProduct(any(),anyString(), any()))
                    .thenThrow(new AppException(errorCode));

            mockMvc.perform(put("/api/v1/products/" + productId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(productUpdateRequest)))
                    .andDo(print())
                    .andExpect(status().is(responseStatus))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));
        }
    }
}