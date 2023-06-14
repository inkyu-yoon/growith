package com.growith.domain.product;

import com.growith.domain.Image.product.QProductImage;
import com.growith.domain.post.Category;
import com.growith.domain.post.PostCustomRepository;
import com.growith.domain.post.dto.PostGetListResponse;
import com.growith.domain.product.dto.ProductGetResponse;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.growith.domain.Image.product.QProductImage.*;
import static com.growith.domain.post.QPost.post;
import static com.growith.domain.product.QProduct.*;
import static com.growith.domain.user.QUser.user;
import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ProductGetResponse> getAllProductsWithImage(Pageable pageable) {
        List<ProductGetResponse> products = jpaQueryFactory.from(product)
                .join(productImage).on(product.id.eq(productImage.product.id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(productImage.createdDate.desc())
                .transform(groupBy(product.id)
                        .list(Projections.constructor(ProductGetResponse.class, product, productImage)
                        ));

        long total = jpaQueryFactory.selectFrom(product)
                .fetch()
                .size();

        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public ProductGetResponse getProductWithImage(Long productId) {

        ProductGetResponse response = jpaQueryFactory.selectFrom(product)
                .where(product.id.eq(productId))
                .join(productImage).on(product.id.eq(productImage.product.id)).fetchJoin()
                .transform(groupBy(product.id)
                        .list(Projections.constructor(ProductGetResponse.class, product, productImage)))
                .stream()
                .findFirst()
                .orElse(null);


        return response;
    }
}
