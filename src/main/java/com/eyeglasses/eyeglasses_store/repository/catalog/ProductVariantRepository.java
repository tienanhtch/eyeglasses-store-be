package com.eyeglasses.eyeglasses_store.repository.catalog;

import com.eyeglasses.eyeglasses_store.entity.catalog.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    List<ProductVariant> findByProductId(UUID productId);
}
