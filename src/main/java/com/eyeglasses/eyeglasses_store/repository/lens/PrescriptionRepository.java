package com.eyeglasses.eyeglasses_store.repository.lens;

import com.eyeglasses.eyeglasses_store.entity.lens.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
}
