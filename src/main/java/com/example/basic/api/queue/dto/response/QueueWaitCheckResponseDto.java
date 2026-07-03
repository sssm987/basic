package com.example.basic.api.queue.dto.response;

import com.example.basic.application.queue.result.QueueWaitCheckResult;
import com.example.basic.domain.queue.QueueStatus;
import lombok.Builder;

@Builder
public record QueueWaitCheckResponseDto(
        int waitingNumber,
        QueueStatus queueStatus
) {
    public static QueueWaitCheckResponseDto of(QueueWaitCheckResult result){
        return QueueWaitCheckResponseDto.builder()
                .waitingNumber(result.waitingNumber())
                .queueStatus(result.queueStatus())
                .build();
    }
}
