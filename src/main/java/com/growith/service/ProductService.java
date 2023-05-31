package com.growith.service;

import com.growith.domain.product.Product;
import com.growith.domain.product.ProductRepository;
import com.growith.domain.product.dto.ProductAddRequest;
import com.growith.domain.product.dto.ProductGetResponse;
import com.growith.domain.product.dto.ProductResponse;
import com.growith.domain.product.dto.ProductUpdateRequest;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.global.exception.AppException;
import com.growith.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductResponse addProduct(String userName, ProductAddRequest productAddRequest) {

        User foundUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        foundUser.checkAdmin();

        Product savedProduct = productRepository.save(productAddRequest.toEntity());

        return savedProduct.toProductResponse();
    }


    public ProductGetResponse getProduct(Long productId) {
        Product foundProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return foundProduct.toProductGetResponse();
    }

    public Page<ProductGetResponse> getProducts(Pageable pageable) {
        return productRepository.findAllByOrderByCreatedDateDesc(pageable)
                .map(product -> product.toProductGetResponse());
    }


    public ProductResponse deleteProduct(String userName, Long productId) {
        User foundUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        foundUser.checkAdmin();

        Product foundProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.delete(foundProduct);

        return foundProduct.toProductResponse();
    }

    public ProductResponse updateProduct(ProductUpdateRequest requestDto, String userName, Long productId) {
        User foundUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        foundUser.checkAdmin();

        Product foundProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        foundProduct.update(requestDto);

        return foundProduct.toProductResponse();
    }
}
