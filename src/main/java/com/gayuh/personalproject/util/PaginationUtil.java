package com.gayuh.personalproject.util;

import com.gayuh.personalproject.dto.PaginationResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    public static Sort getSort(String sort, String sortBy) {
        return Sort.by(new Sort.Order(
                sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy
        ));
    }

    public static PageRequest getPageRequest(int page, int row, Sort sort1) {
        try {
            return PageRequest.of((page - 1), row, sort1);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }
}
