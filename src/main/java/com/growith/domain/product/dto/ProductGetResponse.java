package com.growith.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductGetResponse {
    private Long productId;
    private String name;
    private Long quantity;
    private Long price;
    private String imageUrl;
}
