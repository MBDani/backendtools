package com.inditex.backendtools.application.usecase;

import com.inditex.backendtools.domain.model.Product;
import com.inditex.backendtools.domain.model.ScoringCriteria;
import com.inditex.backendtools.domain.model.SizeStock;
import com.inditex.backendtools.domain.port.out.ProductRepository;
import com.inditex.backendtools.domain.service.ProductScoringService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SortProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductScoringService productScoringService;

    @InjectMocks
    private SortProductsUseCase sortProductsUseCase;

    @Test
    @DisplayName("delegates to repository and scoring service, returns their result")
    void sortProducts_delegatesAndReturnsRankedList() {
        List<Product> products = List.of(sampleProduct(1), sampleProduct(2));
        List<ScoringCriteria> criteria = List.of(new ScoringCriteria("sales_units", 1.0));
        List<Product> rankedProducts = List.of(sampleProduct(2), sampleProduct(1));

        when(productRepository.findAll()).thenReturn(products);
        when(productScoringService.rankProducts(products, criteria)).thenReturn(rankedProducts);

        List<Product> result = sortProductsUseCase.sortProducts(criteria);

        assertThat(result).isEqualTo(rankedProducts);
        verify(productRepository).findAll();
        verify(productScoringService).rankProducts(products, criteria);
    }

    private Product sampleProduct(int id) {
        return Product.builder()
                .id(id)
                .name("Product " + id)
                .salesUnits(100)
                .stock(List.of(new SizeStock("S", 5)))
                .build();
    }
}
