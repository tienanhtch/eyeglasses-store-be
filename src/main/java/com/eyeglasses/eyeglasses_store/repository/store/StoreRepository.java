package com.eyeglasses.eyeglasses_store.repository.store;

import com.eyeglasses.eyeglasses_store.entity.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    List<Store> findByActiveTrueOrderByNameAsc();
}
