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
    @DisplayName("weighted sort sales(0.4) + stock(0.6) returns 6 products in expected order with stock breakdown")
    void sortProducts_withSalesAndStockCriteria_returnsCorrectOrderWithStock() throws Exception {
        SortRequestDto request = new SortRequestDto(List.of(
                new CriteriaDto("sales_units", 0.4),
                new CriteriaDto("stock_ratio", 0.6)
        ));

        mockMvc.perform(post("/api/v1/products/sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                // Ranking by score (sales*0.4 + stock*0.6)
                .andExpect(jsonPath("$[0].id").value(3))   // RAISED PRINT       score=64.92
                .andExpect(jsonPath("$[1].id").value(2))   // CONTRASTING FABRIC score=63.08
                .andExpect(jsonPath("$[2].id").value(6))   // SLOGAN              score=61.23
                .andExpect(jsonPath("$[3].id").value(4))   // PLEATED             score=60.18
                .andExpect(jsonPath("$[4].id").value(5))   // CONTRASTING LACE   score=60.00
                .andExpect(jsonPath("$[5].id").value(1));   // V-NECK BASIC        score=46.15
    }

    @Test
    @DisplayName("pure stock ratio ranking: last place has only 1 in-stock size (CONTRASTING LACE)")
    void sortProducts_withOnlyStockCriteria_lastProductHasWorstStockAndIsVisibleInResponse() throws Exception {
        SortRequestDto request = new SortRequestDto(List.of(
                new CriteriaDto("stock_ratio", 1.0)
        ));

        mockMvc.perform(post("/api/v1/products/sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                // Last product: CONTRASTING LACE T-SHIRT — only M has stock (1/3 sizes)
                .andExpect(jsonPath("$[5].id").value(5))
                .andExpect(jsonPath("$[5].name").value("CONTRASTING LACE T-SHIRT"))
                .andExpect(jsonPath("$[5].stock", hasSize(3)))
                .andExpect(jsonPath("$[5].stock[0].size").value("S"))
                .andExpect(jsonPath("$[5].stock[0].quantity").value(0))
                .andExpect(jsonPath("$[5].stock[1].size").value("M"))
                .andExpect(jsonPath("$[5].stock[1].quantity").value(1))
                .andExpect(jsonPath("$[5].stock[2].size").value("L"))
                .andExpect(jsonPath("$[5].stock[2].quantity").value(0));
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
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].salesUnits").value(650))
                .andExpect(jsonPath("$[5].id").value(4))
                .andExpect(jsonPath("$[5].salesUnits").value(3));
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
