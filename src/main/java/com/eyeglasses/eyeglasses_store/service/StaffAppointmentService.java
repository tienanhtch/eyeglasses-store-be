package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.appointment.Appointment;
import com.eyeglasses.eyeglasses_store.entity.appointment.AppointmentSlot;
import com.eyeglasses.eyeglasses_store.entity.lens.Prescription;
import com.eyeglasses.eyeglasses_store.entity.store.Store;
import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.repository.appointment.AppointmentRepository;
import com.eyeglasses.eyeglasses_store.repository.appointment.AppointmentSlotRepository;
import com.eyeglasses.eyeglasses_store.repository.lens.PrescriptionRepository;
import com.eyeglasses.eyeglasses_store.repository.store.StoreRepository;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class StaffAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSlotRepository slotRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final StoreRepository storeRepository;
    private final AppUserRepository userRepository;

    public StaffAppointmentService(AppointmentRepository appointmentRepository,
            AppointmentSlotRepository slotRepository,
            PrescriptionRepository prescriptionRepository,
            StoreRepository storeRepository,
            AppUserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.slotRepository = slotRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    public List<Map<String, Object>> getStaffAppointments(UUID storeId, LocalDate date, String status) {
        List<Appointment> appointments = appointmentRepository.findAll().stream()
                .filter(appointment -> {
                    if (storeId != null && !appointment.getStore().getId().equals(storeId)) {
                        return false;
                    }
                    if (date != null && !appointment.getStartTime().toLocalDate().equals(date)) {
                        return false;
                    }
                    if (status != null && !appointment.getStatus().equals(status)) {
                        return false;
                    }
                    return true;
                })
                .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .toList();

        return appointments.stream().map(appointment -> {
            Map<String, Object> appointmentData = new HashMap<>();
            appointmentData.put("id", appointment.getId());
            appointmentData.put("userName", "Customer " + appointment.getId().toString().substring(0, 8)); // Mock
            appointmentData.put("userPhone", "0123456789"); // Mock
            appointmentData.put("startTime", appointment.getStartTime());
            appointmentData.put("endTime", appointment.getEndTime());
            appointmentData.put("status", appointment.getStatus());
            appointmentData.put("note", appointment.getNote());
            appointmentData.put("storeName", appointment.getStore().getName());
            return appointmentData;
        }).toList();
    }

    public Map<String, Object> getAppointmentDetails(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();

        Map<String, Object> details = new HashMap<>();
        details.put("id", appointment.getId());
        details.put("startTime", appointment.getStartTime());
        details.put("endTime", appointment.getEndTime());
        details.put("status", appointment.getStatus());
        details.put("note", appointment.getNote());
        details.put("store", Map.of(
                "id", appointment.getStore().getId(),
                "name", appointment.getStore().getName(),
                "address", appointment.getStore().getAddress()));

        // Mock customer info
        details.put("customer", Map.of(
                "name", "Customer " + appointment.getId().toString().substring(0, 8),
                "phone", "0123456789",
                "email", "customer@example.com"));

        return details;
    }

    @Transactional
    public Map<String, Object> checkInAppointment(UUID appointmentId, UUID staffId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();

        if (!"BOOKED".equals(appointment.getStatus())) {
            return Map.of(
                    "success", false,
                    "error", "Appointment is not in BOOKED status");
        }

        appointment.setStatus("IN_PROGRESS");
        appointmentRepository.save(appointment);

        return Map.of(
                "success", true,
                "appointmentId", appointmentId,
                "status", "IN_PROGRESS",
                "message", "Appointment checked in successfully");
    }

    @Transactional
    public Map<String, Object> completeAppointment(UUID appointmentId, Map<String, Object> examResults) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();

        if (!"IN_PROGRESS".equals(appointment.getStatus())) {
            return Map.of(
                    "success", false,
                    "error", "Appointment is not in progress");
        }

        // Create prescription from exam results
        Prescription prescription = new Prescription();
        prescription.setSphereRight(new BigDecimal(examResults.get("sphereRight").toString()));
        prescription.setCylinderRight(new BigDecimal(examResults.get("cylinderRight").toString()));
        prescription.setAxisRight(Integer.parseInt(examResults.get("axisRight").toString()));
        prescription.setSphereLeft(new BigDecimal(examResults.get("sphereLeft").toString()));
        prescription.setCylinderLeft(new BigDecimal(examResults.get("cylinderLeft").toString()));
        prescription.setAxisLeft(Integer.parseInt(examResults.get("axisLeft").toString()));
        prescription.setPd(new BigDecimal(examResults.get("pd").toString()));
        prescription.setNote((String) examResults.get("note"));
        prescription.setSource("EYE_EXAM");
        prescription.setIssuedAt(LocalDate.now());

        prescription = prescriptionRepository.save(prescription);

        // Update appointment status
        appointment.setStatus("COMPLETED");
        appointmentRepository.save(appointment);

        return Map.of(
                "success", true,
                "appointmentId", appointmentId,
                "prescriptionId", prescription.getId(),
                "status", "COMPLETED",
                "message", "Appointment completed and prescription saved");
    }

    @Transactional
    public Map<String, Object> cancelAppointment(UUID appointmentId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();

        if ("COMPLETED".equals(appointment.getStatus())) {
            return Map.of(
                    "success", false,
                    "error", "Cannot cancel completed appointment");
        }

        appointment.setStatus("CANCELLED");
        appointment.setNote(appointment.getNote() + " [Cancelled: " + reason + "]");
        appointmentRepository.save(appointment);

        return Map.of(
                "success", true,
                "appointmentId", appointmentId,
                "status", "CANCELLED",
                "message", "Appointment cancelled successfully");
    }

    public List<Map<String, Object>> getAvailableSlots(UUID storeId, LocalDate date) {
        List<AppointmentSlot> slots = slotRepository.findAll().stream()
                .filter(slot -> slot.getStore().getId().equals(storeId))
                .filter(slot -> slot.getDate().equals(date))
                .filter(slot -> slot.isAvailable())
                .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .toList();

        return slots.stream().map(slot -> {
            Map<String, Object> slotData = new HashMap<>();
            slotData.put("id", slot.getId());
            slotData.put("startTime", slot.getStartTime());
            slotData.put("endTime", slot.getEndTime());
            slotData.put("available", slot.isAvailable());
            slotData.put("maxBookings", 1); // Mock value
            return slotData;
        }).toList();
    }

    public Map<String, Object> getStaffDashboard(UUID storeId, LocalDate date) {
        List<Appointment> todayAppointments = appointmentRepository.findAll().stream()
                .filter(appointment -> appointment.getStore().getId().equals(storeId))
                .filter(appointment -> appointment.getStartTime().toLocalDate().equals(date))
                .toList();

        Map<String, Long> statusCounts = todayAppointments.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Appointment::getStatus,
                        java.util.stream.Collectors.counting()));

        return Map.of(
                "date", date,
                "storeId", storeId,
                "totalAppointments", todayAppointments.size(),
                "statusCounts", statusCounts,
                "appointments", todayAppointments.stream().map(appointment -> {
                    Map<String, Object> appointmentData = new HashMap<>();
                    appointmentData.put("id", appointment.getId());
                    appointmentData.put("startTime", appointment.getStartTime());
                    appointmentData.put("endTime", appointment.getEndTime());
                    appointmentData.put("status", appointment.getStatus());
                    appointmentData.put("customerName", "Customer " + appointment.getId().toString().substring(0, 8));
                    return appointmentData;
                }).toList());
    }

    @Transactional
    public Map<String, Object> rescheduleAppointment(UUID appointmentId, OffsetDateTime newStartTime,
            OffsetDateTime newEndTime) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();

        if ("COMPLETED".equals(appointment.getStatus()) || "CANCELLED".equals(appointment.getStatus())) {
            return Map.of(
                    "success", false,
                    "error", "Cannot reschedule completed or cancelled appointment");
        }

        appointment.setStartTime(newStartTime);
        appointment.setEndTime(newEndTime);
        appointmentRepository.save(appointment);

        return Map.of(
                "success", true,
                "appointmentId", appointmentId,
                "newStartTime", newStartTime,
                "newEndTime", newEndTime,
                "message", "Appointment rescheduled successfully");
    }
}
