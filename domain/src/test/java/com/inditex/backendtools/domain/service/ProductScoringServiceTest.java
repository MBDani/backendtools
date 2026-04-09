package com.inditex.backendtools.domain.service;

import com.inditex.backendtools.domain.exception.UnknownScoringCriteriaException;
import com.inditex.backendtools.domain.model.Product;
import com.inditex.backendtools.domain.model.ScoringCriteria;
import com.inditex.backendtools.domain.model.SizeStock;
import com.inditex.backendtools.domain.service.scoring.SalesUnitsScoringStrategy;
import com.inditex.backendtools.domain.service.scoring.StockRatioScoringStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductScoringServiceTest {

    private ProductScoringService productScoringService;

    @BeforeEach
    void setUp() {
        var strategies = Map.of(
                SalesUnitsScoringStrategy.CRITERIA_NAME, new SalesUnitsScoringStrategy(),
                StockRatioScoringStrategy.CRITERIA_NAME, new StockRatioScoringStrategy()
        );
        productScoringService = new ProductScoringService(strategies);
    }

    @Test
    @DisplayName("products are sorted by weighted score in descending order")
    void rankProducts_sortsByWeightedScoreDescending() {
        Product highSales = productWithSalesAndFullStock(1, "High Sales", 650);
        Product lowSales = productWithSalesAndFullStock(2, "Low Sales", 50);
        Product midSales = productWithSalesAndFullStock(3, "Mid Sales", 100);

        List<ScoringCriteria> criteria = List.of(
                new ScoringCriteria(SalesUnitsScoringStrategy.CRITERIA_NAME, 1.0)
        );

        List<Product> result = productScoringService.rankProducts(List.of(lowSales, highSales, midSales), criteria);

        assertThat(result).extracting(Product::getName)
                .containsExactly("High Sales", "Mid Sales", "Low Sales");
    }

    @Test
    @DisplayName("both criteria are combined with their respective weights")
    void rankProducts_combinesCriteriaWithWeights() {
        Product highSalesLowStock = product(1, "High Sales Low Stock", 650, List.of(
                new SizeStock("S", 0), new SizeStock("M", 0), new SizeStock("L", 1)
        ));
        Product lowSalesHighStock = product(2, "Low Sales High Stock", 50, List.of(
                new SizeStock("S", 10), new SizeStock("M", 10), new SizeStock("L", 10)
        ));

        List<ScoringCriteria> criteria = List.of(
                new ScoringCriteria(SalesUnitsScoringStrategy.CRITERIA_NAME, 0.1),
                new ScoringCriteria(StockRatioScoringStrategy.CRITERIA_NAME, 0.9)
        );

        List<Product> result = productScoringService.rankProducts(List.of(highSalesLowStock, lowSalesHighStock), criteria);

        assertThat(result.getFirst().getName()).isEqualTo("Low Sales High Stock");
    }

    @Test
    @DisplayName("throws exception when criteria name is not registered")
    void rankProducts_throwsException_whenCriteriaNameUnknown() {
        Product product = productWithSalesAndFullStock(1, "Any", 100);
        List<ScoringCriteria> criteria = List.of(new ScoringCriteria("unknown_criteria", 1.0));

        assertThatThrownBy(() -> productScoringService.rankProducts(List.of(product), criteria))
                .isInstanceOf(UnknownScoringCriteriaException.class)
                .hasMessageContaining("unknown_criteria");
    }

    private Product productWithSalesAndFullStock(int id, String name, int salesUnits) {
        return product(id, name, salesUnits, List.of(
                new SizeStock("S", 5), new SizeStock("M", 5), new SizeStock("L", 5)
        ));
    }

    private Product product(int id, String name, int salesUnits, List<SizeStock> stock) {
        return Product.builder().id(id).name(name).salesUnits(salesUnits).stock(stock).build();
    }
}
