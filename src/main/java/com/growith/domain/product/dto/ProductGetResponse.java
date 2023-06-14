package com.growith.domain.product.dto;

import com.growith.domain.Image.product.ProductImage;
import com.growith.domain.product.Product;
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

    public ProductGetResponse(Product product, ProductImage productImage) {
        this.productId = product.getId();
        this.name = product.getName();
        this.quantity = product.getQuantity();
        this.price = product.getPrice();
        this.imageUrl = productImage.getImageUrl();
    }
}
