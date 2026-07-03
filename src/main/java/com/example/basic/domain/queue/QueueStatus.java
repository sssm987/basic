package com.example.basic.domain.queue;

public enum QueueStatus {
    WAITING("대기"),
    ACTIVE("입장");

    private final String description;

    QueueStatus(String description){
        this.description = description;
    }
}
