package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.order.Order;
import com.eyeglasses.eyeglasses_store.entity.order.OrderItem;
import com.eyeglasses.eyeglasses_store.entity.order.Payment;
import com.eyeglasses.eyeglasses_store.service.AdminOrderManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/orders")
public class AdminOrderManagementController {

    private final AdminOrderManagementService adminOrderManagementService;

    public AdminOrderManagementController(AdminOrderManagementService adminOrderManagementService) {
        this.adminOrderManagementService = adminOrderManagementService;
    }

    @GetMapping
    public ResponseEntity<Page<Order>> getAllOrders(Pageable pageable) {
        Page<Order> orders = adminOrderManagementService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Order>> getOrdersByStatus(@PathVariable String status, Pageable pageable) {
        Page<Order> orders = adminOrderManagementService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID orderId) {
        Order order = adminOrderManagementService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable UUID orderId) {
        List<OrderItem> items = adminOrderManagementService.getOrderItems(orderId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{orderId}/payments")
    public ResponseEntity<List<Payment>> getOrderPayments(@PathVariable UUID orderId) {
        List<Payment> payments = adminOrderManagementService.getOrderPayments(orderId);
        return ResponseEntity.ok(payments);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam String status,
            @RequestParam(required = false) String note) {
        try {
            Order order = adminOrderManagementService.updateOrderStatus(orderId, status, note);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orderId", order.getId(),
                    "status", order.getStatus(),
                    "message", "Order status updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/process")
    public ResponseEntity<Map<String, Object>> processOrder(@PathVariable UUID orderId) {
        try {
            Order order = adminOrderManagementService.processOrder(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orderId", order.getId(),
                    "status", order.getStatus(),
                    "message", "Order processing started"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/ship")
    public ResponseEntity<Map<String, Object>> shipOrder(
            @PathVariable UUID orderId,
            @RequestParam String trackingNumber) {
        try {
            Order order = adminOrderManagementService.shipOrder(orderId, trackingNumber);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orderId", order.getId(),
                    "status", order.getStatus(),
                    "trackingNumber", trackingNumber,
                    "message", "Order shipped"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<Map<String, Object>> completeOrder(@PathVariable UUID orderId) {
        try {
            Order order = adminOrderManagementService.completeOrder(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orderId", order.getId(),
                    "status", order.getStatus(),
                    "message", "Order completed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam String reason) {
        try {
            Order order = adminOrderManagementService.cancelOrder(orderId, reason);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orderId", order.getId(),
                    "status", order.getStatus(),
                    "message", "Order cancelled"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getOrderSummary() {
        return ResponseEntity.ok(adminOrderManagementService.getOrderSummary());
    }
}

