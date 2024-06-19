package com.commerce.queue_for_reserve.controller;

import com.commerce.queue_for_reserve.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/queue/process")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;

    @DeleteMapping
    public Mono<Void> deleteFromQueue(@RequestParam(name = "uuid") String uuid) {
        // TODO : Reserve 완료 API 호출 후 성공 시, 본 API 호출.
        return processService.deleteFromQueue(uuid);
    }

    @PatchMapping("/extend")
    public Mono<Void> extendTime(@RequestParam(name = "uuid") String uuid) {
        return processService.extendTime(uuid);
    }
}
