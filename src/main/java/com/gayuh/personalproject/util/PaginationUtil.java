package com.gayuh.personalproject.util;

import com.gayuh.personalproject.dto.PaginationResponse;

import java.util.List;

public class PaginationUtil {

    private PaginationUtil() {
    }

    public static <T> PaginationResponse<T> createPageResponse(
            List<T> result,
            Integer element,
            Integer page,
            Integer currentPage) {

        return new PaginationResponse<>(result, page, element, currentPage);

    }
}
