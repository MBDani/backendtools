package com.inditex.backendtools.domain.service.scoring;

import com.inditex.backendtools.domain.model.Product;
import com.inditex.backendtools.domain.model.SizeStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class SalesUnitsScoringStrategyTest {

    private SalesUnitsScoringStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new SalesUnitsScoringStrategy();
    }

    @Test
    @DisplayName("criteriaName() returns expected constant")
    void criteriaName_returnsExpectedValue() {
        assertThat(strategy.criteriaName()).isEqualTo("sales_units");
    }

    @Test
    @DisplayName("product with the highest sales in the dataset scores 100")
    void score_returns100_forProductWithMaxSalesInDataset() {
        Product topProduct = productWithSales(650);
        ScoringContext context = ScoringContext.from(List.of(topProduct, productWithSales(100)));

        assertThat(strategy.score(topProduct, context)).isEqualTo(100.0, offset(0.001));
    }

    @Test
    @DisplayName("score is proportional when product does not have the maximum sales")
    void score_returnsProportionalValue_forProductBelowMax() {
        Product product = productWithSales(100);
        ScoringContext context = ScoringContext.from(List.of(product, productWithSales(650)));

        // 100 / 650 * 100 ≈ 15.38
        assertThat(strategy.score(product, context)).isEqualTo(15.384, offset(0.001));
    }

    @Test
    @DisplayName("score is 0 when product has zero sales")
    void score_returns0_whenSalesUnitsAreZero() {
        Product product = productWithSales(0);
        ScoringContext context = ScoringContext.from(List.of(product, productWithSales(100)));

        assertThat(strategy.score(product, context)).isEqualTo(0.0, offset(0.001));
    }

    @Test
    @DisplayName("score is 0 when context max is 0 (defensive edge case)")
    void score_returns0_whenContextMaxIsZero() {
        Product product = productWithSales(0);
        ScoringContext context = new ScoringContext(0);

        assertThat(strategy.score(product, context)).isEqualTo(0.0, offset(0.001));
    }

    private Product productWithSales(int salesUnits) {
        return Product.builder()
                .id(1)
                .name("Test Product")
                .salesUnits(salesUnits)
                .stock(List.of(new SizeStock("S", 5)))
                .build();
    }
}
