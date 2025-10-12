package com.eyeglasses.eyeglasses_store.repository.order;

import com.eyeglasses.eyeglasses_store.entity.order.ReturnRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, UUID> {

    Page<ReturnRequest> findByStatus(String status, Pageable pageable);

    List<ReturnRequest> findByOrderId(UUID orderId);

    Optional<ReturnRequest> findByRmaNumber(String rmaNumber);

    @Query("SELECT COUNT(r) FROM ReturnRequest r WHERE r.status = :status")
    long countByStatus(@Param("status") String status);

    @Query("SELECT r FROM ReturnRequest r WHERE r.order.id = :orderId AND r.orderItem.id = :orderItemId")
    List<ReturnRequest> findByOrderAndItem(@Param("orderId") UUID orderId, @Param("orderItemId") UUID orderItemId);
}

