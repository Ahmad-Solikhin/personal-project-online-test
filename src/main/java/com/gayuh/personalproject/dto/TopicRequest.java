package com.gayuh.personalproject.dto;

import jakarta.validation.constraints.NotBlank;

public record TopicRequest(
        @NotBlank
        String name
) {
}
