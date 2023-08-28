package com.gayuh.personalproject.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record QuestionTitleRequest(
        @NotBlank
        String title,
        @NotNull
        Long topicId,
        @NotNull
        Long difficultyId,
        @NotNull
        Long accessId
) {
}
