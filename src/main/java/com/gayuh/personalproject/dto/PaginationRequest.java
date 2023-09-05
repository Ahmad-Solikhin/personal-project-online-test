package com.gayuh.personalproject.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaginationRequest {
    private Integer page;
    private String sort;
    private String sortBy;
    private Long topicId;
    private Long difficultyId;
    private Long accessId;
    private String search;
    private Integer row;
}
