package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.promotion.Promotion;
import com.eyeglasses.eyeglasses_store.repository.promotion.PromotionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminPromotionService {

    private final PromotionRepository promotionRepository;

    public AdminPromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public Page<Promotion> getAllPromotions(Pageable pageable) {
        return promotionRepository.findAll(pageable);
    }

    public List<Promotion> getActivePromotions() {
        return promotionRepository.findByActiveTrue();
    }

    public List<Promotion> getValidPromotions() {
        return promotionRepository.findValidPromotions(OffsetDateTime.now());
    }

    public Promotion getPromotionById(UUID promotionId) {
        return promotionRepository.findById(promotionId).orElseThrow();
    }

    public Optional<Promotion> getPromotionByCode(String code) {
        return promotionRepository.findByCode(code);
    }

    @Transactional
    public Promotion createPromotion(Map<String, Object> promotionData) {
        Promotion promotion = new Promotion();
        promotion.setCode((String) promotionData.get("code"));
        promotion.setName((String) promotionData.get("name"));
        promotion.setDescription((String) promotionData.get("description"));
        promotion.setType((String) promotionData.get("type"));

        if (promotionData.get("value") != null) {
            promotion.setValue(new BigDecimal(promotionData.get("value").toString()));
        }
        if (promotionData.get("minOrderAmount") != null) {
            promotion.setMinOrderAmount(new BigDecimal(promotionData.get("minOrderAmount").toString()));
        }
        if (promotionData.get("maxDiscountAmount") != null) {
            promotion.setMaxDiscountAmount(new BigDecimal(promotionData.get("maxDiscountAmount").toString()));
        }
        if (promotionData.get("usageLimit") != null) {
            promotion.setUsageLimit((Integer) promotionData.get("usageLimit"));
        }

        promotion.setStartDate(OffsetDateTime.parse(promotionData.get("startDate").toString()));
        promotion.setEndDate(OffsetDateTime.parse(promotionData.get("endDate").toString()));
        promotion.setActive((Boolean) promotionData.getOrDefault("active", true));
        promotion.setApplicableTo((String) promotionData.get("applicableTo"));
        promotion.setApplicableIds((String) promotionData.get("applicableIds"));

        return promotionRepository.save(promotion);
    }

    @Transactional
    public Promotion updatePromotion(UUID promotionId, Map<String, Object> promotionData) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow();

        if (promotionData.get("code") != null) {
            promotion.setCode((String) promotionData.get("code"));
        }
        if (promotionData.get("name") != null) {
            promotion.setName((String) promotionData.get("name"));
        }
        if (promotionData.get("description") != null) {
            promotion.setDescription((String) promotionData.get("description"));
        }
        if (promotionData.get("type") != null) {
            promotion.setType((String) promotionData.get("type"));
        }
        if (promotionData.get("value") != null) {
            promotion.setValue(new BigDecimal(promotionData.get("value").toString()));
        }
        if (promotionData.get("minOrderAmount") != null) {
            promotion.setMinOrderAmount(new BigDecimal(promotionData.get("minOrderAmount").toString()));
        }
        if (promotionData.get("maxDiscountAmount") != null) {
            promotion.setMaxDiscountAmount(new BigDecimal(promotionData.get("maxDiscountAmount").toString()));
        }
        if (promotionData.get("usageLimit") != null) {
            promotion.setUsageLimit((Integer) promotionData.get("usageLimit"));
        }
        if (promotionData.get("startDate") != null) {
            promotion.setStartDate(OffsetDateTime.parse(promotionData.get("startDate").toString()));
        }
        if (promotionData.get("endDate") != null) {
            promotion.setEndDate(OffsetDateTime.parse(promotionData.get("endDate").toString()));
        }
        if (promotionData.get("active") != null) {
            promotion.setActive((Boolean) promotionData.get("active"));
        }
        if (promotionData.get("applicableTo") != null) {
            promotion.setApplicableTo((String) promotionData.get("applicableTo"));
        }
        if (promotionData.get("applicableIds") != null) {
            promotion.setApplicableIds((String) promotionData.get("applicableIds"));
        }

        return promotionRepository.save(promotion);
    }

    @Transactional
    public Promotion togglePromotionStatus(UUID promotionId, boolean active) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow();
        promotion.setActive(active);
        return promotionRepository.save(promotion);
    }

    @Transactional
    public void deletePromotion(UUID promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow();
        promotionRepository.delete(promotion);
    }

    public Map<String, Object> validatePromotionCode(String code) {
        Optional<Promotion> promotionOpt = promotionRepository.findValidPromotionByCode(code, OffsetDateTime.now());

        if (promotionOpt.isEmpty()) {
            return Map.of(
                    "valid", false,
                    "error", "Promotion code not found or expired");
        }

        Promotion promotion = promotionOpt.get();
        return Map.of(
                "valid", true,
                "promotion", Map.of(
                        "id", promotion.getId(),
                        "code", promotion.getCode(),
                        "name", promotion.getName(),
                        "type", promotion.getType(),
                        "value", promotion.getValue(),
                        "minOrderAmount", promotion.getMinOrderAmount(),
                        "maxDiscountAmount", promotion.getMaxDiscountAmount()));
    }

    public Map<String, Object> getPromotionSummary() {
        long totalPromotions = promotionRepository.count();
        long activePromotions = promotionRepository.countActivePromotions();
        long validPromotions = promotionRepository.countCurrentlyValidPromotions(OffsetDateTime.now());
        long totalUsedCount = promotionRepository.sumTotalUsedCount();

        return Map.of(
                "total", totalPromotions,
                "active", activePromotions,
                "valid", validPromotions,
                "used", totalUsedCount,
                "expired", totalPromotions - validPromotions);
    }
}
