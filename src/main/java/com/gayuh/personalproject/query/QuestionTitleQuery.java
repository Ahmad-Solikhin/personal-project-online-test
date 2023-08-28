package com.gayuh.personalproject.query;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record QuestionTitleQuery(
        String id,
        String title,
        String token,
        Boolean started,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String userId,
        String userName,
        Long topicId,
        String topic,
        Long difficultyId,
        String difficulty,
        Long accessId,
        String access,
        Long tested
) {
}
