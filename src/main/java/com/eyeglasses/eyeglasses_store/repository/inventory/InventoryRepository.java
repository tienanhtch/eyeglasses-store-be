package com.eyeglasses.eyeglasses_store.repository.inventory;

import com.eyeglasses.eyeglasses_store.entity.inventory.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    @Query("select coalesce(sum(i.onHand - i.reserved),0) from Inventory i where i.variant.id = :variantId")
    Integer totalAvailableByVariant(@Param("variantId") UUID variantId);
}
