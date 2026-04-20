package com.example.basic.global.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "공통 에러 응답")
public class ErrorResponse {

    @Schema(description = "HTTP 상태 코드", example = "409")
    private final int status;

    @Schema(description = "애플리케이션 에러 코드", example = "PRODUCT_INVENTORY_SHORT")
    private final String code;

    @Schema(description = "에러 메시지", example = "상품의 수량이 부족합니다.")
    private final String message;

    public static ErrorResponse from(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }
}
