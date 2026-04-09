package com.inditex.backendtools.infrastructure.adapter.out.persistence.dto;

import java.util.List;

public record ProductDataDto(int id, String name, int salesUnits, List<SizeStockDataDto> stock) {
}
