package com.eyeglasses.eyeglasses_store.repository.inventory;

import com.eyeglasses.eyeglasses_store.entity.inventory.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    @Query("select coalesce(sum(i.onHand - i.reserved),0) from Inventory i where i.variant.id = :variantId")
    Integer totalAvailableByVariant(@Param("variantId") UUID variantId);

    @Query("select i from Inventory i join fetch i.store join fetch i.variant where i.variant.id = :variantId")
    List<Inventory> findByVariantId(@Param("variantId") UUID variantId);
}
