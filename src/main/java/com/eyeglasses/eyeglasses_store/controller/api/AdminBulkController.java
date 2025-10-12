package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.catalog.Product;
import com.eyeglasses.eyeglasses_store.service.AdminBulkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/bulk")
public class AdminBulkController {

    private final AdminBulkService adminBulkService;

    public AdminBulkController(AdminBulkService adminBulkService) {
        this.adminBulkService = adminBulkService;
    }

    @PostMapping("/products")
    public ResponseEntity<Map<String, Object>> createProductWithVariants(@RequestBody Map<String, Object> productData) {
        try {
            Product product = adminBulkService.createProductWithVariants(productData);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "productId", product.getId(),
                    "slug", product.getSlug(),
                    "name", product.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/products/{productId}/status")
    public ResponseEntity<Map<String, Object>> updateProductStatus(
            @PathVariable UUID productId,
            @RequestParam boolean published) {
        try {
            adminBulkService.updateProductStatus(productId, published);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product status updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/variants/{variantId}/status")
    public ResponseEntity<Map<String, Object>> updateVariantStatus(
            @PathVariable UUID variantId,
            @RequestParam boolean active) {
        try {
            adminBulkService.updateVariantStatus(variantId, active);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Variant status updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable UUID productId) {
        try {
            adminBulkService.deleteProduct(productId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @DeleteMapping("/variants/{variantId}")
    public ResponseEntity<Map<String, Object>> deleteVariant(@PathVariable UUID variantId) {
        try {
            adminBulkService.deleteVariant(variantId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Variant deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }
}

