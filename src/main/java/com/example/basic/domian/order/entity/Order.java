package com.example.basic.domian.order.entity;
import com.example.basic.domian.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    private Order(Long memberId, Product product){
        this.memberId = memberId;
        this.product = product;
    }

    public static Order create(Long memberId, Product product){
        return Order.builder()
                .memberId(memberId)
                .product(product)
                .build();
    }
}
