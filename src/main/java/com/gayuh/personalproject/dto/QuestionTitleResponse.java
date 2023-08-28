package com.gayuh.personalproject.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record QuestionTitleResponse(
        String id,
        String title,
        LocalDateTime createdAt,
        String userName,
        String topic,
        String difficulty,
        String access,
        Long tested
) {
}
