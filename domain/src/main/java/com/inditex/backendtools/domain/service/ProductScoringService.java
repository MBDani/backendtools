package com.inditex.backendtools.domain.service;

import com.inditex.backendtools.domain.exception.UnknownScoringCriteriaException;
import com.inditex.backendtools.domain.model.Product;
import com.inditex.backendtools.domain.model.ScoringCriteria;
import com.inditex.backendtools.domain.service.scoring.ScoringContext;
import com.inditex.backendtools.domain.service.scoring.ScoringStrategy;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ProductScoringService {

    private final Map<String, ScoringStrategy> strategies;

    public ProductScoringService(Map<String, ScoringStrategy> strategies) {
        this.strategies = strategies;
    }

    public List<Product> rankProducts(List<Product> products, List<ScoringCriteria> criteria) {
        validateCriteria(criteria);
        ScoringContext context = ScoringContext.from(products);

        return products.stream()
                .sorted(Comparator.comparingDouble(
                        (Product p) -> weightedScore(p, criteria, context)
                ).reversed())
                .toList();
    }

    private void validateCriteria(List<ScoringCriteria> criteria) {
        criteria.forEach(c -> resolveStrategy(c.criteriaName()));
    }

    private double weightedScore(Product product, List<ScoringCriteria> criteria, ScoringContext context) {
        return criteria.stream()
                .mapToDouble(c -> resolveStrategy(c.criteriaName()).score(product, context) * c.weight())
                .sum();
    }

    private ScoringStrategy resolveStrategy(String criteriaName) {
        ScoringStrategy strategy = strategies.get(criteriaName);
        if (strategy == null) {
            throw new UnknownScoringCriteriaException(criteriaName);
        }
        return strategy;
    }
}
