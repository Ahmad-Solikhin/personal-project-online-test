package com.gayuh.personalproject.query;

import java.time.LocalDateTime;

public record TestHistoryQuery(
        String id,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Double score,
        String email,
        String questionTitleId
) {
}
