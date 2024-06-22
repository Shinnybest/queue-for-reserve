package com.commerce.queue_for_reserve.service;

import com.commerce.queue_for_reserve.EmbeddedRedis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@SpringBootTest
@Import(EmbeddedRedis.class)
@ActiveProfiles("test")
class ProcessServiceIntegrationTest {

    @Autowired
    private ProcessService processService;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @BeforeEach
    public void beforeEach() {
        ReactiveRedisConnection redisConnection = reactiveRedisTemplate.getConnectionFactory().getReactiveConnection();
        redisConnection.serverCommands().flushAll().subscribe();
        reactiveRedisTemplate.opsForSet().add("users:queue:processing:uuid", "uuid").subscribe();
    }

    @Test
    @DisplayName("처리열에서 제거")
    void deleteFromQueue() {
        // given
        String uuid = "uuid";

        // when & then
        StepVerifier.create(processService.deleteFromQueue(uuid))
                .verifyComplete();
    }

    @Test
    @DisplayName("처리열에 머무는 시간 연장")
    void extendTime() {
        // given
        String uuid = "uuid";

        // when & then
        StepVerifier.create(processService.extendTime(uuid))
                .verifyComplete();
    }
}