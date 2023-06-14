package com.growith.domain.product;

import com.growith.domain.product.dto.ProductGetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCustomRepository {

    Page<ProductGetResponse> getAllProductsWithImage(Pageable pageable);
    ProductGetResponse getProductWithImage(Long productId);
}
