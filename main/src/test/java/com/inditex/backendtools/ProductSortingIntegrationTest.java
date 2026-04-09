package com.inditex.backendtools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.backendtools.infrastructure.adapter.in.rest.dto.CriteriaDto;
import com.inditex.backendtools.infrastructure.adapter.in.rest.dto.SortRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductSortingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("weighted sort with sales(0.4) + stock(0.6) returns all 6 products in expected order")
    void sortProducts_withSalesAndStockCriteria_returnsCorrectOrder() throws Exception {
        SortRequestDto request = new SortRequestDto(List.of(
                new CriteriaDto("sales_units", 0.4),
                new CriteriaDto("stock_ratio", 0.6)
        ));

        mockMvc.perform(post("/api/v1/products/sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].id").value(3))   // RAISED PRINT T-SHIRT       64.92
                .andExpect(jsonPath("$[1].id").value(2))   // CONTRASTING FABRIC T-SHIRT 63.08
                .andExpect(jsonPath("$[2].id").value(6))   // SLOGAN T-SHIRT             61.23
                .andExpect(jsonPath("$[3].id").value(4))   // PLEATED T-SHIRT            60.18
                .andExpect(jsonPath("$[4].id").value(5))   // CONTRASTING LACE T-SHIRT   60.00
                .andExpect(jsonPath("$[5].id").value(1));  // V-NECK BASIC SHIRT         46.15
    }

    @Test
    @DisplayName("pure sales units ranking puts CONTRASTING LACE T-SHIRT first (650 units)")
    void sortProducts_withOnlySalesCriteria_sortsbyHighestSales() throws Exception {
        SortRequestDto request = new SortRequestDto(List.of(
                new CriteriaDto("sales_units", 1.0)
        ));

        mockMvc.perform(post("/api/v1/products/sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))   // CONTRASTING LACE T-SHIRT 650 units
                .andExpect(jsonPath("$[5].id").value(4));  // PLEATED T-SHIRT           3 units
    }

    @Test
    @DisplayName("pure stock ratio ranking puts full-stock products first")
    void sortProducts_withOnlyStockCriteria_putsFullStockProductsFirst() throws Exception {
        SortRequestDto request = new SortRequestDto(List.of(
                new CriteriaDto("stock_ratio", 1.0)
        ));

        mockMvc.perform(post("/api/v1/products/sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[4].id").value(1))   // V-NECK BASIC SHIRT  2/3 sizes
                .andExpect(jsonPath("$[5].id").value(5));  // CONTRASTING LACE    1/3 sizes
    }

    @Test
    @DisplayName("unknown criteria name returns 422")
    void sortProducts_withUnknownCriteria_returns422() throws Exception {
        SortRequestDto request = new SortRequestDto(List.of(
                new CriteriaDto("non_existent_criteria", 1.0)
        ));

        mockMvc.perform(post("/api/v1/products/sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").exists());
    }
}
