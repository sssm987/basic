package com.example.basic.application.product.service;

import com.example.basic.api.product.dto.response.ProductOrdersSelectResponseDTO;
import com.example.basic.global.common.DomainException;
import com.example.basic.global.common.ErrorCode;
import com.example.basic.domain.product.repository.ProductQueryRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductQueryRepositoryImpl productQueryRepository;

    public List<ProductOrdersSelectResponseDTO> productOrdersSelect(){
        return productQueryRepository.productOrdersSelect();
    }

    public ProductOrdersSelectResponseDTO productOrdersSelect(Long productId){
        return productQueryRepository.productOrdersSelect(productId)
                .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
