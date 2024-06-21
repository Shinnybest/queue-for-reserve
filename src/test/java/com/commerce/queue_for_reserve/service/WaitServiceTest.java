package com.commerce.queue_for_reserve.service;

import com.commerce.queue_for_reserve.EmbeddedRedis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.UUID;

@SpringBootTest
@Import(EmbeddedRedis.class)
@ActiveProfiles("test")
class WaitServiceTest {

    @Autowired
    private WaitService waitService;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @BeforeEach
    public void beforeEach() {
        ReactiveRedisConnection redisConnection = reactiveRedisTemplate.getConnectionFactory().getReactiveConnection();
        redisConnection.serverCommands().flushAll().subscribe();
    }

    @Test
    void addToQueue() throws InterruptedException {
        StepVerifier.create(waitService.addToQueue())
                .expectNextMatches(info -> info.rank() == 1L && isUUID(info.uuid()))
                .verifyComplete();

        StepVerifier.create(waitService.addToQueue())
                .expectNextMatches(info -> info.rank() == 2L && isUUID(info.uuid()))
                .verifyComplete();

        StepVerifier.create(waitService.addToQueue())
                .expectNextMatches(info -> info.rank() == 3L && isUUID(info.uuid()))
                .verifyComplete();
    }

    private boolean isUUID(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}