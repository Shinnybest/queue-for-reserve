package com.commerce.queue_for_reserve.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class QueueControllerAdvice {

    @ExceptionHandler(QueueException.class)
    public Mono<ResponseEntity<ServerExceptionResponse>> queueExceptionHandler(QueueException exception) {
        return Mono.just(ResponseEntity
                .status(exception.getHttpStatus())
                .body(new ServerExceptionResponse(exception.getHttpStatus().value(), exception.getCode(), exception.getReason())));
    }

    public record ServerExceptionResponse(int status, String code, String reason) {
    }
}
