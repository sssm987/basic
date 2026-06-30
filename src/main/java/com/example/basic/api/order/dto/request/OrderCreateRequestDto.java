package com.example.basic.api.order.dto.request;

import com.example.basic.application.order.cmd.OrderCreateCmd;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderCreateRequestDto {
    @NotNull
    @Schema(description = "상품번호", example = "1", hidden = false)
    private Long productId;
    @NotNull
    @Schema(description = "회원번호", example = "1", hidden = false)
    private Long memberId;

    public OrderCreateCmd toCommand(){
        return OrderCreateCmd.builder().productId(productId).memberId(memberId).build();
    }
}
