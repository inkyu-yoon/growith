package com.growith.controller;

import com.growith.domain.product.dto.ProductAddRequest;
import com.growith.domain.product.dto.ProductGetResponse;
import com.growith.domain.product.dto.ProductResponse;
import com.growith.domain.product.dto.ProductUpdateRequest;
import com.growith.global.Response;
import com.growith.global.specification.ProductApiSpecification;
import com.growith.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.cloud.contract.spec.internal.HttpStatus.CREATED;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductApiController implements ProductApiSpecification {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Response<ProductResponse>> create(Authentication authentication, @Validated @RequestBody ProductAddRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        ProductResponse response = productService.addProduct(userName, requestDto);
        return ResponseEntity.status(CREATED).body(Response.success(response));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Response<ProductGetResponse>> getProduct(@PathVariable(name = "productId") Long productId) {
        ProductGetResponse response = productService.getProduct(productId);
        return ResponseEntity.ok(Response.success(response));
    }


    @GetMapping
    public ResponseEntity<Response<Page<ProductGetResponse>>> getProducts(Pageable pageable) {
        Page<ProductGetResponse> response = productService.getProducts(pageable);
        return ResponseEntity.ok(Response.success(response));
    }


    @DeleteMapping("/{productId}")
    public ResponseEntity<Response<ProductResponse>> deleteProduct(Authentication authentication, @PathVariable(name = "productId") Long productId) {
        String userName = authentication.getName();
        ProductResponse response = productService.deleteProduct(userName, productId);
        return ResponseEntity.ok(Response.success(response));
    }


    @PutMapping("/{productId}")
    public ResponseEntity<Response<ProductResponse>> updateProduct(Authentication authentication, @PathVariable(name = "productId") Long productId, @Validated @RequestBody ProductUpdateRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        ProductResponse response = productService.updateProduct(requestDto, userName, productId);
        return ResponseEntity.ok(Response.success(response));
    }
}
