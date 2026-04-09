package com.inditex.backendtools.domain.model;

public record ScoringCriteria(String criteriaName, double weight) {

    public ScoringCriteria {
        if (criteriaName == null || criteriaName.isBlank()) {
            throw new IllegalArgumentException("Criteria name must not be blank");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive, got: " + weight);
        }
    }
}
