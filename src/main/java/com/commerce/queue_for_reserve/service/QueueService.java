package com.commerce.queue_for_reserve.service;

import com.commerce.queue_for_reserve.model.vo.AddToQueueInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final String USER_QUEUE_WAIT_KEY = "users:queue:waiting";
    private final String USER_QUEUE_PROCEED_KEY = "users:queue:processing";
    private final Long USER_QUEUE_PROCEED_SIZE = 10L; // TODO : 임의로 세팅


    public Mono<AddToQueueInfo> addToQueue(Long userId) {
        log.info("ADDING TO QUEUE... user id : {}", userId);
        var unixTimestamp = Instant.now().getEpochSecond();
        return reactiveRedisTemplate.opsForZSet().add(USER_QUEUE_WAIT_KEY, String.valueOf(userId), unixTimestamp)
                .filter(user -> user)
                .flatMap(i -> reactiveRedisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY, userId.toString()))
                .map(rank -> new AddToQueueInfo(rank + 1, unixTimestamp));
    }

    public Mono<Void> deleteFromQueue() {
        log.info("DELETING FROM QUEUE...");
        return reactiveRedisTemplate.opsForSet()
                .size(USER_QUEUE_PROCEED_KEY)
                .doOnNext(size -> log.info("USER QUEUE IN PROGRESS SIZE : {}", size))
                .flatMap(size -> {
                    long diff = USER_QUEUE_PROCEED_SIZE - size;
                    if (diff > 0) {
                        return reactiveRedisTemplate.opsForZSet().popMin(USER_QUEUE_WAIT_KEY, diff)
                                .flatMap(user -> reactiveRedisTemplate.opsForSet()
                                        .add(USER_QUEUE_PROCEED_KEY, user.getValue()))
                                .then();
                    } else {
                        return Mono.empty();
                    }
                })
                .then();
    }
}
