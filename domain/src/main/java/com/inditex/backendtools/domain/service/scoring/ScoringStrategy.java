package com.inditex.backendtools.domain.service.scoring;

import com.inditex.backendtools.domain.model.Product;

public interface ScoringStrategy {

    String criteriaName();

    double score(Product product, ScoringContext context);
}
