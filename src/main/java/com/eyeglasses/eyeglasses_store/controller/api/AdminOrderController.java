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
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        List<Map<String, Object>> allOrders = adminOrderService.listAll();
        
        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            allOrders = allOrders.stream()
                .filter(order -> status.equals(order.get("status")))
                .toList();
        }
        
        // Calculate pagination
        int totalElements = allOrders.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = page * size;
        int end = Math.min(start + size, totalElements);
        
        // Get page content
        List<Map<String, Object>> content = (start < totalElements) 
            ? allOrders.subList(start, end) 
            : List.of();
        
        return ResponseEntity.ok(Map.of(
            "content", content,
            "totalElements", totalElements,
            "totalPages", totalPages,
            "currentPage", page,
            "pageSize", size
        ));
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
