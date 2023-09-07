package com.gayuh.personalproject.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TestHistoryDetailResponse(
        String id,
        String questionTitleId,
        String title,
        String user,
        String email,
        Double score,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        List<TestHistoryQuestionResponse> questions
) {
}
