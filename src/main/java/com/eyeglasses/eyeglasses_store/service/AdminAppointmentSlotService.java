package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.appointment.AppointmentSlot;
import com.eyeglasses.eyeglasses_store.entity.store.Store;
import com.eyeglasses.eyeglasses_store.repository.appointment.AppointmentSlotRepository;
import com.eyeglasses.eyeglasses_store.repository.store.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminAppointmentSlotService {

    private final AppointmentSlotRepository appointmentSlotRepository;
    private final StoreRepository storeRepository;

    public AdminAppointmentSlotService(AppointmentSlotRepository appointmentSlotRepository,
            StoreRepository storeRepository) {
        this.appointmentSlotRepository = appointmentSlotRepository;
        this.storeRepository = storeRepository;
    }

    public List<AppointmentSlot> getAvailableSlots(UUID storeId, LocalDate date) {
        return appointmentSlotRepository.findByStoreIdAndDateAndAvailableTrue(storeId, date);
    }

    public List<AppointmentSlot> getSlotsByDateRange(UUID storeId, LocalDate startDate, LocalDate endDate) {
        return appointmentSlotRepository.findByStoreAndDateRange(storeId, startDate, endDate);
    }

    @Transactional
    public AppointmentSlot createSlot(UUID storeId, LocalDate date, LocalTime startTime,
            LocalTime endTime, Integer maxAppointments, String notes) {
        Store store = storeRepository.findById(storeId).orElseThrow();

        AppointmentSlot slot = new AppointmentSlot();
        slot.setStore(store);
        slot.setDate(date);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setMaxAppointments(maxAppointments != null ? maxAppointments : 1);
        slot.setNotes(notes);
        slot.setAvailable(true);

        return appointmentSlotRepository.save(slot);
    }

    @Transactional
    public AppointmentSlot updateSlot(UUID slotId, LocalTime startTime, LocalTime endTime,
            Integer maxAppointments, String notes, Boolean available) {
        AppointmentSlot slot = appointmentSlotRepository.findById(slotId).orElseThrow();

        if (startTime != null)
            slot.setStartTime(startTime);
        if (endTime != null)
            slot.setEndTime(endTime);
        if (maxAppointments != null)
            slot.setMaxAppointments(maxAppointments);
        if (notes != null)
            slot.setNotes(notes);
        if (available != null)
            slot.setAvailable(available);

        return appointmentSlotRepository.save(slot);
    }

    @Transactional
    public void deleteSlot(UUID slotId) {
        AppointmentSlot slot = appointmentSlotRepository.findById(slotId).orElseThrow();
        appointmentSlotRepository.delete(slot);
    }

    @Transactional
    public List<AppointmentSlot> createBulkSlots(UUID storeId, LocalDate startDate, LocalDate endDate,
            LocalTime startTime, LocalTime endTime,
            Integer maxAppointments, String notes) {
        Store store = storeRepository.findById(storeId).orElseThrow();

        List<AppointmentSlot> slots = List.of();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            AppointmentSlot slot = new AppointmentSlot();
            slot.setStore(store);
            slot.setDate(currentDate);
            slot.setStartTime(startTime);
            slot.setEndTime(endTime);
            slot.setMaxAppointments(maxAppointments != null ? maxAppointments : 1);
            slot.setNotes(notes);
            slot.setAvailable(true);

            appointmentSlotRepository.save(slot);
            slots = List.of(slot); // TODO: Fix this to properly collect slots

            currentDate = currentDate.plusDays(1);
        }

        return slots;
    }

    @Transactional
    public AppointmentSlot toggleSlotAvailability(UUID slotId, boolean available) {
        AppointmentSlot slot = appointmentSlotRepository.findById(slotId).orElseThrow();
        slot.setAvailable(available);
        return appointmentSlotRepository.save(slot);
    }

    public Map<String, Object> getSlotSummary(UUID storeId, LocalDate date) {
        List<AppointmentSlot> slots = appointmentSlotRepository.findByStoreIdAndDateAndAvailableTrue(storeId, date);
        long totalSlots = slots.size();
        long availableSlots = slots.stream().mapToLong(s -> s.isAvailable() ? 1 : 0).sum();
        long bookedSlots = totalSlots - availableSlots;

        return Map.of(
                "storeId", storeId,
                "date", date,
                "totalSlots", totalSlots,
                "availableSlots", availableSlots,
                "bookedSlots", bookedSlots);
    }
}

