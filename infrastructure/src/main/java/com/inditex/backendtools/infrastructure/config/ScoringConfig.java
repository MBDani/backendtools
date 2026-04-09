package com.inditex.backendtools.infrastructure.config;

import com.inditex.backendtools.domain.service.ProductScoringService;
import com.inditex.backendtools.domain.service.scoring.SalesUnitsScoringStrategy;
import com.inditex.backendtools.domain.service.scoring.ScoringStrategy;
import com.inditex.backendtools.domain.service.scoring.StockRatioScoringStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class ScoringConfig {

    @Bean
    public SalesUnitsScoringStrategy salesUnitsScoringStrategy() {
        return new SalesUnitsScoringStrategy();
    }

    @Bean
    public StockRatioScoringStrategy stockRatioScoringStrategy() {
        return new StockRatioScoringStrategy();
    }

    @Bean
    public ProductScoringService productScoringService(List<ScoringStrategy> strategies) {
        Map<String, ScoringStrategy> strategyMap = strategies.stream()
                .collect(Collectors.toMap(ScoringStrategy::criteriaName, Function.identity()));
        return new ProductScoringService(strategyMap);
    }
}
