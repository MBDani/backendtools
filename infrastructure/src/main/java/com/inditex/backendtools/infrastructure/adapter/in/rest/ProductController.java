package com.inditex.backendtools.infrastructure.adapter.in.rest;

import com.inditex.backendtools.domain.port.in.ProductSortingUseCase;
import com.inditex.backendtools.infrastructure.adapter.in.rest.dto.ProductResponseDto;
import com.inditex.backendtools.infrastructure.adapter.in.rest.dto.SortRequestDto;
import com.inditex.backendtools.infrastructure.adapter.in.rest.mapper.ProductRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductSortingUseCase productSortingUseCase;
    private final ProductRestMapper mapper;

    public ProductController(ProductSortingUseCase productSortingUseCase, ProductRestMapper mapper) {
        this.productSortingUseCase = productSortingUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/sort")
    public ResponseEntity<List<ProductResponseDto>> sortProducts(@Valid @RequestBody SortRequestDto request) {
        List<ProductResponseDto> response = mapper.toResponse(
                productSortingUseCase.sortProducts(mapper.toDomain(request.criteria()))
        );
        return ResponseEntity.ok(response);
    }
}
