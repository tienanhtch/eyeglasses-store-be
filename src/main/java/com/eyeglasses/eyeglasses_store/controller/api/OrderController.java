package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ORDERS_BASE)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Tạo đơn từ giỏ hàng của user
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@RequestParam("userId") UUID userId,
            @RequestParam(value = "shippingAddressId", required = false) UUID shippingAddressId,
            @RequestParam(value = "billingAddressId", required = false) UUID billingAddressId,
            @RequestParam(value = "fulfillment", required = false) String fulfillment,
            @RequestParam(value = "storeId", required = false) UUID storeId) {
        return ResponseEntity.ok(orderService.createOrderFromCartAdvanced(userId, shippingAddressId, billingAddressId,
                fulfillment, storeId));
    }

    // Danh sách đơn theo user
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(orderService.listOrders(userId));
    }

    // Chi tiết đơn theo id
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }

    // Khởi tạo thanh toán VNPAY cho đơn
    @PostMapping("/{orderId}/payments/vnpay")
    public ResponseEntity<Map<String, Object>> initVnpay(@PathVariable("orderId") UUID orderId,
            @RequestParam(value = "returnUrl", required = false) String returnUrl) {
        return ResponseEntity.ok(orderService.initVnpaySigned(orderId, returnUrl));
    }

    // Callback giả lập VNPAY (sandbox)
    @GetMapping("/payments/vnpay/return")
    public ResponseEntity<Map<String, Object>> vnpayReturn(@RequestParam("txRef") String txRef,
            @RequestParam(value = "status", defaultValue = "success") String status) {
        return ResponseEntity.ok(orderService.vnpayReturn(txRef, status));
    }
}
