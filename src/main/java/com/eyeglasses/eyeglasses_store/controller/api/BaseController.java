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
 * Base controller for API health check and basic information
 */
@RestController
@RequestMapping("/")
public class BaseController {

    /**
     * Health check endpoint
     * GET /api/v1/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Eyeglasses Store API");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    /**
     * API information endpoint
     * GET /api/v1/info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "Eyeglasses Store API");
        response.put("description", "REST API for Eyeglasses Store Management System");
        response.put("version", "1.0.0");
        response.put("contextPath", ApiConstants.API_BASE_PATH);
        response.put("endpoints", Map.of(
                "health", ApiConstants.API_BASE_PATH + "/health",
                "auth", ApiConstants.API_BASE_PATH + ApiConstants.AUTH_BASE,
                "users", ApiConstants.API_BASE_PATH + ApiConstants.USERS_BASE,
                "products", ApiConstants.API_BASE_PATH + ApiConstants.PRODUCTS_BASE,
                "orders", ApiConstants.API_BASE_PATH + ApiConstants.ORDERS_BASE));
        return ResponseEntity.ok(response);
    }
}
