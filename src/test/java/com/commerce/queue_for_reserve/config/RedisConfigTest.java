package com.commerce.queue_for_reserve.config;

import com.commerce.queue_for_reserve.EmbeddedRedis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Import(EmbeddedRedis.class)
@ActiveProfiles("test")
class RedisConfigTest {

    @Autowired
    private ReactiveRedisTemplate reactiveRedisTemplate;

    @BeforeEach
    void setUp() {
        ReactiveRedisConnection redisConnection = reactiveRedisTemplate.getConnectionFactory().getReactiveConnection();
        redisConnection.serverCommands().flushAll().subscribe();
    }

    @Test
    @DisplayName("ReactiveRedisTemplate Sorted Set 연산 테스트")
    void opsZSet() {
        // given
        ReactiveZSetOperations zSetOps = reactiveRedisTemplate.opsForZSet();
        List<ZSetOperations.TypedTuple<String>> tuples = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tuples.add(new DefaultTypedTuple<>(String.valueOf(i), (double) i));
        }

        // then
        StepVerifier.create(zSetOps.add("key", "value", 1.0))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(zSetOps.add("key2", "value2", 2.0))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(zSetOps.rank("key", "value"))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("ReactiveRedisTemplate Set 연산 테스트")
    void opsSet() {
        // given
        var setOps = reactiveRedisTemplate.opsForSet();

        // then
        StepVerifier.create(setOps.add("key", "value"))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(setOps.add("key", "value"))
                .expectNext(0L)
                .verifyComplete();

        StepVerifier.create(setOps.add("key", "value2"))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(setOps.isMember("key", "value"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(setOps.size("key"))
                .expectNext(2L)
                .verifyComplete();
    }
}