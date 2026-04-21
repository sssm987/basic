package com.example.basic.domain.product.entity;

import com.example.basic.global.common.DomainException;
import com.example.basic.global.common.ErrorCode;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "stock")
    private Long stock;

    @Column(name = "initiative_stock")
    private Long initiativeStock;

    public void decrease(){
        if(stock > 0){
            stock--;
            return;
        }
        throw new DomainException(ErrorCode.PRODUCT_INVENTORY_SHORT);
    }
}
