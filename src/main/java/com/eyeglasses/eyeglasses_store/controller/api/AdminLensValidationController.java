package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.AdminLensValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/lens-validation")
public class AdminLensValidationController {

    private final AdminLensValidationService adminLensValidationService;

    public AdminLensValidationController(AdminLensValidationService adminLensValidationService) {
        this.adminLensValidationService = adminLensValidationService;
    }

    @GetMapping("/package/{lensPackageId}")
    public ResponseEntity<Map<String, Object>> validateLensPackage(@PathVariable UUID lensPackageId) {
        try {
            Map<String, Object> result = adminLensValidationService.validateLensPackage(lensPackageId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to validate lens package: " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> validateAllLensPackages() {
        try {
            Map<String, Object> result = adminLensValidationService.validateAllLensPackages();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to validate lens packages: " + e.getMessage()));
        }
    }
}

