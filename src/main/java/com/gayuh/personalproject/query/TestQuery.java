package com.gayuh.personalproject.query;

public record TestQuery(
        String id,
        Long answer,
        Long questionId,
        String testHistoryId
) {
}
