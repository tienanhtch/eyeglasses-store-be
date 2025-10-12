package com.eyeglasses.eyeglasses_store.repository.appointment;

import com.eyeglasses.eyeglasses_store.entity.appointment.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByUserIdOrderByStartTimeAsc(UUID userId);

    List<Appointment> findByStoreIdAndStartTimeBetweenOrderByStartTimeAsc(UUID storeId, OffsetDateTime start,
            OffsetDateTime end);
}
