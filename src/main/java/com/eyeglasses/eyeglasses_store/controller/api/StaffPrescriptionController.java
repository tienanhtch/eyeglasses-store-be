package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.StaffPrescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.PUBLIC_BASE + "/staff/prescriptions")
public class StaffPrescriptionController {

    private final StaffPrescriptionService staffPrescriptionService;

    public StaffPrescriptionController(StaffPrescriptionService staffPrescriptionService) {
        this.staffPrescriptionService = staffPrescriptionService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getPrescriptionsByUser(@PathVariable UUID userId) {
        try {
            List<Map<String, Object>> prescriptions = staffPrescriptionService.getPrescriptionsByUser(userId);
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of(Map.of(
                    "error", e.getMessage())));
        }
    }

    @GetMapping("/{prescriptionId}")
    public ResponseEntity<Map<String, Object>> getPrescriptionDetails(@PathVariable UUID prescriptionId) {
        try {
            Map<String, Object> details = staffPrescriptionService.getPrescriptionDetails(prescriptionId);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPrescription(
            @RequestParam UUID userId,
            @RequestBody Map<String, Object> prescriptionData) {
        try {
            Map<String, Object> result = staffPrescriptionService.createPrescription(userId, prescriptionData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{prescriptionId}")
    public ResponseEntity<Map<String, Object>> updatePrescription(
            @PathVariable UUID prescriptionId,
            @RequestBody Map<String, Object> prescriptionData) {
        try {
            Map<String, Object> result = staffPrescriptionService.updatePrescription(prescriptionId, prescriptionData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{prescriptionId}")
    public ResponseEntity<Map<String, Object>> deletePrescription(@PathVariable UUID prescriptionId) {
        try {
            Map<String, Object> result = staffPrescriptionService.deletePrescription(prescriptionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePrescription(@RequestBody Map<String, Object> prescriptionData) {
        try {
            Map<String, Object> validation = staffPrescriptionService.validatePrescription(prescriptionData);
            return ResponseEntity.ok(validation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/{prescriptionId}/report")
    public ResponseEntity<Map<String, Object>> generatePrescriptionReport(@PathVariable UUID prescriptionId) {
        try {
            Map<String, Object> report = staffPrescriptionService.generatePrescriptionReport(prescriptionId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchPrescriptions(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) {
        try {
            List<Map<String, Object>> prescriptions = staffPrescriptionService.searchPrescriptions(query, fromDate,
                    toDate);
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of(Map.of(
                    "error", e.getMessage())));
        }
    }
}
