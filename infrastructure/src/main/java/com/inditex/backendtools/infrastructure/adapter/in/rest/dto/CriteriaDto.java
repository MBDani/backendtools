package com.inditex.backendtools.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CriteriaDto(
        @NotBlank String name,
        @Positive double weight
) {
}
