package com.eyeglasses.eyeglasses_store.repository.analytics;

import com.eyeglasses.eyeglasses_store.entity.analytics.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SearchLogRepository extends JpaRepository<SearchLog, UUID> {
}
