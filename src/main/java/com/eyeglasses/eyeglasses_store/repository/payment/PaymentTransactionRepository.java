package com.eyeglasses.eyeglasses_store.repository.payment;

import com.eyeglasses.eyeglasses_store.entity.payment.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    Optional<PaymentTransaction> findByTransactionId(String transactionId);

    List<PaymentTransaction> findByOrderId(UUID orderId);

    Optional<PaymentTransaction> findByOrderIdAndStatus(UUID orderId, String status);
}
