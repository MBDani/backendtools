package com.inditex.backendtools.infrastructure.adapter.out.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.backendtools.domain.model.Product;
import com.inditex.backendtools.domain.model.SizeStock;
import com.inditex.backendtools.domain.port.out.ProductRepository;
import com.inditex.backendtools.infrastructure.adapter.out.persistence.dto.ProductDataDto;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> products;

    public InMemoryProductRepository(ObjectMapper objectMapper,
                                     @org.springframework.beans.factory.annotation.Value("classpath:products.json") Resource productsResource) {
        this.products = loadProducts(objectMapper, productsResource);
    }

    @Override
    public List<Product> findAll() {
        return products;
    }

    private List<Product> loadProducts(ObjectMapper objectMapper, Resource resource) {
        try {
            List<ProductDataDto> data = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
            return data.stream().map(this::toDomain).toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load products from " + resource.getFilename(), e);
        }
    }

    private Product toDomain(ProductDataDto dto) {
        List<SizeStock> stock = dto.stock().stream()
                .map(s -> new SizeStock(s.size(), s.quantity()))
                .toList();
        return Product.builder()
                .id(dto.id())
                .name(dto.name())
                .salesUnits(dto.salesUnits())
                .stock(stock)
                .build();
    }
}
