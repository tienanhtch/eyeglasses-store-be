package com.eyeglasses.eyeglasses_store.repository.order;

import com.eyeglasses.eyeglasses_store.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Order> findByOrderNo(String orderNo);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(o.grandTotal), 0) FROM Order o WHERE o.status = 'COMPLETED'")
    Double sumRevenue();
}
