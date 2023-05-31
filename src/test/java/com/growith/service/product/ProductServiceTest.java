package com.growith.service.product;

import com.growith.domain.product.Product;
import com.growith.domain.product.ProductRepository;
import com.growith.domain.product.dto.ProductAddRequest;
import com.growith.domain.product.dto.ProductUpdateRequest;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
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

    private Long productId;
    private String userName;
    private String productName;
    private Long productQuantity;
    private Long price;
    private String productImageUrl;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        productId = 1L;
        userName = "userName";
        productName = "productName";
        productQuantity = 1L;
        price = 10000L;
        productImageUrl = "imageUrl";

        pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
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
        public void addProductError1() {

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
        public void addProductError2() {

            when(userRepository.findByUserName(userName))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> productService.addProduct(userName, productAddRequest));

            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
        }
    }

    @Nested
    @DisplayName("상품 조회 테스트")
    class GetProduct {

        @Mock
        ProductAddRequest productAddRequest;

        @Test
        @DisplayName("상품 조회 성공 테스트")
        public void getProductSuccess() {
            given(productRepository.findById(productId))
                    .willReturn(Optional.of(mockProduct));

            assertDoesNotThrow(() -> productService.getProduct(productId));

            verify(productRepository, atLeastOnce()).findById(productId);

        }

        @Test
        @DisplayName("상품 조회 실패 테스트 (등록된 상품을 찾을 수 없는 경우)")
        public void getProductError() {

            when(productRepository.findById(productId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> productService.getProduct(productId));

            assertThat(appException.getErrorCode()).isEqualTo(PRODUCT_NOT_FOUND);
            verify(productRepository, atLeastOnce()).findById(productId);

        }

        @Test
        @DisplayName("상품 페이지 단위 조회 성공 테스트")
        public void getProductsSuccess() {
            given(productRepository.findAllByOrderByCreatedDateDesc(pageable))
                    .willReturn(new PageImpl<>(List.of(mockProduct)));

            assertDoesNotThrow(() -> productService.getProducts(pageable));

            verify(productRepository, atLeastOnce()).findAllByOrderByCreatedDateDesc(pageable);

        }
    }

    @Nested
    @DisplayName("상품 삭제 테스트")
    class deleteProduct {

        @Test
        @DisplayName("상품 삭제 성공 테스트")
        public void deleteProductSuccess() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            willDoNothing()
                    .given(mockUser)
                    .checkAdmin();
            given(productRepository.findById(productId))
                    .willReturn(Optional.of(mockProduct));


            assertDoesNotThrow(() -> productService.deleteProduct(userName, productId));

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(mockUser, atLeastOnce()).checkAdmin();
            verify(productRepository, atLeastOnce()).findById(productId);

        }

        @Test
        @DisplayName("상품 삭제 실패 테스트 (요청한 회원을 찾을 수 없는 경우)")
        public void deleteProductError1() {
            when(userRepository.findByUserName(userName))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> productService.deleteProduct(userName, productId));

            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
        }

        @Test
        @DisplayName("상품 삭제 실패 테스트 (관리자 계정이 요청하지 않은 경우)")
        public void deleteProductError2() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));

            doThrow(new AppException(ALLOWED_ONLY_ADMIN))
                    .when(mockUser)
                    .checkAdmin();

            AppException appException = assertThrows(AppException.class, () -> productService.deleteProduct(userName, productId));

            assertThat(appException.getErrorCode()).isEqualTo(ALLOWED_ONLY_ADMIN);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(mockUser, atLeastOnce()).checkAdmin();
        }

        @Test
        @DisplayName("상품 삭제 실패 테스트 (상품이 존재하지 않은 경우)")
        public void deleteProductError3() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            willDoNothing()
                    .given(mockUser)
                    .checkAdmin();

            when(productRepository.findById(productId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> productService.deleteProduct(userName, productId));

            assertThat(appException.getErrorCode()).isEqualTo(PRODUCT_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(mockUser, atLeastOnce()).checkAdmin();
            verify(productRepository, atLeastOnce()).findById(productId);
        }
    }

    @Nested
    @DisplayName("상품 수정 테스트")
    class updateProduct {

        @Mock
        ProductUpdateRequest productUpdateRequest;

        @Test
        @DisplayName("상품 수정 성공 테스트")
        public void updateProductSuccess() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            willDoNothing()
                    .given(mockUser)
                    .checkAdmin();
            given(productRepository.findById(productId))
                    .willReturn(Optional.of(mockProduct));


            assertDoesNotThrow(() -> productService.updateProduct(productUpdateRequest, userName, productId));

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(mockUser, atLeastOnce()).checkAdmin();
            verify(productRepository, atLeastOnce()).findById(productId);

        }

        @Test
        @DisplayName("상품 삭제 실패 테스트 (요청한 회원을 찾을 수 없는 경우)")
        public void deleteProductError1() {
            when(userRepository.findByUserName(userName))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> productService.updateProduct(productUpdateRequest, userName, productId));

            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
        }

        @Test
        @DisplayName("상품 삭제 실패 테스트 (관리자 계정이 요청하지 않은 경우)")
        public void deleteProductError2() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));

            doThrow(new AppException(ALLOWED_ONLY_ADMIN))
                    .when(mockUser)
                    .checkAdmin();

            AppException appException = assertThrows(AppException.class, () -> productService.updateProduct(productUpdateRequest, userName, productId));

            assertThat(appException.getErrorCode()).isEqualTo(ALLOWED_ONLY_ADMIN);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(mockUser, atLeastOnce()).checkAdmin();
        }

        @Test
        @DisplayName("상품 삭제 실패 테스트 (상품이 존재하지 않은 경우)")
        public void deleteProductError3() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            willDoNothing()
                    .given(mockUser)
                    .checkAdmin();

            when(productRepository.findById(productId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> productService.updateProduct(productUpdateRequest, userName, productId));

            assertThat(appException.getErrorCode()).isEqualTo(PRODUCT_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(mockUser, atLeastOnce()).checkAdmin();
            verify(productRepository, atLeastOnce()).findById(productId);
        }
    }
}