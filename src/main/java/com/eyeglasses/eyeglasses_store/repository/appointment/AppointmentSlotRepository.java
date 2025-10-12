package com.eyeglasses.eyeglasses_store.repository.appointment;

import com.eyeglasses.eyeglasses_store.entity.appointment.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, UUID> {

    List<AppointmentSlot> findByStoreIdAndDateAndAvailableTrue(UUID storeId, LocalDate date);

    List<AppointmentSlot> findByStoreIdAndDateBetween(UUID storeId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT s FROM AppointmentSlot s WHERE s.store.id = :storeId AND s.date = :date AND s.startTime >= :startTime AND s.endTime <= :endTime AND s.available = true")
    List<AppointmentSlot> findAvailableSlotsInTimeRange(@Param("storeId") UUID storeId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    @Query("SELECT s FROM AppointmentSlot s WHERE s.store.id = :storeId AND s.date >= :startDate AND s.date <= :endDate ORDER BY s.date, s.startTime")
    List<AppointmentSlot> findByStoreAndDateRange(@Param("storeId") UUID storeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(s) FROM AppointmentSlot s WHERE s.store.id = :storeId AND s.date = :date")
    long countByStoreAndDate(@Param("storeId") UUID storeId, @Param("date") LocalDate date);
}

