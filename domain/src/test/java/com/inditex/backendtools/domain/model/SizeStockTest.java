package com.inditex.backendtools.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SizeStockTest {

    @Test
    void hasStock_returnsTrue_whenQuantityIsPositive() {
        SizeStock sizeStock = new SizeStock("S", 5);
        assertThat(sizeStock.hasStock()).isTrue();
    }

    @Test
    void hasStock_returnsFalse_whenQuantityIsZero() {
        SizeStock sizeStock = new SizeStock("M", 0);
        assertThat(sizeStock.hasStock()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100})
    void constructor_throwsException_whenQuantityIsNegative(int quantity) {
        assertThatThrownBy(() -> new SizeStock("L", quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must not be negative");
    }

    @Test
    void constructor_throwsException_whenSizeIsBlank() {
        assertThatThrownBy(() -> new SizeStock("  ", 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Size must not be blank");
    }
}
