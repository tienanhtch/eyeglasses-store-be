package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.AdminShippingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/shipping")
public class AdminShippingController {

    private final AdminShippingService adminShippingService;

    public AdminShippingController(AdminShippingService adminShippingService) {
        this.adminShippingService = adminShippingService;
    }

    @PostMapping("/labels/{orderId}")
    public ResponseEntity<Map<String, Object>> generateShippingLabel(@PathVariable UUID orderId) {
        try {
            Map<String, Object> label = adminShippingService.generateShippingLabel(orderId);
            return ResponseEntity.ok(label);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to generate shipping label: " + e.getMessage()));
        }
    }

    @GetMapping("/rates/{orderId}")
    public ResponseEntity<Map<String, Object>> getShippingRates(@PathVariable UUID orderId) {
        try {
            Map<String, Object> rates = adminShippingService.getShippingRates(orderId);
            return ResponseEntity.ok(rates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to get shipping rates: " + e.getMessage()));
        }
    }

    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<Map<String, Object>> trackShipment(@PathVariable String trackingNumber) {
        try {
            Map<String, Object> tracking = adminShippingService.trackShipment(trackingNumber);
            return ResponseEntity.ok(tracking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to track shipment: " + e.getMessage()));
        }
    }

    @GetMapping("/labels/{orderId}/download")
    public ResponseEntity<Map<String, Object>> downloadShippingLabel(@PathVariable UUID orderId) {
        try {
            // Mock PDF download - in real implementation, generate actual PDF
            Map<String, Object> label = adminShippingService.generateShippingLabel(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "downloadUrl", "/api/v1/admin/shipping/labels/" + orderId + "/pdf",
                    "label", label));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Failed to download shipping label: " + e.getMessage()));
        }
    }
}

