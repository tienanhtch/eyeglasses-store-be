package com.eyeglasses.eyeglasses_store.entity.appointment;

import com.eyeglasses.eyeglasses_store.entity.store.Store;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointment_slot", indexes = {
        @Index(name = "uk_appointment_slot_store_date_time", columnList = "store_id, date, start_time", unique = true)
})
public class AppointmentSlot {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "max_appointments", nullable = false)
    private Integer maxAppointments = 1;

    @Column(name = "current_appointments", nullable = false)
    private Integer currentAppointments = 0;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Constructors
    public AppointmentSlot() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMaxAppointments() {
        return maxAppointments;
    }

    public void setMaxAppointments(Integer maxAppointments) {
        this.maxAppointments = maxAppointments;
    }

    public Integer getCurrentAppointments() {
        return currentAppointments;
    }

    public void setCurrentAppointments(Integer currentAppointments) {
        this.currentAppointments = currentAppointments;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean hasCapacity() {
        return currentAppointments < maxAppointments;
    }

    public void incrementAppointments() {
        this.currentAppointments++;
        if (currentAppointments >= maxAppointments) {
            this.available = false;
        }
    }

    public void decrementAppointments() {
        this.currentAppointments = Math.max(0, currentAppointments - 1);
        if (currentAppointments < maxAppointments) {
            this.available = true;
        }
    }
}

