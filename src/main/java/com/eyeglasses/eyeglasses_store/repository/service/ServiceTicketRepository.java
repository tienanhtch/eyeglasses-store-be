package com.eyeglasses.eyeglasses_store.repository.service;

import com.eyeglasses.eyeglasses_store.entity.service.ServiceTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceTicketRepository extends JpaRepository<ServiceTicket, UUID> {
    List<ServiceTicket> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
