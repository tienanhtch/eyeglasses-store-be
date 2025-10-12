package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.order.ReturnRequest;
import com.eyeglasses.eyeglasses_store.entity.order.Order;
import com.eyeglasses.eyeglasses_store.entity.order.OrderItem;
import com.eyeglasses.eyeglasses_store.repository.order.ReturnRequestRepository;
import com.eyeglasses.eyeglasses_store.repository.order.OrderRepository;
import com.eyeglasses.eyeglasses_store.repository.order.OrderItemRepository;
import com.eyeglasses.eyeglasses_store.repository.inventory.InventoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminReturnService {

    private final ReturnRequestRepository returnRequestRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;

    public AdminReturnService(ReturnRequestRepository returnRequestRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            InventoryRepository inventoryRepository) {
        this.returnRequestRepository = returnRequestRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public Page<ReturnRequest> getAllReturnRequests(Pageable pageable) {
        return returnRequestRepository.findAll(pageable);
    }

    public Page<ReturnRequest> getReturnRequestsByStatus(String status, Pageable pageable) {
        return returnRequestRepository.findByStatus(status, pageable);
    }

    public ReturnRequest getReturnRequestById(UUID returnRequestId) {
        return returnRequestRepository.findById(returnRequestId).orElseThrow();
    }

    public List<ReturnRequest> getReturnRequestsByOrder(UUID orderId) {
        return returnRequestRepository.findByOrderId(orderId);
    }

    @Transactional
    public ReturnRequest createReturnRequest(UUID orderId, UUID orderItemId, String reason,
            String description, BigDecimal refundAmount, String refundMethod) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow();

        // Generate RMA number
        String rmaNumber = "RMA-" + System.currentTimeMillis();

        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setRmaNumber(rmaNumber);
        returnRequest.setOrder(order);
        returnRequest.setOrderItem(orderItem);
        returnRequest.setReason(reason);
        returnRequest.setDescription(description);
        returnRequest.setRefundAmount(refundAmount);
        returnRequest.setRefundMethod(refundMethod);
        returnRequest.setStatus("PENDING");

        return returnRequestRepository.save(returnRequest);
    }

    @Transactional
    public ReturnRequest approveReturnRequest(UUID returnRequestId, String adminNotes) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnRequestId).orElseThrow();
        returnRequest.setStatus("APPROVED");
        returnRequest.setAdminNotes(adminNotes);
        // ReturnRequest updatedAt is handled by @UpdateTimestamp
        return returnRequestRepository.save(returnRequest);
    }

    @Transactional
    public ReturnRequest rejectReturnRequest(UUID returnRequestId, String adminNotes) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnRequestId).orElseThrow();
        returnRequest.setStatus("REJECTED");
        returnRequest.setAdminNotes(adminNotes);
        // ReturnRequest updatedAt is handled by @UpdateTimestamp
        return returnRequestRepository.save(returnRequest);
    }

    @Transactional
    public ReturnRequest processReturnRequest(UUID returnRequestId) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnRequestId).orElseThrow();

        // Restore inventory
        var inventory = inventoryRepository.findAll().stream()
                .filter(inv -> inv.getVariant().getId().equals(returnRequest.getOrderItem().getVariant().getId()))
                .findFirst();

        if (inventory.isPresent()) {
            var inv = inventory.get();
            inv.setOnHand(inv.getOnHand() + returnRequest.getOrderItem().getQty());
            inventoryRepository.save(inv);
        }

        returnRequest.setStatus("PROCESSING");
        // ReturnRequest updatedAt is handled by @UpdateTimestamp
        return returnRequestRepository.save(returnRequest);
    }

    @Transactional
    public ReturnRequest completeReturnRequest(UUID returnRequestId) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnRequestId).orElseThrow();
        returnRequest.setStatus("COMPLETED");
        returnRequest.setProcessedAt(OffsetDateTime.now());
        // ReturnRequest updatedAt is handled by @UpdateTimestamp
        return returnRequestRepository.save(returnRequest);
    }

    public Map<String, Object> getReturnSummary() {
        long totalReturns = returnRequestRepository.count();
        long pendingReturns = returnRequestRepository.countByStatus("PENDING");
        long approvedReturns = returnRequestRepository.countByStatus("APPROVED");
        long processingReturns = returnRequestRepository.countByStatus("PROCESSING");
        long completedReturns = returnRequestRepository.countByStatus("COMPLETED");
        long rejectedReturns = returnRequestRepository.countByStatus("REJECTED");

        return Map.of(
                "total", totalReturns,
                "pending", pendingReturns,
                "approved", approvedReturns,
                "processing", processingReturns,
                "completed", completedReturns,
                "rejected", rejectedReturns);
    }
}
