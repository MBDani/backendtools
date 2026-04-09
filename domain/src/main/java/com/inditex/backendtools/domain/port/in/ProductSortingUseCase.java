package com.inditex.backendtools.domain.port.in;

import com.inditex.backendtools.domain.model.Product;
import com.inditex.backendtools.domain.model.ScoringCriteria;

import java.util.List;

public interface ProductSortingUseCase {

    List<Product> sortProducts(List<ScoringCriteria> criteria);
}
