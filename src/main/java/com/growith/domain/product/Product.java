package com.growith.domain.product;

import com.growith.domain.BaseEntity;
import com.growith.domain.product.dto.ProductResponse;
import jakarta.persistence.*;
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

    private String imageUrl;

    @Builder
    public Product(String name, Long quantity, Long price, String imageUrl) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public ProductResponse toProductResponse() {
        return ProductResponse.builder()
                .productId(this.id)
                .name(this.name)
                .build();
    }

}
