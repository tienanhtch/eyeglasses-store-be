package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.appointment.Appointment;
import com.eyeglasses.eyeglasses_store.entity.service.ServiceTicket;
import com.eyeglasses.eyeglasses_store.service.NotificationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.PUBLIC_BASE + "/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/appointment/confirm")
    public ResponseEntity<Map<String, Object>> sendAppointmentConfirmation(@RequestBody Appointment appointment) {
        Map<String, Object> notification = notificationService.sendAppointmentConfirmation(appointment);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/appointment/reminder")
    public ResponseEntity<Map<String, Object>> sendAppointmentReminder(@RequestBody Appointment appointment) {
        Map<String, Object> notification = notificationService.sendAppointmentReminder(appointment);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/service-ticket/update")
    public ResponseEntity<Map<String, Object>> sendServiceTicketUpdate(@RequestBody ServiceTicket ticket) {
        Map<String, Object> notification = notificationService.sendServiceTicketUpdate(ticket);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/order/confirm")
    public ResponseEntity<Map<String, Object>> sendOrderConfirmation(
            @RequestParam UUID userId,
            @RequestParam String orderNumber,
            @RequestParam BigDecimal totalAmount) {
        Map<String, Object> notification = notificationService.sendOrderConfirmation(userId, orderNumber, totalAmount);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/order/status")
    public ResponseEntity<Map<String, Object>> sendOrderStatusUpdate(
            @RequestParam UUID userId,
            @RequestParam String orderNumber,
            @RequestParam String status) {
        Map<String, Object> notification = notificationService.sendOrderStatusUpdate(userId, orderNumber, status);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/ical/{appointmentId}")
    public ResponseEntity<String> downloadICalEvent(@PathVariable UUID appointmentId) {
        // This would typically fetch the appointment and generate iCal
        // For now, return a mock response
        String icalContent = "BEGIN:VCALENDAR\nVERSION:2.0\nEND:VCALENDAR";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/calendar"));
        headers.setContentDispositionFormData("attachment", "appointment.ics");

        return ResponseEntity.ok()
                .headers(headers)
                .body(icalContent);
    }
}
