package com.inditex.backendtools.domain.service.scoring;

import com.inditex.backendtools.domain.model.Product;
import com.inditex.backendtools.domain.model.SizeStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class StockRatioScoringStrategyTest {

    private StockRatioScoringStrategy strategy;
    private ScoringContext anyContext;

    @BeforeEach
    void setUp() {
        strategy = new StockRatioScoringStrategy();
        anyContext = new ScoringContext(100);
    }

    @Test
    @DisplayName("criteriaName() returns expected constant")
    void criteriaName_returnsExpectedValue() {
        assertThat(strategy.criteriaName()).isEqualTo("stock_ratio");
    }

    @Test
    @DisplayName("score is 100 when all sizes have stock")
    void score_returns100_whenAllSizesHaveStock() {
        Product product = productWithStock(
                new SizeStock("S", 35),
                new SizeStock("M", 9),
                new SizeStock("L", 9)
        );
        assertThat(strategy.score(product, anyContext)).isEqualTo(100.0, offset(0.001));
    }

    @Test
    @DisplayName("score is 0 when no sizes have stock")
    void score_returns0_whenNoSizesHaveStock() {
        Product product = productWithStock(
                new SizeStock("S", 0),
                new SizeStock("M", 0),
                new SizeStock("L", 0)
        );
        assertThat(strategy.score(product, anyContext)).isEqualTo(0.0, offset(0.001));
    }

    @Test
    @DisplayName("score is 0 for product with empty stock list")
    void score_returns0_whenStockListIsEmpty() {
        Product product = Product.builder()
                .id(99)
                .name("Ghost Product")
                .salesUnits(10)
                .stock(Collections.emptyList())
                .build();
        assertThat(strategy.score(product, anyContext)).isEqualTo(0.0, offset(0.001));
    }

    @Test
    @DisplayName("score is 33.33 when 1 of 3 sizes has stock")
    void score_returnsPartialValue_whenOneSizeHasStock() {
        Product product = productWithStock(
                new SizeStock("S", 0),
                new SizeStock("M", 1),
                new SizeStock("L", 0)
        );
        assertThat(strategy.score(product, anyContext)).isEqualTo(33.333, offset(0.001));
    }

    @Test
    @DisplayName("score is 66.66 when 2 of 3 sizes have stock")
    void score_returnsPartialValue_whenTwoSizesHaveStock() {
        Product product = productWithStock(
                new SizeStock("S", 4),
                new SizeStock("M", 9),
                new SizeStock("L", 0)
        );
        assertThat(strategy.score(product, anyContext)).isEqualTo(66.666, offset(0.001));
    }

    private Product productWithStock(SizeStock... sizes) {
        return Product.builder()
                .id(1)
                .name("Test Product")
                .salesUnits(50)
                .stock(List.of(sizes))
                .build();
    }
}
