package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.service.ServiceTicket;
import com.eyeglasses.eyeglasses_store.entity.store.Store;
import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.repository.service.ServiceTicketRepository;
import com.eyeglasses.eyeglasses_store.repository.store.StoreRepository;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class StaffServiceTicketService {

    private final ServiceTicketRepository serviceTicketRepository;
    private final StoreRepository storeRepository;
    private final AppUserRepository userRepository;

    public StaffServiceTicketService(ServiceTicketRepository serviceTicketRepository,
            StoreRepository storeRepository,
            AppUserRepository userRepository) {
        this.serviceTicketRepository = serviceTicketRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    public List<Map<String, Object>> getStaffServiceTickets(UUID storeId, String status) {
        List<ServiceTicket> tickets = serviceTicketRepository.findAll().stream()
                .filter(ticket -> {
                    // if (storeId != null && !ticket.getStore().getId().equals(storeId)) { // TODO:
                    // Implement when ServiceTicket entity is properly set up
                    // return false;
                    // }
                    if (status != null && !ticket.getStatus().equals(status)) {
                        return false;
                    }
                    return true;
                })
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();

        return tickets.stream().map(ticket -> {
            Map<String, Object> ticketData = new HashMap<>();
            ticketData.put("id", ticket.getId());
            ticketData.put("ticketNumber", "TKT-" + ticket.getId().toString().substring(0, 8));
            ticketData.put("status", ticket.getStatus());
            ticketData.put("serviceType", ticket.getServiceType());
            ticketData.put("description", ticket.getDescription());
            ticketData.put("createdAt", ticket.getCreatedAt());
            ticketData.put("customerName", "Customer " + ticket.getId().toString().substring(0, 8)); // Mock
            ticketData.put("customerPhone", "0123456789"); // Mock
            ticketData.put("storeName", "Mock Store"); // Mock store name
            return ticketData;
        }).toList();
    }

    public Map<String, Object> getServiceTicketDetails(UUID ticketId) {
        ServiceTicket ticket = serviceTicketRepository.findById(ticketId).orElseThrow();

        Map<String, Object> details = new HashMap<>();
        details.put("id", ticket.getId());
        details.put("ticketNumber", "TKT-" + ticket.getId().toString().substring(0, 8));
        details.put("status", ticket.getStatus());
        details.put("serviceType", ticket.getServiceType());
        details.put("description", ticket.getDescription());
        details.put("createdAt", ticket.getCreatedAt());
        details.put("estimatedCompletion", ticket.getEstimatedCompletion());

        // Mock customer info
        details.put("customer", Map.of(
                "name", "Customer " + ticket.getId().toString().substring(0, 8),
                "phone", "0123456789",
                "email", "customer@example.com"));

        details.put("store", Map.of(
                "id", ticket.getId(), // Mock store ID
                "name", "Mock Store",
                "address", "Mock Address"));

        return details;
    }

    @Transactional
    public Map<String, Object> receiveServiceTicket(UUID ticketId, UUID staffId, String conditionNotes,
            List<String> imageUrls) {
        ServiceTicket ticket = serviceTicketRepository.findById(ticketId).orElseThrow();

        if (!"REQUESTED".equals(ticket.getStatus())) {
            return Map.of(
                    "success", false,
                    "error", "Ticket is not in REQUESTED status");
        }

        ticket.setStatus("RECEIVED");
        ticket.setDescription(ticket.getDescription() + "\n[Received by staff: " + staffId + "]");
        if (conditionNotes != null) {
            ticket.setDescription(ticket.getDescription() + "\n[Condition: " + conditionNotes + "]");
        }
        serviceTicketRepository.save(ticket);

        return Map.of(
                "success", true,
                "ticketId", ticketId,
                "status", "RECEIVED",
                "message", "Service ticket received successfully",
                "conditionNotes", conditionNotes,
                "imageUrls", imageUrls);
    }

    @Transactional
    public Map<String, Object> startProcessing(UUID ticketId, UUID staffId, String processNotes) {
        ServiceTicket ticket = serviceTicketRepository.findById(ticketId).orElseThrow();

        if (!"RECEIVED".equals(ticket.getStatus())) {
            return Map.of(
                    "success", false,
                    "error", "Ticket is not in RECEIVED status");
        }

        ticket.setStatus("PROCESSING");
        ticket.setDescription(ticket.getDescription() + "\n[Processing started by: " + staffId + "]");
        if (processNotes != null) {
            ticket.setDescription(ticket.getDescription() + "\n[Process notes: " + processNotes + "]");
        }
        serviceTicketRepository.save(ticket);

        return Map.of(
                "success", true,
                "ticketId", ticketId,
                "status", "PROCESSING",
                "message", "Service processing started");
    }

    @Transactional
    public Map<String, Object> estimateService(UUID ticketId, BigDecimal estimatedCost, String estimatedTime,
            String materials) {
        ServiceTicket ticket = serviceTicketRepository.findById(ticketId).orElseThrow();

        if (!"PROCESSING".equals(ticket.getStatus())) {
            return Map.of(
                    "success", false,
                    "error", "Ticket is not in PROCESSING status");
        }

        ticket.setStatus("ESTIMATE");
        ticket.setEstimatedCost(estimatedCost);
        ticket.setEstimatedCompletion(OffsetDateTime.now().plusDays(7)); // Mock 7 days
        ticket.setDescription(ticket.getDescription() + "\n[Estimate: " + estimatedCost + " VND, Time: " + estimatedTime
                + ", Materials: " + materials + "]");
        serviceTicketRepository.save(ticket);

        return Map.of(
                "success", true,
                "ticketId", ticketId,
                "status", "ESTIMATE",
                "estimatedCost", estimatedCost,
                "estimatedTime", estimatedTime,
                "materials", materials,
                "message", "Service estimate provided");
    }

    @Transactional
    public Map<String, Object> completeService(UUID ticketId, UUID staffId, String completionNotes,
            BigDecimal finalCost) {
        ServiceTicket ticket = serviceTicketRepository.findById(ticketId).orElseThrow();

        if (!"ESTIMATE".equals(ticket.getStatus()) && !"PROCESSING".equals(ticket.getStatus())) {
            return Map.of(
                    "success", false,
                    "error", "Ticket is not in correct status for completion");
        }

        ticket.setStatus("DONE");
        // ticket.setFinalCost(finalCost); // TODO: Implement when ServiceTicket entity
        // is properly set up
        ticket.setDescription(ticket.getDescription() + "\n[Completed by: " + staffId + "]");
        if (completionNotes != null) {
            ticket.setDescription(ticket.getDescription() + "\n[Completion notes: " + completionNotes + "]");
        }
        serviceTicketRepository.save(ticket);

        return Map.of(
                "success", true,
                "ticketId", ticketId,
                "status", "DONE",
                "finalCost", finalCost,
                "message", "Service completed successfully");
    }

    @Transactional
    public Map<String, Object> createWalkInTicket(UUID storeId, String customerName, String customerPhone,
            String serviceType, String description, String conditionNotes) {
        ServiceTicket ticket = new ServiceTicket();
        ticket.setServiceType(serviceType);
        ticket.setDescription(description);
        ticket.setStatus("RECEIVED");
        // ticket.setStore(storeRepository.findById(storeId).orElseThrow()); // TODO:
        // Implement when ServiceTicket entity is properly set up
        // ticket.setCreatedAt(OffsetDateTime.now()); // TODO: Implement when
        // ServiceTicket entity is properly set up

        if (conditionNotes != null) {
            ticket.setDescription(ticket.getDescription() + "\n[Walk-in condition: " + conditionNotes + "]");
        }

        ticket = serviceTicketRepository.save(ticket);

        return Map.of(
                "success", true,
                "ticketId", ticket.getId(),
                "ticketNumber", "TKT-" + ticket.getId().toString().substring(0, 8),
                "status", "RECEIVED",
                "customerName", customerName,
                "customerPhone", customerPhone,
                "message", "Walk-in service ticket created successfully");
    }

    public Map<String, Object> getStaffServiceDashboard(UUID storeId) {
        List<ServiceTicket> allTickets = serviceTicketRepository.findAll().stream()
                // .filter(ticket -> ticket.getStore().getId().equals(storeId)) // TODO:
                // Implement when ServiceTicket entity is properly set up
                .toList();

        Map<String, Long> statusCounts = allTickets.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        ServiceTicket::getStatus,
                        java.util.stream.Collectors.counting()));

        List<ServiceTicket> recentTickets = allTickets.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .toList();

        return Map.of(
                "storeId", storeId,
                "totalTickets", allTickets.size(),
                "statusCounts", statusCounts,
                "recentTickets", recentTickets.stream().map(ticket -> {
                    Map<String, Object> ticketData = new HashMap<>();
                    ticketData.put("id", ticket.getId());
                    ticketData.put("ticketNumber", "TKT-" + ticket.getId().toString().substring(0, 8));
                    ticketData.put("status", ticket.getStatus());
                    ticketData.put("serviceType", ticket.getServiceType());
                    ticketData.put("createdAt", ticket.getCreatedAt());
                    return ticketData;
                }).toList());
    }

    @Transactional
    public Map<String, Object> updateTicketStatus(UUID ticketId, String newStatus, String notes) {
        ServiceTicket ticket = serviceTicketRepository.findById(ticketId).orElseThrow();

        String oldStatus = ticket.getStatus();
        ticket.setStatus(newStatus);

        if (notes != null) {
            ticket.setDescription(ticket.getDescription() + "\n[Status changed from " + oldStatus + " to " + newStatus
                    + ": " + notes + "]");
        }

        serviceTicketRepository.save(ticket);

        return Map.of(
                "success", true,
                "ticketId", ticketId,
                "oldStatus", oldStatus,
                "newStatus", newStatus,
                "message", "Ticket status updated successfully");
    }
}
