package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.appointment.AppointmentSlot;
import com.eyeglasses.eyeglasses_store.service.AdminAppointmentSlotService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/appointment-slots")
public class AdminAppointmentSlotController {

    private final AdminAppointmentSlotService adminAppointmentSlotService;

    public AdminAppointmentSlotController(AdminAppointmentSlotService adminAppointmentSlotService) {
        this.adminAppointmentSlotService = adminAppointmentSlotService;
    }

    @GetMapping("/{storeId}/available")
    public ResponseEntity<List<AppointmentSlot>> getAvailableSlots(
            @PathVariable UUID storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AppointmentSlot> slots = adminAppointmentSlotService.getAvailableSlots(storeId, date);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/{storeId}/range")
    public ResponseEntity<List<AppointmentSlot>> getSlotsByDateRange(
            @PathVariable UUID storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AppointmentSlot> slots = adminAppointmentSlotService.getSlotsByDateRange(storeId, startDate, endDate);
        return ResponseEntity.ok(slots);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createSlot(
            @RequestParam UUID storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(required = false) Integer maxAppointments,
            @RequestParam(required = false) String notes) {
        try {
            AppointmentSlot slot = adminAppointmentSlotService.createSlot(
                    storeId, date, startTime, endTime, maxAppointments, notes);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "slotId", slot.getId(),
                    "message", "Appointment slot created"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{slotId}")
    public ResponseEntity<Map<String, Object>> updateSlot(
            @PathVariable UUID slotId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(required = false) Integer maxAppointments,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) Boolean available) {
        try {
            AppointmentSlot slot = adminAppointmentSlotService.updateSlot(
                    slotId, startTime, endTime, maxAppointments, notes, available);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "slotId", slot.getId(),
                    "message", "Appointment slot updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<Map<String, Object>> deleteSlot(@PathVariable UUID slotId) {
        try {
            adminAppointmentSlotService.deleteSlot(slotId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointment slot deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> createBulkSlots(
            @RequestParam UUID storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(required = false) Integer maxAppointments,
            @RequestParam(required = false) String notes) {
        try {
            List<AppointmentSlot> slots = adminAppointmentSlotService.createBulkSlots(
                    storeId, startDate, endDate, startTime, endTime, maxAppointments, notes);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", slots.size(),
                    "message", "Bulk appointment slots created"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{slotId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleSlotAvailability(
            @PathVariable UUID slotId,
            @RequestParam boolean available) {
        try {
            AppointmentSlot slot = adminAppointmentSlotService.toggleSlotAvailability(slotId, available);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "slotId", slot.getId(),
                    "available", slot.isAvailable(),
                    "message", "Slot availability updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/{storeId}/summary")
    public ResponseEntity<Map<String, Object>> getSlotSummary(
            @PathVariable UUID storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(adminAppointmentSlotService.getSlotSummary(storeId, date));
    }
}

