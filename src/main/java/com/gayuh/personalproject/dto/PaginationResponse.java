package com.gayuh.personalproject.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PaginationResponse<T>(
        List<T> result,
        Integer page,
        Integer element,
        Integer currentPage
) {
}
