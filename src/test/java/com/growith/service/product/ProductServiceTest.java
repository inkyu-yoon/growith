package com.growith.service.product;

import com.growith.domain.product.Product;
import com.growith.domain.product.ProductRepository;
import com.growith.domain.product.dto.ProductAddRequest;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.global.exception.AppException;
import com.growith.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.growith.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ProductService productService;

    @Mock
    User mockUser;

    @Mock
    Product mockProduct;

    private String userName;
    private String productName;
    private Long productQuantity;
    private Long price;
    private String productImageUrl;

    @BeforeEach
    public void setUp() {
        userName = "userName";
        productName = "productName";
        productQuantity = 1L;
        price = 10000L;
        productImageUrl = "imageUrl";
    }

    @Nested
    @DisplayName("상품 등록 테스트")
    class AddProduct {

        @Mock
        ProductAddRequest productAddRequest;

        @Test
        @DisplayName("상품 등록 성공 테스트")
        public void addProductSuccess() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            willDoNothing()
                    .given(mockUser)
                    .checkAdmin();
            given(productAddRequest.toEntity())
                    .willReturn(mockProduct);
            given(productRepository.save(mockProduct))
                    .willReturn(mockProduct);

            assertDoesNotThrow(() -> productService.addProduct(userName, productAddRequest));

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(mockUser, atLeastOnce()).checkAdmin();
            verify(productAddRequest, atLeastOnce()).toEntity();
            verify(productRepository, atLeastOnce()).save(mockProduct);

        }

        @Test
        @DisplayName("상품 등록 실패 테스트 (요청한 회원을 찾을 수 없는 경우)")
        public void addProductError1(){

            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));

            doThrow(new AppException(ALLOWED_ONLY_ADMIN))
                    .when(mockUser)
                    .checkAdmin();

            AppException appException = assertThrows(AppException.class, () -> productService.addProduct(userName, productAddRequest));

            assertThat(appException.getErrorCode()).isEqualTo(ALLOWED_ONLY_ADMIN);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(mockUser, atLeastOnce()).checkAdmin();
        }

        @Test
        @DisplayName("상품 등록 실패 테스트 (관리자 권한이 없는 경우)")
        public void addProductError2(){

            when(userRepository.findByUserName(userName))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> productService.addProduct(userName, productAddRequest));

            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
        }
    }
}