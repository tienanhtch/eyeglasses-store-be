package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Public controller for endpoints that don't require authentication
 */
@RestController
@RequestMapping(ApiConstants.PUBLIC_BASE)
public class PublicController {

    /**
     * Get public product categories
     * GET /api/v1/public/categories
     */
    @GetMapping(ApiConstants.PUBLIC_CATEGORIES)
    public ResponseEntity<Map<String, Object>> getCategories() {
        Map<String, Object> response = new HashMap<>();
        response.put("categories", new String[] {
                "Kính cận", "Kính viễn", "Kính râm", "Kính đọc sách", "Kính thể thao"
        });
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * Get public products (limited info)
     * GET /api/v1/public/products
     */
    @GetMapping(ApiConstants.PUBLIC_PRODUCTS)
    public ResponseEntity<Map<String, Object>> getPublicProducts() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Public products endpoint - no authentication required");
        response.put("timestamp", LocalDateTime.now());
        response.put("note", "This endpoint returns limited product information");
        return ResponseEntity.ok(response);
    }

    /**
     * Get store information
     * GET /api/v1/public/store-info
     */
    @GetMapping("/store-info")
    public ResponseEntity<Map<String, Object>> getStoreInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("storeName", "Eyeglasses Store");
        response.put("description", "Premium eyewear for everyone");
        response.put("contact", Map.of(
                "phone", "+84 123 456 789",
                "email", "info@eyeglassesstore.com",
                "address", "123 Main Street, Ho Chi Minh City"));
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
