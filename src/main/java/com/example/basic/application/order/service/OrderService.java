package com.example.basic.application.order.service;

import com.example.basic.application.order.cmd.OrderCreateCmd;
import com.example.basic.domain.order.entity.Order;
import com.example.basic.domain.order.repository.OrderRepository;
import com.example.basic.domain.product.entity.Product;
import com.example.basic.domain.product.repository.ProductRepository;
import com.example.basic.global.common.DomainException;
import com.example.basic.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void create(OrderCreateCmd cmd){
        Product product = productRepository.findProductsById(cmd.getProductId())
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_NOT_FOUND));

        product.decrease();

        Order order = Order.create(cmd.getMemberId(),product);
        orderRepository.save(order);
    }
}
