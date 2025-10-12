package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.inventory.Inventory;
import com.eyeglasses.eyeglasses_store.repository.inventory.InventoryRepository;
import com.eyeglasses.eyeglasses_store.repository.store.StoreRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AdminInventoryService {

    private final InventoryRepository inventoryRepository;
    private final StoreRepository storeRepository;
    private final ProductVariantRepository variantRepository;

    public AdminInventoryService(InventoryRepository inventoryRepository,
            StoreRepository storeRepository,
            ProductVariantRepository variantRepository) {
        this.inventoryRepository = inventoryRepository;
        this.storeRepository = storeRepository;
        this.variantRepository = variantRepository;
    }

    @Transactional
    public Inventory setStock(UUID storeId, UUID variantId, Integer onHand, Integer reserved) {
        var store = storeRepository.findById(storeId).orElseThrow();
        var variant = variantRepository.findById(variantId).orElseThrow();
        Optional<Inventory> invOpt = inventoryRepository.findAll().stream()
                .filter(i -> i.getStore().getId().equals(storeId) && i.getVariant().getId().equals(variantId))
                .findFirst();
        Inventory inv = invOpt.orElseGet(() -> {
            Inventory i = new Inventory();
            i.setStore(store);
            i.setVariant(variant);
            return i;
        });
        if (onHand != null)
            inv.setOnHand(onHand);
        if (reserved != null)
            inv.setReserved(reserved);
        return inventoryRepository.save(inv);
    }
}
