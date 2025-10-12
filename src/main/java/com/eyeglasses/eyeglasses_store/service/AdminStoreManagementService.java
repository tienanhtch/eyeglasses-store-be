package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.store.Store;
import com.eyeglasses.eyeglasses_store.repository.store.StoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminStoreManagementService {

    private final StoreRepository storeRepository;

    public AdminStoreManagementService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public Page<Store> getAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable);
    }

    public List<Store> getAllActiveStores() {
        return storeRepository.findAll().stream()
                .filter(Store::isActive)
                .toList();
    }

    public Store getStoreById(UUID storeId) {
        return storeRepository.findById(storeId).orElseThrow();
    }

    @Transactional
    public Store createStore(Map<String, Object> storeData) {
        Store store = new Store();
        store.setCode((String) storeData.get("code"));
        store.setName((String) storeData.get("name"));
        store.setAddress((String) storeData.get("address"));

        if (storeData.get("lat") != null) {
            store.setLat(new BigDecimal(storeData.get("lat").toString()));
        }
        if (storeData.get("lng") != null) {
            store.setLng(new BigDecimal(storeData.get("lng").toString()));
        }

        store.setPhone((String) storeData.get("phone"));
        store.setOpenHours((String) storeData.get("openHours"));
        store.setActive((Boolean) storeData.getOrDefault("active", true));

        return storeRepository.save(store);
    }

    @Transactional
    public Store updateStore(UUID storeId, Map<String, Object> storeData) {
        Store store = storeRepository.findById(storeId).orElseThrow();

        if (storeData.get("code") != null) {
            store.setCode((String) storeData.get("code"));
        }
        if (storeData.get("name") != null) {
            store.setName((String) storeData.get("name"));
        }
        if (storeData.get("address") != null) {
            store.setAddress((String) storeData.get("address"));
        }
        if (storeData.get("lat") != null) {
            store.setLat(new BigDecimal(storeData.get("lat").toString()));
        }
        if (storeData.get("lng") != null) {
            store.setLng(new BigDecimal(storeData.get("lng").toString()));
        }
        if (storeData.get("phone") != null) {
            store.setPhone((String) storeData.get("phone"));
        }
        if (storeData.get("openHours") != null) {
            store.setOpenHours((String) storeData.get("openHours"));
        }
        if (storeData.get("active") != null) {
            store.setActive((Boolean) storeData.get("active"));
        }

        return storeRepository.save(store);
    }

    @Transactional
    public Store toggleStoreStatus(UUID storeId, boolean active) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        store.setActive(active);
        return storeRepository.save(store);
    }

    @Transactional
    public void deleteStore(UUID storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        storeRepository.delete(store);
    }

    public Map<String, Object> getStoreSummary() {
        List<Store> allStores = storeRepository.findAll();
        long totalStores = allStores.size();
        long activeStores = allStores.stream().mapToLong(s -> s.isActive() ? 1 : 0).sum();
        long inactiveStores = totalStores - activeStores;

        return Map.of(
                "total", totalStores,
                "active", activeStores,
                "inactive", inactiveStores);
    }
}

