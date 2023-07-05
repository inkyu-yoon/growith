package com.growith.domain.orderProduct;

import com.growith.domain.BaseEntity;
import com.growith.domain.order.Orders;
import com.growith.domain.product.Product;
import com.growith.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date is NULL")
@SQLDelete(sql = "UPDATE order_product SET deleted_date = current_timestamp WHERE id = ?")
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Long numOfProducts;
    private Long sumOfPrice;

    public static OrderProduct of(Orders order, Product product, User user, Long num) {
        long totalPrice = product.getPrice() * num;

        user.checkAddress();
        user.checkPoint(totalPrice);

        product.removeStock(num);

        return OrderProduct.builder()
                .order(order)
                .product(product)
                .numOfProducts(num)
                .sumOfPrice(totalPrice)
                .build();
    }
}
