package com.inditex.backendtools.infrastructure.adapter.in.rest.mapper;

import com.inditex.backendtools.domain.model.Product;
import com.inditex.backendtools.domain.model.ScoringCriteria;
import com.inditex.backendtools.infrastructure.adapter.in.rest.dto.CriteriaDto;
import com.inditex.backendtools.infrastructure.adapter.in.rest.dto.ProductResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ProductRestMapper {

    @Mapping(source = "name", target = "criteriaName")
    ScoringCriteria toDomain(CriteriaDto dto);

    List<ScoringCriteria> toDomain(List<CriteriaDto> dtos);

    ProductResponseDto toResponse(Product product);

    List<ProductResponseDto> toResponse(List<Product> products);
}
