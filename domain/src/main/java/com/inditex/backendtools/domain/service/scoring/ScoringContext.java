package com.inditex.backendtools.domain.service.scoring;

import com.inditex.backendtools.domain.model.Product;

import java.util.List;

public record ScoringContext(int maxSalesUnits) {

    public static ScoringContext from(List<Product> products) {
        int max = products.stream()
                .mapToInt(Product::getSalesUnits)
                .max()
                .orElse(1);
        return new ScoringContext(max);
    }
}
