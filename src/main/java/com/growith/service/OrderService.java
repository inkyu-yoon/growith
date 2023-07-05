package com.growith.service;

import com.growith.domain.order.OrderRepository;
import com.growith.domain.order.Orders;
import com.growith.domain.order.dto.OrderCreateRequest;
import com.growith.domain.order.dto.OrderResponse;
import com.growith.domain.orderProduct.OrderProduct;
import com.growith.domain.orderProduct.OrderProductRepository;
import com.growith.domain.product.Product;
import com.growith.domain.product.ProductRepository;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.global.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.growith.global.exception.ErrorCode.PRODUCT_NOT_FOUND;
import static com.growith.global.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderResponse order(String userName, OrderCreateRequest requestDto) {
        User foundUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        Product foundProduct = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new AppException(PRODUCT_NOT_FOUND));

        Orders order = Orders.of(foundUser);

        OrderProduct orderProduct = OrderProduct.of(order, foundProduct, foundUser, requestDto.getQuantity());

        orderRepository.save(order);
        orderProductRepository.save(orderProduct);

        return OrderResponse.builder()
                .orderId(order.getId())
                .build();
    }


}
