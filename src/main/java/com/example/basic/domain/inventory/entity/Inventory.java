package com.example.basic.domain.inventory.entity;

import com.example.basic.domain.product.entity.Product;
import com.example.basic.global.common.DomainException;
import com.example.basic.global.common.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inventory",
        uniqueConstraints = @UniqueConstraint(
        name = "uk_inventory_product",
        columnNames = "product_id"
))
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "stock")
    private Long stock;

    @Column(name = "initiative_stock")
    private Long initiativeStock;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public void decrease(){
        if(stock > 0){
            stock--;
            return;
        }
        throw new DomainException(ErrorCode.PRODUCT_INVENTORY_SHORT);
    }
}