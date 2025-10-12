package com.eyeglasses.eyeglasses_store.repository.order;

import com.eyeglasses.eyeglasses_store.entity.order.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByTxRef(String txRef);
}
