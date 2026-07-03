package com.example.basic.domain.queue.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueueRedisRepository implements QueueRepository{

    private static final String WAIT_KEY = "queue:wait";
    private static final String ACTIVE_KEY = "queue:active";
    private static final String ACTIVE_TOKEN_KEY_PREFIX = "queue:token:active:member:";
    private static final String WAIT_TOKEN_KEY_PREFIX = "queue:token:wait:member:";
    private static final int ACTIVE_LIMIT = 100;
    private static final Duration ACTIVE_TOKEN_TTL = Duration.ofMinutes(3);
    private static final Duration WAIT_TOKEN_TTL = Duration.ofMinutes(30);

    private static final DefaultRedisScript<Long> TRY_ENTER_IMMEDIATELY_SCRIPT =
            new DefaultRedisScript<>("""
                    redis.call('ZREMRANGEBYSCORE', KEYS[2], '-inf', ARGV[3])

                    local waitSize = redis.call('ZCARD', KEYS[1])
                    local activeSize = redis.call('ZCARD', KEYS[2])

                    if waitSize == 0 and activeSize < tonumber(ARGV[2]) then
                        redis.call('ZADD', KEYS[2], ARGV[4], ARGV[1])
                        redis.call('SET', KEYS[3], ARGV[5], 'EX', ARGV[6])
                        return 1
                    end

                    return 0
                    """, Long.class);

    private static final DefaultRedisScript<String> REGISTER_WAITING_SCRIPT =
            new DefaultRedisScript<>("""
                    local currentToken = redis.call('GET', KEYS[2])

                    if currentToken then
                        return currentToken
                    end

                    redis.call('SET', KEYS[2], ARGV[2], 'EX', ARGV[3])
                    redis.call('ZADD', KEYS[1], 'NX', ARGV[1], ARGV[4])

                    return ARGV[2]
                    """, String.class);

    private final StringRedisTemplate redisTemplate;

    @Override
    public Long getRank(Long memberId) {
        return redisTemplate.opsForZSet()
                .rank(WAIT_KEY, memberId.toString());
    }

    @Override
    public Optional<String> getActiveToken(Long memberId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(activeTokenKey(memberId)));
    }

    @Override
    public boolean tryEnterImmediately(Long memberId, String activeToken) {
        long nowMillis = System.currentTimeMillis();
        long expiresAtMillis = nowMillis + ACTIVE_TOKEN_TTL.toMillis();

        Long result = redisTemplate.execute(
                TRY_ENTER_IMMEDIATELY_SCRIPT,
                List.of(WAIT_KEY, ACTIVE_KEY, activeTokenKey(memberId)),
                memberId.toString(),
                String.valueOf(ACTIVE_LIMIT),
                String.valueOf(nowMillis),
                String.valueOf(expiresAtMillis),
                activeToken,
                String.valueOf(ACTIVE_TOKEN_TTL.toSeconds())
        );

        return result != null && result == 1;
    }

    @Override
    public String registerWaiting(Long memberId, String waitToken) {
        String result = redisTemplate.execute(
                REGISTER_WAITING_SCRIPT,
                List.of(WAIT_KEY, waitTokenKey(memberId)),
                String.valueOf(System.currentTimeMillis()),
                waitToken,
                String.valueOf(WAIT_TOKEN_TTL.toSeconds()),
                memberId.toString()
        );

        return result == null ? waitToken : result;
    }

    private String activeTokenKey(Long memberId) {
        return ACTIVE_TOKEN_KEY_PREFIX + memberId;
    }

    private String waitTokenKey(Long memberId) {
        return WAIT_TOKEN_KEY_PREFIX + memberId;
    }
}
