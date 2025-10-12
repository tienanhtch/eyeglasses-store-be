package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.lens.Prescription;
import com.eyeglasses.eyeglasses_store.service.EnhancedLensService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.PUBLIC_BASE + "/lens")
public class EnhancedLensController {

    private final EnhancedLensService enhancedLensService;

    public EnhancedLensController(EnhancedLensService enhancedLensService) {
        this.enhancedLensService = enhancedLensService;
    }

    @PostMapping("/validate/{lensPackageId}")
    public ResponseEntity<Map<String, Object>> validatePrescription(
            @PathVariable UUID lensPackageId,
            @RequestBody Map<String, Object> prescriptionData) {
        try {
            Map<String, Object> validation = enhancedLensService.validatePrescriptionWithLensPackage(lensPackageId,
                    prescriptionData);
            return ResponseEntity.ok(validation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/pricing")
    public ResponseEntity<Map<String, Object>> calculatePricing(
            @RequestParam UUID frameVariantId,
            @RequestParam UUID lensPackageId,
            @RequestBody Map<String, Object> prescriptionData) {
        try {
            Map<String, Object> pricing = enhancedLensService.calculateLensPricing(frameVariantId, lensPackageId,
                    prescriptionData);
            return ResponseEntity.ok(pricing);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/recommendations")
    public ResponseEntity<List<Map<String, Object>>> getLensRecommendations(
            @RequestBody Map<String, Object> prescriptionData) {
        try {
            List<Map<String, Object>> recommendations = enhancedLensService
                    .getRecommendedLensPackages(prescriptionData);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of(Map.of(
                    "error", e.getMessage())));
        }
    }

    @PostMapping("/prescriptions")
    public ResponseEntity<Map<String, Object>> savePrescription(
            @RequestParam UUID userId,
            @RequestBody Map<String, Object> prescriptionData) {
        try {
            Prescription prescription = enhancedLensService.savePrescription(userId, prescriptionData);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "prescriptionId", prescription.getId(),
                    "message", "Prescription saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }
}
