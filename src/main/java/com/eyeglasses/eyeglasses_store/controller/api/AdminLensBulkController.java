package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.lens.LensPackage;
import com.eyeglasses.eyeglasses_store.service.AdminLensBulkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/lens-bulk")
public class AdminLensBulkController {

    private final AdminLensBulkService adminLensBulkService;

    public AdminLensBulkController(AdminLensBulkService adminLensBulkService) {
        this.adminLensBulkService = adminLensBulkService;
    }

    @PostMapping("/packages")
    public ResponseEntity<Map<String, Object>> createLensPackage(@RequestBody Map<String, Object> packageData) {
        try {
            LensPackage lensPackage = adminLensBulkService.createLensPackage(packageData);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "lensPackageId", lensPackage.getId(),
                    "code", lensPackage.getCode(),
                    "name", lensPackage.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/packages/{lensPackageId}/status")
    public ResponseEntity<Map<String, Object>> updateLensPackageStatus(
            @PathVariable UUID lensPackageId,
            @RequestParam boolean active) {
        try {
            adminLensBulkService.updateLensPackageStatus(lensPackageId, active);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Lens package status updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/packages/{lensPackageId}/pricing")
    public ResponseEntity<Map<String, Object>> updateLensPackagePricing(
            @PathVariable UUID lensPackageId,
            @RequestParam BigDecimal retailPrice,
            @RequestParam(required = false) BigDecimal salePrice) {
        try {
            adminLensBulkService.updateLensPackagePricing(lensPackageId, retailPrice, salePrice);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Lens package pricing updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/packages/{lensPackageId}/ranges")
    public ResponseEntity<Map<String, Object>> updateLensPackageRanges(
            @PathVariable UUID lensPackageId,
            @RequestParam(required = false) BigDecimal minSph,
            @RequestParam(required = false) BigDecimal maxSph,
            @RequestParam(required = false) BigDecimal minCyl,
            @RequestParam(required = false) BigDecimal maxCyl) {
        try {
            adminLensBulkService.updateLensPackageRanges(lensPackageId, minSph, maxSph, minCyl, maxCyl);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Lens package ranges updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @DeleteMapping("/packages/{lensPackageId}")
    public ResponseEntity<Map<String, Object>> deleteLensPackage(@PathVariable UUID lensPackageId) {
        try {
            adminLensBulkService.deleteLensPackage(lensPackageId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Lens package deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/packages/bulk/status")
    public ResponseEntity<Map<String, Object>> bulkUpdateStatus(
            @RequestBody List<UUID> lensPackageIds,
            @RequestParam boolean active) {
        try {
            adminLensBulkService.bulkUpdateStatus(lensPackageIds, active);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Bulk status update completed",
                    "count", lensPackageIds.size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/packages/bulk/pricing")
    public ResponseEntity<Map<String, Object>> bulkUpdatePricing(
            @RequestBody List<UUID> lensPackageIds,
            @RequestParam BigDecimal retailPrice,
            @RequestParam(required = false) BigDecimal salePrice) {
        try {
            adminLensBulkService.bulkUpdatePricing(lensPackageIds, retailPrice, salePrice);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Bulk pricing update completed",
                    "count", lensPackageIds.size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }
}

