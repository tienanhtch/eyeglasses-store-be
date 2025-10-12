package com.eyeglasses.eyeglasses_store.repository.catalog;

import com.eyeglasses.eyeglasses_store.entity.catalog.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, UUID> {
    List<CategoryAttribute> findByCategoryIdOrderBySortOrderAsc(UUID categoryId);
}
