package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.order.ReturnRequest;
import com.eyeglasses.eyeglasses_store.service.AdminReturnService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/returns")
public class AdminReturnController {

    private final AdminReturnService adminReturnService;

    public AdminReturnController(AdminReturnService adminReturnService) {
        this.adminReturnService = adminReturnService;
    }

    @GetMapping
    public ResponseEntity<Page<ReturnRequest>> getAllReturnRequests(Pageable pageable) {
        Page<ReturnRequest> returns = adminReturnService.getAllReturnRequests(pageable);
        return ResponseEntity.ok(returns);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ReturnRequest>> getReturnRequestsByStatus(
            @PathVariable String status, Pageable pageable) {
        Page<ReturnRequest> returns = adminReturnService.getReturnRequestsByStatus(status, pageable);
        return ResponseEntity.ok(returns);
    }

    @GetMapping("/{returnRequestId}")
    public ResponseEntity<ReturnRequest> getReturnRequestById(@PathVariable UUID returnRequestId) {
        ReturnRequest returnRequest = adminReturnService.getReturnRequestById(returnRequestId);
        return ResponseEntity.ok(returnRequest);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ReturnRequest>> getReturnRequestsByOrder(@PathVariable UUID orderId) {
        List<ReturnRequest> returns = adminReturnService.getReturnRequestsByOrder(orderId);
        return ResponseEntity.ok(returns);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReturnRequest(
            @RequestParam UUID orderId,
            @RequestParam UUID orderItemId,
            @RequestParam String reason,
            @RequestParam(required = false) String description,
            @RequestParam BigDecimal refundAmount,
            @RequestParam String refundMethod) {
        try {
            ReturnRequest returnRequest = adminReturnService.createReturnRequest(
                    orderId, orderItemId, reason, description, refundAmount, refundMethod);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "returnRequestId", returnRequest.getId(),
                    "rmaNumber", returnRequest.getRmaNumber(),
                    "message", "Return request created"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{returnRequestId}/approve")
    public ResponseEntity<Map<String, Object>> approveReturnRequest(
            @PathVariable UUID returnRequestId,
            @RequestParam(required = false) String adminNotes) {
        try {
            ReturnRequest returnRequest = adminReturnService.approveReturnRequest(returnRequestId, adminNotes);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "returnRequestId", returnRequest.getId(),
                    "status", returnRequest.getStatus(),
                    "message", "Return request approved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{returnRequestId}/reject")
    public ResponseEntity<Map<String, Object>> rejectReturnRequest(
            @PathVariable UUID returnRequestId,
            @RequestParam(required = false) String adminNotes) {
        try {
            ReturnRequest returnRequest = adminReturnService.rejectReturnRequest(returnRequestId, adminNotes);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "returnRequestId", returnRequest.getId(),
                    "status", returnRequest.getStatus(),
                    "message", "Return request rejected"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{returnRequestId}/process")
    public ResponseEntity<Map<String, Object>> processReturnRequest(@PathVariable UUID returnRequestId) {
        try {
            ReturnRequest returnRequest = adminReturnService.processReturnRequest(returnRequestId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "returnRequestId", returnRequest.getId(),
                    "status", returnRequest.getStatus(),
                    "message", "Return request processing started"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{returnRequestId}/complete")
    public ResponseEntity<Map<String, Object>> completeReturnRequest(@PathVariable UUID returnRequestId) {
        try {
            ReturnRequest returnRequest = adminReturnService.completeReturnRequest(returnRequestId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "returnRequestId", returnRequest.getId(),
                    "status", returnRequest.getStatus(),
                    "message", "Return request completed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getReturnSummary() {
        return ResponseEntity.ok(adminReturnService.getReturnSummary());
    }
}

