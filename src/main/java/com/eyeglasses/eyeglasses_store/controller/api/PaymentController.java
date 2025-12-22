package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.service.payment.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        try {
            UUID orderId = UUID.fromString((String) request.get("orderId"));
            String paymentMethod = (String) request.get("paymentMethod");
            String ipAddress = getClientIpAddress(httpRequest);

            Map<String, Object> response = paymentService.createPayment(orderId, paymentMethod, ipAddress);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating payment", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @GetMapping("/vnpay/return")
    public ResponseEntity<?> vnpayReturn(@RequestParam Map<String, String> params) {
        try {
            paymentService.handleVNPayReturn(params);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment processed successfully"));
        } catch (Exception e) {
            log.error("Error handling VNPay return", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @PostMapping("/vnpay/ipn")
    public ResponseEntity<?> vnpayIPN(@RequestParam Map<String, String> params) {
        try {
            paymentService.handleVNPayReturn(params);
            return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "success"));
        } catch (Exception e) {
            log.error("Error handling VNPay IPN", e);
            return ResponseEntity.ok(Map.of("RspCode", "99", "Message", "failed"));
        }
    }

    // MoMo endpoints - temporarily disabled
    /*
     * @GetMapping("/momo/return")
     * public ResponseEntity<?> momoReturn(@RequestParam Map<String, String> params)
     * {
     * try {
     * paymentService.handleMoMoReturn(params);
     * return ResponseEntity.ok(Map.of(
     * "success", true,
     * "message", "Payment processed successfully"
     * ));
     * } catch (Exception e) {
     * log.error("Error handling MoMo return", e);
     * return ResponseEntity.badRequest().body(Map.of(
     * "success", false,
     * "message", e.getMessage()
     * ));
     * }
     * }
     * 
     * @PostMapping("/momo/ipn")
     * public ResponseEntity<?> momoIPN(@RequestBody Map<String, String> params) {
     * try {
     * paymentService.handleMoMoReturn(params);
     * return ResponseEntity.ok(Map.of("resultCode", 0, "message", "success"));
     * } catch (Exception e) {
     * log.error("Error handling MoMo IPN", e);
     * return ResponseEntity.ok(Map.of("resultCode", 1, "message", "failed"));
     * }
     * }
     */

    @GetMapping("/transaction/{orderId}")
    public ResponseEntity<?> getTransaction(@PathVariable UUID orderId) {
        try {
            var transaction = paymentService.getTransactionByOrderId(orderId);
            Map<String, Object> response = new HashMap<>();
            response.put("id", transaction.getId());
            response.put("orderId", transaction.getOrder().getId());
            response.put("paymentMethod", transaction.getPaymentMethod());
            response.put("amount", transaction.getAmount());
            response.put("status", transaction.getStatus());
            response.put("transactionId", transaction.getTransactionId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
