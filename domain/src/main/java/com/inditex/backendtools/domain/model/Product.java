package com.inditex.backendtools.domain.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Product {

    int id;
    String name;
    int salesUnits;
    List<SizeStock> stock;

    public long availableSizeCount() {
        if (stock == null) return 0;
        return stock.stream()
                .filter(SizeStock::hasStock)
                .count();
    }

    public int totalSizeCount() {
        if (stock == null) return 0;
        return stock.size();
    }
}
