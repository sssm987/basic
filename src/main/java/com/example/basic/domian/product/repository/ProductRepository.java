package com.example.basic.domian.product.repository;

import com.example.basic.domian.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findProductsById(Long id);
}
