package com.growith.domain.Image.product;

import com.growith.domain.BaseEntity;
import com.growith.domain.Image.S3FileInfo;
import com.growith.domain.product.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date is NULL")
@SQLDelete(sql = "UPDATE productimage SET deleted_date = current_timestamp WHERE id = ?")
public class ProductImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;


    public static ProductImage of(S3FileInfo s3FileInfo, Product product) {
        return ProductImage.builder()
                .product(product)
                .imageUrl(s3FileInfo.getImageUrl())
                .fileName(s3FileInfo.getFileName())
                .build();
    }
}
