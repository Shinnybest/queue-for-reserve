package com.commerce.queue_for_reserve.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_IN_PROGRESS(HttpStatus.FORBIDDEN, "001", "This user is not in the processing queue.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String reason;

    public QueueException build() {
        return new QueueException(httpStatus, code, reason);
    }
}
