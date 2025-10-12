package com.eyeglasses.eyeglasses_store.repository.catalog;

import com.eyeglasses.eyeglasses_store.entity.catalog.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, UUID> {
    List<ProductAttribute> findByProductId(UUID productId);
}
