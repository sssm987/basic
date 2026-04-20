package com.example.basic.domian.order.repository;

import com.example.basic.domian.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {

}
