package com.commerce.queue_for_reserve.service;

import com.commerce.queue_for_reserve.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.Mockito.when;

class ProcessServiceUnitTest {

    @Mock
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    @Mock
    private ReactiveSetOperations<String, String> setOperations;
    @InjectMocks
    private ProcessService processService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(reactiveRedisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Test
    @DisplayName("처리열에서 제거")
    void deleteFromQueue() {
        // given
        final String USER_QUEUE_PROCEED_KEY = "users:queue:processing:%s";
        String uuid = "uuid";

        // when
        when(setOperations.delete(USER_QUEUE_PROCEED_KEY.formatted(uuid))).thenReturn(Mono.just(true));

        // then
        processService.deleteFromQueue(uuid);
    }

    @Test
    @DisplayName("처리열에 머무는 시간 연장")
    void extendTime() {
        // given
        final String USER_QUEUE_PROCEED_KEY = "users:queue:processing:%s";
        String uuid = "uuid";

        // when
        when(setOperations.isMember(USER_QUEUE_PROCEED_KEY.formatted(uuid), uuid)).thenReturn(Mono.just(true));
        when(setOperations.delete(USER_QUEUE_PROCEED_KEY.formatted(uuid))).thenReturn(Mono.just(true));
        when(setOperations.add(USER_QUEUE_PROCEED_KEY.formatted(uuid), uuid)).thenReturn(Mono.just(1L));
        when(reactiveRedisTemplate.expire(USER_QUEUE_PROCEED_KEY.formatted(uuid), Duration.ofMinutes(10))).thenReturn(Mono.just(true));

        // then
        processService.extendTime(uuid);
    }

    @Test
    @DisplayName("처리열에 머무는 시간 연장 - 사용자가 처리열에 없는 경우")
    void extendTime_UserNotInProcessing() {
        // given
        final String USER_QUEUE_PROCEED_KEY = "users:queue:processing:%s";
        String uuid = "uuid";
        when(setOperations.isMember(USER_QUEUE_PROCEED_KEY.formatted(uuid), uuid)).thenReturn(Mono.just(false));

        // when
        Mono<Void> result = processService.extendTime(uuid);

        // then
        StepVerifier.create(result)
                .expectError(ErrorCode.USER_NOT_IN_PROGRESS.build().getClass())
                .verify();
    }
}