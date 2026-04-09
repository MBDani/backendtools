package com.inditex.backendtools.domain.port.out;

import com.inditex.backendtools.domain.model.Product;

import java.util.List;

public interface ProductRepository {

    List<Product> findAll();
}
