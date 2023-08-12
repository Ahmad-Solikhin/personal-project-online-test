package com.gayuh.personalproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
public class TestAddRequest {
    @NotBlank(message = "Name must be exist")
    private String name;
    @NotNull(message = "Age cannot be null")
    private Integer age;
}
