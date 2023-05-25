package com.growith.controller;

import com.growith.domain.product.dto.ProductAddRequest;
import com.growith.domain.product.dto.ProductResponse;
import com.growith.global.Response;
import com.growith.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.cloud.contract.spec.internal.HttpStatus.CREATED;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Response<ProductResponse>> create(Authentication authentication, @Validated @RequestBody ProductAddRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        ProductResponse response = productService.addProduct(userName, requestDto);
        return ResponseEntity.status(CREATED).body(Response.success(response));
    }
}
