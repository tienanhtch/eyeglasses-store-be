package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.promotion.Promotion;
import com.eyeglasses.eyeglasses_store.service.AdminPromotionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/promotions")
public class AdminPromotionController {

    private final AdminPromotionService adminPromotionService;

    public AdminPromotionController(AdminPromotionService adminPromotionService) {
        this.adminPromotionService = adminPromotionService;
    }

    @GetMapping
    public ResponseEntity<Page<Promotion>> getAllPromotions(Pageable pageable) {
        Page<Promotion> promotions = adminPromotionService.getAllPromotions(pageable);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Promotion>> getActivePromotions() {
        List<Promotion> promotions = adminPromotionService.getActivePromotions();
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/valid")
    public ResponseEntity<List<Promotion>> getValidPromotions() {
        List<Promotion> promotions = adminPromotionService.getValidPromotions();
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/{promotionId}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable UUID promotionId) {
        Promotion promotion = adminPromotionService.getPromotionById(promotionId);
        return ResponseEntity.ok(promotion);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Promotion> getPromotionByCode(@PathVariable String code) {
        return adminPromotionService.getPromotionByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPromotion(@RequestBody Map<String, Object> promotionData) {
        try {
            var promotion = adminPromotionService.createPromotion(promotionData);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "promotionId", promotion.getId(),
                    "code", promotion.getCode(),
                    "name", promotion.getName(),
                    "message", "Promotion created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{promotionId}")
    public ResponseEntity<Map<String, Object>> updatePromotion(
            @PathVariable UUID promotionId,
            @RequestBody Map<String, Object> promotionData) {
        try {
            var promotion = adminPromotionService.updatePromotion(promotionId, promotionData);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "promotionId", promotion.getId(),
                    "code", promotion.getCode(),
                    "name", promotion.getName(),
                    "message", "Promotion updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{promotionId}/toggle")
    public ResponseEntity<Map<String, Object>> togglePromotionStatus(
            @PathVariable UUID promotionId,
            @RequestParam boolean active) {
        try {
            var promotion = adminPromotionService.togglePromotionStatus(promotionId, active);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "promotionId", promotion.getId(),
                    "active", promotion.isActive(),
                    "message", "Promotion status updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{promotionId}")
    public ResponseEntity<Map<String, Object>> deletePromotion(@PathVariable UUID promotionId) {
        try {
            adminPromotionService.deletePromotion(promotionId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Promotion deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<Map<String, Object>> validatePromotionCode(@PathVariable String code) {
        return ResponseEntity.ok(adminPromotionService.validatePromotionCode(code));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getPromotionSummary() {
        return ResponseEntity.ok(adminPromotionService.getPromotionSummary());
    }
}
