package com.example.basic.global.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."),
    PRODUCT_INVENTORY_SHORT(HttpStatus.CONFLICT, "PRODUCT_INVENTORY_SHORT", "상품의 수량이 부족합니다."),
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "INVENTORY_NOT_FOUND", "재고를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
