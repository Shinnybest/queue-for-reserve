package com.commerce.queue_for_reserve.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.commerce.queue_for_reserve.exception.ErrorCode.USER_NOT_IN_PROGRESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final String USER_QUEUE_PROCEED_KEY = "users:queue:processing:%s";
    private static final int TIME_EXTENSION_MINUTES = 10;

    public Mono<Void> deleteFromQueue(String uuid) {
        return reactiveRedisTemplate.opsForSet()
                .delete(USER_QUEUE_PROCEED_KEY.formatted(uuid))
                .then();
    }

    public Mono<Void> extendTime(String uuid) {
        return reactiveRedisTemplate.opsForSet()
                .isMember(USER_QUEUE_PROCEED_KEY.formatted(uuid), uuid)
                .flatMap(isMember -> {
                    if (isMember) {
                        return reactiveRedisTemplate.opsForSet()
                                .delete(USER_QUEUE_PROCEED_KEY.formatted(uuid))
                                .then(reactiveRedisTemplate.opsForSet()
                                        .add(USER_QUEUE_PROCEED_KEY.formatted(uuid), uuid)
                                        .then(reactiveRedisTemplate.expire(USER_QUEUE_PROCEED_KEY.formatted(uuid), Duration.ofMinutes(10))))
                                .then();
                    } else {
                        throw USER_NOT_IN_PROGRESS.build();
                    }
                })
                .then();
    }
}
