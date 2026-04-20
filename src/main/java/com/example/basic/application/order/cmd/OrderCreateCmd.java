package com.example.basic.application.order.cmd;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderCreateCmd {
    private Long productId;
    private Long memberId;
}
