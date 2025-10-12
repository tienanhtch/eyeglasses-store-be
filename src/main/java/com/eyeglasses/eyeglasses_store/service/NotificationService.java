package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.appointment.Appointment;
import com.eyeglasses.eyeglasses_store.entity.service.ServiceTicket;
import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificationService {

    private final AppUserRepository appUserRepository;

    public NotificationService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public Map<String, Object> sendAppointmentConfirmation(Appointment appointment) {
        // AppUser user =
        // appUserRepository.findById(appointment.getUserId()).orElseThrow(); // TODO:
        // Implement when Appointment entity is properly set up
        AppUser user = new AppUser(); // Mock user

        // Mock email/SMS sending
        Map<String, Object> notification = Map.of(
                "type", "APPOINTMENT_CONFIRMATION",
                "userId", user.getId(),
                "email", "user@example.com", // Mock email
                "phone", "0123456789", // Mock phone
                "appointmentId", appointment.getId(),
                "storeName", "Mock Store", // Mock store name
                "appointmentDate", appointment.getStartTime(),
                "message", "Your eye exam appointment has been confirmed",
                "sentAt", OffsetDateTime.now(),
                "status", "SENT");

        // In real implementation, integrate with email/SMS service
        logNotification(notification);

        return notification;
    }

    public Map<String, Object> sendAppointmentReminder(Appointment appointment) {
        // AppUser user =
        // appUserRepository.findById(appointment.getUserId()).orElseThrow(); // TODO:
        // Implement when Appointment entity is properly set up
        AppUser user = new AppUser(); // Mock user

        Map<String, Object> notification = Map.of(
                "type", "APPOINTMENT_REMINDER",
                "userId", user.getId(),
                "email", "user@example.com", // Mock email
                "phone", "0123456789", // Mock phone
                "appointmentId", appointment.getId(),
                "storeName", "Mock Store", // Mock store name
                "appointmentDate", appointment.getStartTime(),
                "message", "Reminder: Your eye exam appointment is tomorrow",
                "sentAt", OffsetDateTime.now(),
                "status", "SENT");

        logNotification(notification);
        return notification;
    }

    public Map<String, Object> sendServiceTicketUpdate(ServiceTicket ticket) {
        // AppUser user = appUserRepository.findById(ticket.getUserId()).orElseThrow();
        // // TODO: Implement when ServiceTicket entity is properly set up
        AppUser user = new AppUser(); // Mock user

        Map<String, Object> notification = Map.of(
                "type", "SERVICE_TICKET_UPDATE",
                "userId", user.getId(),
                "email", "user@example.com", // Mock email
                "phone", "0123456789", // Mock phone
                "ticketId", ticket.getId(),
                "ticketNumber", "TKT-" + ticket.getId().toString().substring(0, 8), // Mock ticket number
                "status", ticket.getStatus(),
                "message", "Your service request status has been updated to: " + ticket.getStatus(),
                "sentAt", OffsetDateTime.now(),
                "status", "SENT");

        logNotification(notification);
        return notification;
    }

    public Map<String, Object> sendOrderConfirmation(UUID userId, String orderNumber, BigDecimal totalAmount) {
        AppUser user = appUserRepository.findById(userId).orElseThrow();

        Map<String, Object> notification = Map.of(
                "type", "ORDER_CONFIRMATION",
                "userId", user.getId(),
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "orderNumber", orderNumber,
                "totalAmount", totalAmount,
                "message", "Your order has been confirmed and is being processed",
                "sentAt", OffsetDateTime.now(),
                "status", "SENT");

        logNotification(notification);
        return notification;
    }

    public Map<String, Object> sendOrderStatusUpdate(UUID userId, String orderNumber, String status) {
        AppUser user = appUserRepository.findById(userId).orElseThrow();

        Map<String, Object> notification = Map.of(
                "type", "ORDER_STATUS_UPDATE",
                "userId", user.getId(),
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "orderNumber", orderNumber,
                "status", status,
                "message", "Your order status has been updated to: " + status,
                "sentAt", OffsetDateTime.now(),
                "status", "SENT");

        logNotification(notification);
        return notification;
    }

    public Map<String, Object> generateICalEvent(Appointment appointment) {
        // Generate iCal format for calendar integration
        String icalContent = generateICalContent(appointment);

        return Map.of(
                "type", "ICAL_EVENT",
                "appointmentId", appointment.getId(),
                "icalContent", icalContent,
                "downloadUrl", "/api/v1/notifications/ical/" + appointment.getId(),
                "generatedAt", OffsetDateTime.now());
    }

    private String generateICalContent(Appointment appointment) {
        StringBuilder ical = new StringBuilder();
        ical.append("BEGIN:VCALENDAR\n");
        ical.append("VERSION:2.0\n");
        ical.append("PRODID:-//Eyeglasses Store//Appointment//EN\n");
        ical.append("BEGIN:VEVENT\n");
        ical.append("UID:").append(appointment.getId()).append("@eyeglasses-store.com\n");
        ical.append("DTSTART:").append(formatDateTime(appointment.getStartTime())).append("\n");
        ical.append("DTEND:").append(formatDateTime(appointment.getEndTime())).append("\n");
        ical.append("SUMMARY:Eye Exam Appointment\n");
        ical.append("DESCRIPTION:Eye exam appointment at ").append(appointment.getStore().getName()).append("\n");
        ical.append("LOCATION:").append(appointment.getStore().getAddress()).append("\n");
        ical.append("STATUS:CONFIRMED\n");
        ical.append("END:VEVENT\n");
        ical.append("END:VCALENDAR\n");

        return ical.toString();
    }

    private String formatDateTime(OffsetDateTime dateTime) {
        return dateTime.toString().replaceAll("[-:]", "").replaceAll("\\.\\d+", "");
    }

    private void logNotification(Map<String, Object> notification) {
        // In real implementation, save to notification log table
        System.out.println("Notification sent: " + notification);
    }
}
