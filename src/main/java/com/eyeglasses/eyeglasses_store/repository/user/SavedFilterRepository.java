package com.eyeglasses.eyeglasses_store.repository.user;

import com.eyeglasses.eyeglasses_store.entity.user.SavedFilter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SavedFilterRepository extends JpaRepository<SavedFilter, UUID> {
    List<SavedFilter> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
