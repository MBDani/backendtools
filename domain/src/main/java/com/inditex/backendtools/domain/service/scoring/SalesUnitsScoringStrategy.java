package com.inditex.backendtools.domain.service.scoring;

import com.inditex.backendtools.domain.model.Product;

public class SalesUnitsScoringStrategy implements ScoringStrategy {

    public static final String CRITERIA_NAME = "sales_units";

    @Override
    public String criteriaName() {
        return CRITERIA_NAME;
    }

    @Override
    public double score(Product product, ScoringContext context) {
        if (context.maxSalesUnits() == 0) {
            return 0.0;
        }
        return ((double) product.getSalesUnits() / context.maxSalesUnits()) * 100.0;
    }
}
