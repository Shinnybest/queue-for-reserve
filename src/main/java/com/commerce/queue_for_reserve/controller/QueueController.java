package com.commerce.queue_for_reserve.controller;

import com.commerce.queue_for_reserve.model.dto.AddToQueueResponse;
import com.commerce.queue_for_reserve.model.dto.GetWaitingRankResponse;
import com.commerce.queue_for_reserve.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping
    public Mono<AddToQueueResponse> addToQueue() {
        return queueService.addToQueue()
                .map(addToQueueInfo -> AddToQueueResponse.builder()
                        .rank(addToQueueInfo.rank())
                        .uuid(addToQueueInfo.uuid())
                        .build());
    }

    @DeleteMapping
    public Mono<ResponseEntity<Object>> deleteFromQueue() {
        return queueService.deleteFromQueue()
                .then(Mono.just(ResponseEntity.ok().build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).build()));
    }

    @GetMapping("/rank")
    public Mono<GetWaitingRankResponse> getWaitingRank(@RequestParam(name = "uuid") String uuid) {
        return queueService.getWaitingRank(uuid)
                .map(rank -> GetWaitingRankResponse.builder()
                        .rank(rank.rank())
                        .build());
    }
}
