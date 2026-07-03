package com.example.basic.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseWrapper<T>(
    boolean success,
    String message,
    @JsonInclude(JsonInclude.Include.ALWAYS)
    T data,
    String errorCode
) {
    public static final String SUCCESS_MESSAGE = "성공";

    public static <T> ResponseWrapper<T> of(T data) {
        return ResponseWrapper.<T>builder()
            .success(TRUE)
            .data(data)
            .build();
    }

    public static ResponseWrapper<Void> ok() {
        return ResponseWrapper.<Void>builder()
            .success(TRUE)
            .message(SUCCESS_MESSAGE)
            .errorCode(null)
            .build();
    }

    public static ResponseWrapper<Object> fail(String message, String errorCode) {
        return ResponseWrapper.builder()
            .success(FALSE)
            .message(message)
            .errorCode(errorCode)
            .build();
    }

}
