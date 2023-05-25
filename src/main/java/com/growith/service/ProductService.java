package com.growith.service;

import com.growith.domain.product.Product;
import com.growith.domain.product.ProductRepository;
import com.growith.domain.product.dto.ProductAddRequest;
import com.growith.domain.product.dto.ProductResponse;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.global.exception.AppException;
import com.growith.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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


}
