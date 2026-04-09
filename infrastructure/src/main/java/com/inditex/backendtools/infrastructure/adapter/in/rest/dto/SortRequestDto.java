package com.inditex.backendtools.infrastructure.adapter.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SortRequestDto(@NotEmpty List<@Valid CriteriaDto> criteria) {
}
