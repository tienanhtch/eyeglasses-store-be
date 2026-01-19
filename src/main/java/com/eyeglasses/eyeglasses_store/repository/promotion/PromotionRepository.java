package com.eyeglasses.eyeglasses_store.repository.promotion;

import com.eyeglasses.eyeglasses_store.entity.promotion.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    Optional<Promotion> findByCode(String code);

    List<Promotion> findByActiveTrue();

    List<Promotion> findByActiveTrueAndStartDateBeforeAndEndDateAfter(OffsetDateTime startDate, OffsetDateTime endDate);

    @Query("SELECT p FROM Promotion p WHERE p.active = true AND p.startDate <= :now AND p.endDate >= :now AND (p.usageLimit IS NULL OR p.usedCount < p.usageLimit)")
    List<Promotion> findValidPromotions(@Param("now") OffsetDateTime now);

    @Query("SELECT p FROM Promotion p WHERE p.code = :code AND p.active = true AND p.startDate <= :now AND p.endDate >= :now AND (p.usageLimit IS NULL OR p.usedCount < p.usageLimit)")
    Optional<Promotion> findValidPromotionByCode(@Param("code") String code, @Param("now") OffsetDateTime now);

    @Query("SELECT COUNT(p) FROM Promotion p WHERE p.active = true")
    long countActivePromotions();

    @Query("SELECT COUNT(p) FROM Promotion p WHERE p.active = true AND p.startDate <= :now AND p.endDate >= :now")
    long countCurrentlyValidPromotions(@Param("now") OffsetDateTime now);

    @Query("SELECT COALESCE(SUM(p.usedCount), 0) FROM Promotion p")
    long sumTotalUsedCount();
}
