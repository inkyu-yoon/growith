package com.growith.controller;

import com.growith.domain.order.dto.OrderCreateRequest;
import com.growith.domain.order.dto.OrderResponse;
import com.growith.domain.post.dto.PostCreateRequest;
import com.growith.domain.post.dto.PostResponse;
import com.growith.global.Response;
import com.growith.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.cloud.contract.spec.internal.HttpStatus.CREATED;

@RestController
@Slf4j
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Response<OrderResponse>> create(Authentication authentication, @Validated @RequestBody OrderCreateRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        OrderResponse response = orderService.order(userName, requestDto);
        return ResponseEntity.status(CREATED).body(Response.success(response));
    }
}
