package com.inditex.backendtools.domain.model;

public record SizeStock(String size, int quantity) {

    public SizeStock {
        if (size == null || size.isBlank()) {
            throw new IllegalArgumentException("Size must not be blank");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must not be negative");
        }
    }

    public boolean hasStock() {
        return quantity > 0;
    }
}
