package com.eyeglasses.eyeglasses_store.repository.catalog;

import com.eyeglasses.eyeglasses_store.entity.catalog.VariantImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VariantImageRepository extends JpaRepository<VariantImage, UUID> {
    List<VariantImage> findByVariantIdOrderBySortOrderAsc(UUID variantId);
}

