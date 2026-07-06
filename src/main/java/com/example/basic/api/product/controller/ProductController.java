package com.example.basic.api.product.controller;

import com.example.basic.api.product.dto.response.ProductOrdersSelectResponseDTO;
import com.example.basic.application.product.service.ProductService;
import com.example.basic.global.common.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Tag(name = "상품 컨트롤러")
public class ProductController {

    private final ProductService productService;

    @GetMapping()
    @Operation(summary= "상품별 주문내역 조회", description = "상품별 주문내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품별 주문내역 조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품별 주문내역을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<ProductOrdersSelectResponseDTO> productOrdersSelect(){
        return productService.productOrdersSelect();
    }
}
