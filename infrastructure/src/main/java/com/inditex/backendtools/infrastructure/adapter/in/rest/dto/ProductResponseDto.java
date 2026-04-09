package com.inditex.backendtools.infrastructure.adapter.in.rest.dto;

import java.util.List;

public record ProductResponseDto(int id, String name, int salesUnits, List<SizeStockResponseDto> stock) {
}
