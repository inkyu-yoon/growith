package com.growith.domain.product.dto;

import com.growith.domain.product.Product;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateRequest {

    @NotNull
    private String name;

    @Positive
    private Long quantity;

    @Positive
    private Long price;



    public Product toEntity() {
        return Product.builder()
                .name(this.name)
                .quantity(this.quantity)
                .price(this.price)
                .build();
    }
}
