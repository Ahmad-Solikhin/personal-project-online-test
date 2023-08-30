package com.gayuh.personalproject.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserTestHistoryResponse(
        String testHistoryId,
        Double score,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Integer time,
        String name,
        String email
) {
}
