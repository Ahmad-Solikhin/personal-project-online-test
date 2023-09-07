package com.gayuh.personalproject.query;

public record ChoiceQuery(
        Long id,
        String choiceText,
        Boolean correct,
        Long questionId
) {
}
