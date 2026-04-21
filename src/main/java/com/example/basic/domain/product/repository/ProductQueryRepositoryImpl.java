package com.example.basic.domain.product.repository;

import com.example.basic.api.product.dto.response.ProductOrdersSelectResponseDTO;
import com.example.basic.domain.order.entity.QOrder;
import com.example.basic.domain.product.entity.QProduct;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl {
    private final QOrder order = QOrder.order;
    private final QProduct product = QProduct.product;
    private final JPAQueryFactory queryFactory;


    public List<ProductOrdersSelectResponseDTO> productOrdersSelect(){
        return queryFactory
                .select(Projections.fields(
                        ProductOrdersSelectResponseDTO.class,
                        product.id.as("productId"),
                        order.id.count().as("orderCount"),
                        product.stock,
                        product.initiativeStock
                ))
                .from(product)
                .leftJoin(order).on(order.product.eq(product))
                .groupBy(product.id, product.stock, product.initiativeStock)
                .fetch();
    }

}
