package com.gayuh.personalproject.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TestResponse(
        String questionTitleId,
        Long questionId,
        String questionText,
        Integer time,
        String mediaLink,
        List<TestChoiceResponse> choices
) {
}
