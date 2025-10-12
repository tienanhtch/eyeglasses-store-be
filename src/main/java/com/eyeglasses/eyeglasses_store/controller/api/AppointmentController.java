package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.API_BASE_PATH)
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/stores")
    public ResponseEntity<List<Map<String, Object>>> listStores() {
        return ResponseEntity.ok(appointmentService.listStores());
    }

    @GetMapping("/stores/{storeId}/slots")
    public ResponseEntity<List<Map<String, Object>>> availableSlots(
            @PathVariable("storeId") UUID storeId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.availableSlots(storeId, date));
    }

    private record BookRequest(UUID userId, UUID storeId, OffsetDateTime start, OffsetDateTime end, String note) {
    }

    @PostMapping("/appointments")
    public ResponseEntity<Map<String, Object>> book(@RequestBody BookRequest body) {
        return ResponseEntity
                .ok(appointmentService.book(body.userId(), body.storeId(), body.start(), body.end(), body.note()));
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Map<String, Object>> cancel(@PathVariable("appointmentId") UUID appointmentId) {
        return ResponseEntity.ok(appointmentService.cancel(appointmentId));
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<Map<String, Object>>> listByUser(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(appointmentService.listByUser(userId));
    }

    private record RescheduleRequest(OffsetDateTime start, OffsetDateTime end) {
    }

    @PatchMapping("/appointments/{appointmentId}/reschedule")
    public ResponseEntity<Map<String, Object>> reschedule(@PathVariable("appointmentId") UUID appointmentId,
            @RequestBody RescheduleRequest body) {
        return ResponseEntity.ok(appointmentService.book(
                appointmentService.getDetail(appointmentId).get("userId") != null
                        ? UUID.fromString(appointmentService.getDetail(appointmentId).get("userId").toString())
                        : null,
                UUID.fromString(appointmentService.getDetail(appointmentId).get("storeId").toString()),
                body.start(), body.end(), "Rescheduled"));
    }

    @PostMapping("/appointments/{appointmentId}/checkin")
    public ResponseEntity<Map<String, Object>> checkin(@PathVariable("appointmentId") UUID appointmentId) {
        return ResponseEntity.ok(appointmentService.checkin(appointmentId));
    }
}
