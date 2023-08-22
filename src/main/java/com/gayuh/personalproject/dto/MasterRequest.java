package com.gayuh.personalproject.dto;

import jakarta.validation.constraints.NotBlank;

public record MasterRequest(
        @NotBlank
        String name
) {
}
