package com.gayuh.personalproject.dto;

import java.time.LocalDateTime;

public record QuestionResponse(
        Long id,
        Integer time,
        Integer score,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String mediaId
) {
}
