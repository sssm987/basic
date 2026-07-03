package com.example.basic.application.queue.service;

import com.example.basic.application.queue.result.QueueWaitCheckResult;
import com.example.basic.domain.queue.QueueStatus;
import com.example.basic.domain.queue.repository.QueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;

    public QueueWaitCheckResult waitCheck(Long memberId){
        return activeResultOrElseGet(
                memberId,
                () -> waitingResult(memberId)
        );
    }
    public QueueWaitCheckResult waitRegistration(Long memberId){
        return activeResultOrElseGet(
                memberId,
                () -> register(memberId)
        );
    }

    private QueueWaitCheckResult activeResultOrElseGet(
            Long memberId,
            Supplier<QueueWaitCheckResult> fallback
    ) {
        if (queueRepository.getActiveToken(memberId).isPresent()) {
            return activeResult();
        }
        return fallback.get();
    }

    private QueueWaitCheckResult activeResult() {
        return QueueWaitCheckResult.builder()
                .waitingNumber(0)
                .queueStatus(QueueStatus.ACTIVE)
                .build();
    }
    private QueueWaitCheckResult register(Long memberId) {

        if (queueRepository.tryEnterImmediately(memberId, UUID.randomUUID().toString())) {
            return activeResult();
        }

        queueRepository.registerWaiting(memberId, UUID.randomUUID().toString());

        return waitingResult(memberId);
    }
    private QueueWaitCheckResult waitingResult(Long memberId) {
        Long rank = queueRepository.getRank(memberId);

        return QueueWaitCheckResult.builder()
                .waitingNumber(rank == null ? 0 : rank.intValue() + 1)
                .queueStatus(QueueStatus.WAITING)
                .build();
    }
}
