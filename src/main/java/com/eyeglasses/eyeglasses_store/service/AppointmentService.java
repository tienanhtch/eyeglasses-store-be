package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.appointment.Appointment;
import com.eyeglasses.eyeglasses_store.entity.store.Store;
import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.repository.appointment.AppointmentRepository;
import com.eyeglasses.eyeglasses_store.repository.store.StoreRepository;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final StoreRepository storeRepository;
    private final AppUserRepository userRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
            StoreRepository storeRepository,
            AppUserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listStores() {
        return storeRepository.findByActiveTrueOrderByNameAsc().stream().map(s -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", s.getId());
            m.put("code", s.getCode());
            m.put("name", s.getName());
            m.put("address", s.getAddress());
            m.put("phone", s.getPhone());
            m.put("openHours", s.getOpenHours());
            return m;
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> availableSlots(UUID storeId, LocalDate date) {
        // Slot đơn giản: mỗi 30' từ 9:00-17:00 theo UTC+7
        ZoneOffset tz = ZoneOffset.ofHours(7);
        OffsetDateTime start = date.atTime(LocalTime.of(9, 0)).atOffset(tz);
        OffsetDateTime end = date.atTime(LocalTime.of(17, 0)).atOffset(tz);
        List<Appointment> existing = appointmentRepository.findByStoreIdAndStartTimeBetweenOrderByStartTimeAsc(storeId,
                start, end);
        Set<OffsetDateTime> taken = existing.stream().map(Appointment::getStartTime).collect(Collectors.toSet());
        List<Map<String, Object>> slots = new ArrayList<>();
        OffsetDateTime cur = start;
        while (!cur.isAfter(end.minusMinutes(30))) {
            boolean available = !taken.contains(cur);
            slots.add(Map.of(
                    "start", cur,
                    "end", cur.plusMinutes(30),
                    "available", available));
            cur = cur.plusMinutes(30);
        }
        return slots;
    }

    @Transactional
    public Map<String, Object> book(UUID userId, UUID storeId, OffsetDateTime start, OffsetDateTime end, String note) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        Store store = storeRepository.findById(storeId).orElseThrow();
        List<Appointment> overlaps = appointmentRepository.findByStoreIdAndStartTimeBetweenOrderByStartTimeAsc(storeId,
                start, end);
        if (!overlaps.isEmpty())
            throw new IllegalStateException("Slot is not available");
        Appointment a = new Appointment();
        a.setUser(user);
        a.setStore(store);
        a.setStartTime(start);
        a.setEndTime(end);
        a.setStatus("BOOKED");
        a.setNote(note);
        Appointment saved = appointmentRepository.save(a);
        return toPayload(saved);
    }

    @Transactional
    public Map<String, Object> cancel(UUID appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow();
        a.setStatus("CANCELLED");
        appointmentRepository.save(a);
        return toPayload(a);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listByUser(UUID userId) {
        return appointmentRepository.findByUserIdOrderByStartTimeAsc(userId).stream().map(this::toPayload).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDetail(UUID appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow();
        return toPayload(a);
    }

    @Transactional
    public Map<String, Object> checkin(UUID appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId).orElseThrow();
        a.setStatus("CHECKED_IN");
        appointmentRepository.save(a);
        return toPayload(a);
    }

    private Map<String, Object> toPayload(Appointment a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", a.getId());
        m.put("userId", a.getUser() != null ? a.getUser().getId() : null);
        m.put("storeId", a.getStore() != null ? a.getStore().getId() : null);
        m.put("startTime", a.getStartTime());
        m.put("endTime", a.getEndTime());
        m.put("status", a.getStatus());
        m.put("note", a.getNote());
        return m;
    }
}
