package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.StaffServiceTicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.PUBLIC_BASE + "/staff/service-tickets")
public class StaffServiceTicketController {

    private final StaffServiceTicketService staffServiceTicketService;

    public StaffServiceTicketController(StaffServiceTicketService staffServiceTicketService) {
        this.staffServiceTicketService = staffServiceTicketService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getStaffServiceTickets(
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) String status) {
        try {
            List<Map<String, Object>> tickets = staffServiceTicketService.getStaffServiceTickets(storeId, status);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of(Map.of(
                    "error", e.getMessage())));
        }
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Map<String, Object>> getServiceTicketDetails(@PathVariable UUID ticketId) {
        try {
            Map<String, Object> details = staffServiceTicketService.getServiceTicketDetails(ticketId);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{ticketId}/receive")
    public ResponseEntity<Map<String, Object>> receiveServiceTicket(
            @PathVariable UUID ticketId,
            @RequestParam UUID staffId,
            @RequestParam(required = false) String conditionNotes,
            @RequestBody(required = false) List<String> imageUrls) {
        try {
            Map<String, Object> result = staffServiceTicketService.receiveServiceTicket(ticketId, staffId,
                    conditionNotes, imageUrls);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{ticketId}/start-processing")
    public ResponseEntity<Map<String, Object>> startProcessing(
            @PathVariable UUID ticketId,
            @RequestParam UUID staffId,
            @RequestParam(required = false) String processNotes) {
        try {
            Map<String, Object> result = staffServiceTicketService.startProcessing(ticketId, staffId, processNotes);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{ticketId}/estimate")
    public ResponseEntity<Map<String, Object>> estimateService(
            @PathVariable UUID ticketId,
            @RequestParam BigDecimal estimatedCost,
            @RequestParam String estimatedTime,
            @RequestParam(required = false) String materials) {
        try {
            Map<String, Object> result = staffServiceTicketService.estimateService(ticketId, estimatedCost,
                    estimatedTime, materials);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{ticketId}/complete")
    public ResponseEntity<Map<String, Object>> completeService(
            @PathVariable UUID ticketId,
            @RequestParam UUID staffId,
            @RequestParam(required = false) String completionNotes,
            @RequestParam BigDecimal finalCost) {
        try {
            Map<String, Object> result = staffServiceTicketService.completeService(ticketId, staffId, completionNotes,
                    finalCost);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/walk-in")
    public ResponseEntity<Map<String, Object>> createWalkInTicket(
            @RequestParam UUID storeId,
            @RequestParam String customerName,
            @RequestParam String customerPhone,
            @RequestParam String serviceType,
            @RequestParam String description,
            @RequestParam(required = false) String conditionNotes) {
        try {
            Map<String, Object> result = staffServiceTicketService.createWalkInTicket(
                    storeId, customerName, customerPhone, serviceType, description, conditionNotes);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getStaffServiceDashboard(@RequestParam UUID storeId) {
        try {
            Map<String, Object> dashboard = staffServiceTicketService.getStaffServiceDashboard(storeId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<Map<String, Object>> updateTicketStatus(
            @PathVariable UUID ticketId,
            @RequestParam String newStatus,
            @RequestParam(required = false) String notes) {
        try {
            Map<String, Object> result = staffServiceTicketService.updateTicketStatus(ticketId, newStatus, notes);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }
}
