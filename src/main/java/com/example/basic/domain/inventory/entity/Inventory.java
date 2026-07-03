package com.example.basic.domain.inventory.entity;

import com.example.basic.domain.product.entity.Product;
import com.example.basic.global.common.DomainException;
import com.example.basic.global.common.ErrorCode;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "product_id")
    @OneToOne
    private Product product;

    @Column(name = "stock")
    private Long stock;

    @Column(name = "initiative_stock")
    private Long initiativeStock;
}
