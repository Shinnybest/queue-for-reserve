package com.commerce.queue_for_reserve.controller;

import com.commerce.queue_for_reserve.model.dto.AddToQueueResponse;
import com.commerce.queue_for_reserve.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping
    public Mono<AddToQueueResponse> addToQueue(@RequestParam(name = "user_id") Long userId) {
        return queueService.addToQueue(userId)
                .map(addToQueueInfo -> AddToQueueResponse.builder()
                        .rank(addToQueueInfo.rank())
                        .timestamp(addToQueueInfo.timestamp())
                        .build());
    }
}
