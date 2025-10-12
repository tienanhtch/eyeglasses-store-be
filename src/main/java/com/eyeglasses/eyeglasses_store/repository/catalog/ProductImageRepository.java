package com.eyeglasses.eyeglasses_store.repository.catalog;

import com.eyeglasses.eyeglasses_store.entity.catalog.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
    List<ProductImage> findByProductIdOrderBySortOrderAsc(UUID productId);
}
