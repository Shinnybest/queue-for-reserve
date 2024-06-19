package com.commerce.queue_for_reserve.service;

import com.commerce.queue_for_reserve.model.vo.AddToQueueInfo;
import com.commerce.queue_for_reserve.model.vo.GetWaitingRankInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final String USER_QUEUE_WAIT_KEY = "users:queue:waiting";
    private final String USER_QUEUE_PROCEED_KEY = "users:queue:processing:%s";
    private final Long USER_QUEUE_PROCEED_SIZE = 10L; // TODO : 임의로 세팅


    public Mono<AddToQueueInfo> addToQueue() {
        var unixTimestamp = Instant.now().getEpochSecond();
        var uuid = UUID.randomUUID();
        log.info("ADDING TO QUEUE... timestamp = {}, uuid = {}", unixTimestamp, uuid);
        return reactiveRedisTemplate.opsForZSet().add(USER_QUEUE_WAIT_KEY, String.valueOf(uuid), unixTimestamp)
                .filter(user -> user)
                .flatMap(i -> reactiveRedisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY, uuid.toString()))
                .map(rank -> new AddToQueueInfo(rank + 1, uuid.toString()));
    }

    public Mono<Void> deleteFromQueue() {
        log.info("DELETING FROM QUEUE...");
        return reactiveRedisTemplate.scan(ScanOptions.scanOptions().match("users:queue:processing:*").build())
                .count()
                .doOnNext(size -> log.info("USER QUEUE IN PROGRESS SIZE : {}", size))
                .flatMap(size -> {
                    long diff = USER_QUEUE_PROCEED_SIZE - size;
                    if (diff > 0) {
                        return reactiveRedisTemplate.opsForZSet().popMin(USER_QUEUE_WAIT_KEY, diff)
                                .flatMap(user -> reactiveRedisTemplate.opsForSet()
                                        .add(USER_QUEUE_PROCEED_KEY.formatted(user.getValue()), user.getValue())
                                        .then(reactiveRedisTemplate.expire(USER_QUEUE_PROCEED_KEY.formatted(user.getValue()), Duration.ofMinutes(10))))
                                .then();
                    } else {
                        return Mono.empty();
                    }
                })
                .then();
    }

    public Mono<GetWaitingRankInfo> getWaitingRank(String uuid) {
        log.info("GETTING RANK IN THE WAITING QUEUE ... uuid : {}", uuid);
        return reactiveRedisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY, uuid)
                .defaultIfEmpty(-1L)
                .map(rank -> rank >= 0 ? rank + 1 : rank)
                .map(rank -> new GetWaitingRankInfo(rank));
    }
}
