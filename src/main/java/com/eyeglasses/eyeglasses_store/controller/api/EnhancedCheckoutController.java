package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.order.Order;
import com.eyeglasses.eyeglasses_store.service.EnhancedCheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.PUBLIC_BASE + "/checkout")
public class EnhancedCheckoutController {

    private final EnhancedCheckoutService enhancedCheckoutService;

    public EnhancedCheckoutController(EnhancedCheckoutService enhancedCheckoutService) {
        this.enhancedCheckoutService = enhancedCheckoutService;
    }

    @PostMapping("/validate-promotion")
    public ResponseEntity<Map<String, Object>> validatePromotionCode(
            @RequestParam String code,
            @RequestParam UUID userId,
            @RequestParam BigDecimal orderTotal) {
        try {
            Map<String, Object> validation = enhancedCheckoutService.validatePromotionCode(code, userId, orderTotal);
            return ResponseEntity.ok(validation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/totals")
    public ResponseEntity<Map<String, Object>> calculateOrderTotals(
            @RequestParam UUID cartId,
            @RequestParam(required = false) String promotionCode,
            @RequestParam(required = false) BigDecimal shippingFee) {
        try {
            BigDecimal shipping = shippingFee != null ? shippingFee : BigDecimal.valueOf(30000);
            Map<String, Object> totals = enhancedCheckoutService.calculateOrderTotals(cartId, promotionCode, shipping);
            return ResponseEntity.ok(totals);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrderWithPromotion(
            @RequestParam UUID cartId,
            @RequestParam UUID shippingAddressId,
            @RequestParam UUID billingAddressId,
            @RequestParam String fulfillment,
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) String promotionCode) {
        try {
            Order order = enhancedCheckoutService.createOrderWithPromotion(
                    cartId, shippingAddressId, billingAddressId, fulfillment, storeId, promotionCode);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orderId", order.getId(),
                    "orderNumber", order.getOrderNo(),
                    "total", order.getGrandTotal(),
                    "message", "Order created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getCheckoutSummary(@RequestParam UUID cartId) {
        try {
            Map<String, Object> summary = enhancedCheckoutService.getCheckoutSummary(cartId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()));
        }
    }
}
