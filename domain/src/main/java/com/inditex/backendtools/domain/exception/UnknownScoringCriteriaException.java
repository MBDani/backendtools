package com.inditex.backendtools.domain.exception;

public class UnknownScoringCriteriaException extends RuntimeException {

    public UnknownScoringCriteriaException(String criteriaName) {
        super("No scoring strategy registered for criteria: '" + criteriaName + "'");
    }
}
