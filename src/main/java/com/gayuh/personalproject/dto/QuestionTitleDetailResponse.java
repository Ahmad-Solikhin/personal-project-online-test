package com.gayuh.personalproject.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record QuestionTitleDetailResponse(
        String id,
        String title,
        LocalDateTime createdAt,
        String userId,
        String userName,
        Long topicId,
        String topic,
        Long difficultyId,
        String difficulty,
        String access,
        Long tested
) {
}
