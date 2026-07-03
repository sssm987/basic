package com.example.basic.domain.queue.repository;

import java.util.Optional;

public interface QueueRepository {
    Long getRank(Long memberId);
    Optional<String> getActiveToken(Long memberId);
    boolean tryEnterImmediately(Long memberId, String activeToken);
    String registerWaiting(Long memberId, String waitToken);
}
