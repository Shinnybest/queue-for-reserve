package com.commerce.queue_for_reserve.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToQueueResponse extends QueueCommonResponse {
    private long rank;

    @Builder
    public AddToQueueResponse(long timestamp, long rank) {
        super(timestamp);
        this.rank = rank;
    }
}
