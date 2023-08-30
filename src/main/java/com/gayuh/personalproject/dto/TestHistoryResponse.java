package com.gayuh.personalproject.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TestHistoryResponse(
        String id,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Double score,
        String questionTitleId,
        String questionTitle,
        String topic,
        String difficulty
) {
}
