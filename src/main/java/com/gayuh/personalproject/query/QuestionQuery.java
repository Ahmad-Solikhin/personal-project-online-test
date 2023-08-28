package com.gayuh.personalproject.query;

import java.time.LocalDateTime;

public record QuestionQuery(
        Long id,
        String questionText,
        Integer time,
        Integer score,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String mediaId
) {
}
