package com.growith.domain.product;

import com.growith.domain.BaseEntity;
import com.growith.domain.product.dto.ProductGetResponse;
import com.growith.domain.product.dto.ProductResponse;
import com.growith.domain.product.dto.ProductUpdateRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date is NULL")
@SQLDelete(sql = "UPDATE product SET deleted_date = current_timestamp WHERE id = ?")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long quantity;

    private Long price;


    @Builder
    public Product(String name, Long quantity, Long price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public ProductResponse toProductResponse() {
        return ProductResponse.builder()
                .productId(this.id)
                .name(this.name)
                .build();
    }

    public ProductGetResponse toProductGetResponse() {
        return ProductGetResponse.builder()
                .productId(this.id)
                .name(this.name)
                .price(this.price)
                .quantity(this.quantity)
                .build();
    }

    public void update(ProductUpdateRequest requestDto) {
        this.name = requestDto.getName();
        this.quantity = requestDto.getQuantity();
        this.price = requestDto.getPrice();
    }
}
