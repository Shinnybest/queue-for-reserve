package com.commerce.queue_for_reserve.service;

import com.commerce.queue_for_reserve.exception.ErrorCode;
import com.commerce.queue_for_reserve.model.vo.AddToQueueInfo;
import com.commerce.queue_for_reserve.model.vo.GetWaitingRankInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.*;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WaitServiceUnitTest {

    @Mock
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    @Mock
    private ReactiveZSetOperations<String, String> zSetOperations;
    @Mock
    private ReactiveSetOperations<String, String> setOperations;
    @InjectMocks
    private WaitService waitService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(reactiveRedisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(reactiveRedisTemplate.opsForSet()).thenReturn(setOperations);
        ReflectionTestUtils.setField(waitService, "scheduling", true);
    }

    @Test
    @DisplayName("대기열에 사용자 추가")
    void addToQueue() {
        // given
        when(reactiveRedisTemplate.opsForZSet().add(anyString(), anyString(), anyDouble())).thenReturn(Mono.just(true));
        when(reactiveRedisTemplate.opsForZSet().rank(anyString(), anyString())).thenReturn(Mono.just(0L));

        // when
        Mono<AddToQueueInfo> result = waitService.addToQueue();

        // then
        StepVerifier.create(result)
                .expectNextMatches(info -> info.rank() == 1 && info.uuid() != null)
                .verifyComplete();
    }

    @Test
    @DisplayName("대기열에 사용자 추가 실패 예외처리")
    void addToQueueFailed() {
        // given
        when(reactiveRedisTemplate.opsForZSet().add(anyString(), anyString(), anyDouble()))
                .thenReturn(Mono.error(ErrorCode.FAILED_TO_ADD_IN_WAIT_QUEUE.build()));

        // when
        Mono<AddToQueueInfo> result = waitService.addToQueue();

        // then
        StepVerifier.create(result)
                .expectError(ErrorCode.FAILED_TO_ADD_IN_WAIT_QUEUE.build().getClass())
                .verify();
    }

    @Test
    @DisplayName("처리열에서 빠진 사용자 수만큼 대기열에서 처리열로 이동")
    void scheduleAllowUser() {
        // given
        when(reactiveRedisTemplate.scan(any(ScanOptions.class))).thenReturn(Flux.just("user1", "user2"));
        when(zSetOperations.popMin(anyString(), anyLong())).thenReturn(Flux.just(new PoppedUser("user1", 1.0)));
        when(setOperations.add(anyString(), anyString())).thenReturn(Mono.just(1L));
        when(reactiveRedisTemplate.expire(anyString(), any(Duration.class))).thenReturn(Mono.just(true));

        // when
        waitService.scheduleAllowUser();

        // then
        verify(reactiveRedisTemplate, times(1)).scan(any(ScanOptions.class));
        verify(zSetOperations, times(1)).popMin(anyString(), anyLong());
        verify(setOperations, times(1)).add(anyString(), anyString());
        verify(reactiveRedisTemplate, times(1)).expire(anyString(), any(Duration.class));
    }

    @Test
    @DisplayName("대기열의 순서 반환")
    void getWaitingRank() {
        // given
        var uuid = UUID.randomUUID().toString();
        when(reactiveRedisTemplate.opsForZSet().rank(anyString(), anyString())).thenReturn(Mono.just(1L));

        // when
        Mono<GetWaitingRankInfo> result = waitService.getWaitingRank(uuid);

        // then
        StepVerifier.create(result)
                .expectNextMatches(info -> info.rank() == 2)
                .verifyComplete();
    }

    @Test
    @DisplayName("대기열의 순서 반환 실패 - 대기열에 없는 사용자")
    void NoUserInWaitingQueue() {
        // given
        var uuid = UUID.randomUUID().toString();
        when(reactiveRedisTemplate.opsForZSet().rank(anyString(), anyString())).thenReturn(Mono.just(-1L));

        // when
        Mono<GetWaitingRankInfo> result = waitService.getWaitingRank(uuid);

        // then
        StepVerifier.create(result)
                .expectNextMatches(info -> info.rank() == -1)
                .verifyComplete();
    }

    private class PoppedUser implements ZSetOperations.TypedTuple<String> {
        private final String value;
        private final Double score;

        public PoppedUser(String value, Double score) {
            this.value = value;
            this.score = score;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public Double getScore() {
            return score;
        }

        @Override
        public int compareTo(ZSetOperations.TypedTuple<String> o) {
            return Double.compare(getScore(), o.getScore());
        }
    }
}