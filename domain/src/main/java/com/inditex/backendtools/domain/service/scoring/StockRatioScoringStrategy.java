package com.inditex.backendtools.domain.service.scoring;

import com.inditex.backendtools.domain.model.Product;

public class StockRatioScoringStrategy implements ScoringStrategy {

    public static final String CRITERIA_NAME = "stock_ratio";

    @Override
    public String criteriaName() {
        return CRITERIA_NAME;
    }

    @Override
    public double score(Product product, ScoringContext context) {
        int totalSizes = product.totalSizeCount();

        if (totalSizes == 0) {
            return 0.0;
        }

        return ((double) product.availableSizeCount() / totalSizes) * 100.0;
    }
}
