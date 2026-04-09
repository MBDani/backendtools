package com.inditex.backendtools.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.backendtools.domain.exception.UnknownScoringCriteriaException;
import com.inditex.backendtools.domain.model.Product;
import com.inditex.backendtools.domain.model.SizeStock;
import com.inditex.backendtools.domain.port.in.ProductSortingUseCase;
import com.inditex.backendtools.infrastructure.adapter.in.rest.dto.CriteriaDto;
import com.inditex.backendtools.infrastructure.adapter.in.rest.dto.SortRequestDto;
import com.inditex.backendtools.infrastructure.adapter.in.rest.mapper.ProductRestMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@Import({ProductRestMapperImpl.class, GlobalExceptionHandler.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductSortingUseCase productSortingUseCase;

    @Test
    @DisplayName("POST /sort returns 200 with products in order")
    void sortProducts_returns200WithSortedProducts() throws Exception {
        List<Product> ranked = List.of(
                product(5, "CONTRASTING LACE T-SHIRT", 650),
                product(1, "V-NECK BASIC SHIRT", 100)
        );
        when(productSortingUseCase.sortProducts(anyList())).thenReturn(ranked);

        SortRequestDto request = new SortRequestDto(List.of(
                new CriteriaDto("sales_units", 1.0)
        ));

        mockMvc.perform(post("/api/v1/products/sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].name").value("CONTRASTING LACE T-SHIRT"))
                .andExpect(jsonPath("$[1].id").value(1));
    }

    @Test
    @DisplayName("POST /sort returns 400 when criteria list is empty")
    void sortProducts_returns400_whenCriteriaIsEmpty() throws Exception {
        SortRequestDto request = new SortRequestDto(List.of());

        mockMvc.perform(post("/api/v1/products/sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /sort returns 422 when criteria name is unknown")
    void sortProducts_returns422_whenCriteriaNameUnknown() throws Exception {
        when(productSortingUseCase.sortProducts(anyList()))
                .thenThrow(new UnknownScoringCriteriaException("unknown"));

        SortRequestDto request = new SortRequestDto(List.of(new CriteriaDto("unknown", 1.0)));

        mockMvc.perform(post("/api/v1/products/sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /sort returns 400 when weight is negative")
    void sortProducts_returns400_whenWeightIsNegative() throws Exception {
        SortRequestDto request = new SortRequestDto(List.of(new CriteriaDto("sales_units", -1.0)));

        mockMvc.perform(post("/api/v1/products/sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private Product product(int id, String name, int salesUnits) {
        return Product.builder()
                .id(id).name(name).salesUnits(salesUnits)
                .stock(List.of(new SizeStock("S", 5)))
                .build();
    }
}
