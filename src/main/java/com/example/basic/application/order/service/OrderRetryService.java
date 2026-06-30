package com.example.basic.application.order.service;

import com.example.basic.application.order.cmd.OrderCreateCmd;
import com.example.basic.global.common.DomainException;
import com.example.basic.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderRetryService {

    private static final int MAX_RETRY_COUNT = 5;

    private final OrderService orderService;

    public void create(OrderCreateCmd cmd) {
        for (int retryCount = 0; retryCount < MAX_RETRY_COUNT; retryCount++) {
            try {
                orderService.create(cmd);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                if (retryCount == MAX_RETRY_COUNT - 1) {
                    throw new DomainException(ErrorCode.ORDER_CONFLICT_RETRY_EXCEEDED);
                }
            }
        }
    }
}
