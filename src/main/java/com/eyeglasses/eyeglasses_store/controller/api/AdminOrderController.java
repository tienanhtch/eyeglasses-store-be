package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.AdminOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list() {
        return ResponseEntity.ok(adminOrderService.listAll());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.ok(adminOrderService.getDetail(orderId));
    }

    private record StatusRequest(String status) {
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable("orderId") UUID orderId,
            @RequestBody StatusRequest body) {
        return ResponseEntity.ok(adminOrderService.updateStatus(orderId, body.status()));
    }

    @PostMapping("/{orderId}/refund")
    public ResponseEntity<Map<String, Object>> refund(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.ok(adminOrderService.refundOrder(orderId));
    }
}
