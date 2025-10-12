package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.StaffAppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.PUBLIC_BASE + "/staff/appointments")
public class StaffAppointmentController {

    private final StaffAppointmentService staffAppointmentService;

    public StaffAppointmentController(StaffAppointmentService staffAppointmentService) {
        this.staffAppointmentService = staffAppointmentService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getStaffAppointments(
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String status) {
        try {
            List<Map<String, Object>> appointments = staffAppointmentService.getStaffAppointments(storeId, date,
                    status);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of(Map.of(
                    "error", e.getMessage())));
        }
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<Map<String, Object>> getAppointmentDetails(@PathVariable UUID appointmentId) {
        try {
            Map<String, Object> details = staffAppointmentService.getAppointmentDetails(appointmentId);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{appointmentId}/checkin")
    public ResponseEntity<Map<String, Object>> checkInAppointment(
            @PathVariable UUID appointmentId,
            @RequestParam UUID staffId) {
        try {
            Map<String, Object> result = staffAppointmentService.checkInAppointment(appointmentId, staffId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{appointmentId}/complete")
    public ResponseEntity<Map<String, Object>> completeAppointment(
            @PathVariable UUID appointmentId,
            @RequestBody Map<String, Object> examResults) {
        try {
            Map<String, Object> result = staffAppointmentService.completeAppointment(appointmentId, examResults);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{appointmentId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelAppointment(
            @PathVariable UUID appointmentId,
            @RequestParam String reason) {
        try {
            Map<String, Object> result = staffAppointmentService.cancelAppointment(appointmentId, reason);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/slots")
    public ResponseEntity<List<Map<String, Object>>> getAvailableSlots(
            @RequestParam UUID storeId,
            @RequestParam LocalDate date) {
        try {
            List<Map<String, Object>> slots = staffAppointmentService.getAvailableSlots(storeId, date);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of(Map.of(
                    "error", e.getMessage())));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getStaffDashboard(
            @RequestParam UUID storeId,
            @RequestParam LocalDate date) {
        try {
            Map<String, Object> dashboard = staffAppointmentService.getStaffDashboard(storeId, date);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{appointmentId}/reschedule")
    public ResponseEntity<Map<String, Object>> rescheduleAppointment(
            @PathVariable UUID appointmentId,
            @RequestParam OffsetDateTime newStartTime,
            @RequestParam OffsetDateTime newEndTime) {
        try {
            Map<String, Object> result = staffAppointmentService.rescheduleAppointment(appointmentId, newStartTime,
                    newEndTime);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }
}
