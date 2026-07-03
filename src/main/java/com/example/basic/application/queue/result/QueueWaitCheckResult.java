package com.example.basic.application.queue.result;

import com.example.basic.domain.queue.QueueStatus;
import lombok.Builder;

@Builder
public record QueueWaitCheckResult(
        int waitingNumber,
        QueueStatus queueStatus
) {
}
