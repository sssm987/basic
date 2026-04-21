package com.example.basic.api.product.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@JsonPropertyOrder({ "productId", "stock", "orderCount", "initiativeStock" })
public class ProductOrdersSelectResponseDTO {
    @Schema(description = "상품번호", example = "1", hidden = false)
    private Long productId;
    @Schema(description = "남은 상품재고", example = "1", hidden = false)
    private Long stock;
    @Schema(description = "주문횟수", example = "1", hidden = false)
    private Long orderCount;
    @Schema(description = "초기 상품재고", example = "1", hidden = false)
    private Long initiativeStock;
}
