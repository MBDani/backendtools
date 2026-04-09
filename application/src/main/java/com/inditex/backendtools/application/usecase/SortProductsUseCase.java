package com.inditex.backendtools.application.usecase;

import com.inditex.backendtools.domain.model.Product;
import com.inditex.backendtools.domain.model.ScoringCriteria;
import com.inditex.backendtools.domain.port.in.ProductSortingUseCase;
import com.inditex.backendtools.domain.port.out.ProductRepository;
import com.inditex.backendtools.domain.service.ProductScoringService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SortProductsUseCase implements ProductSortingUseCase {

    private final ProductRepository productRepository;
    private final ProductScoringService productScoringService;

    public SortProductsUseCase(ProductRepository productRepository, ProductScoringService productScoringService) {
        this.productRepository = productRepository;
        this.productScoringService = productScoringService;
    }

    @Override
    public List<Product> sortProducts(List<ScoringCriteria> criteria) {
        List<Product> products = productRepository.findAll();
        return productScoringService.rankProducts(products, criteria);
    }
}
