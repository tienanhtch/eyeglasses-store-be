package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.order.Order;
import com.eyeglasses.eyeglasses_store.entity.order.OrderItem;
import com.eyeglasses.eyeglasses_store.entity.order.Payment;
import com.eyeglasses.eyeglasses_store.repository.order.OrderRepository;
import com.eyeglasses.eyeglasses_store.repository.order.OrderItemRepository;
import com.eyeglasses.eyeglasses_store.repository.order.PaymentRepository;
import com.eyeglasses.eyeglasses_store.repository.inventory.InventoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminOrderManagementService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final InventoryRepository inventoryRepository;

    public AdminOrderManagementService(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository,
            InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.paymentRepository = paymentRepository;
    }

    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Page<Order> getOrdersByStatus(String status, Pageable pageable) {
        return orderRepository.findAll(pageable); // TODO: Add findByStatus method to repository
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow();
    }

    public List<OrderItem> getOrderItems(UUID orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public List<Payment> getOrderPayments(UUID orderId) {
        return paymentRepository.findAll(); // TODO: Add findByOrderId method to repository
    }

    @Transactional
    public Order updateOrderStatus(UUID orderId, String newStatus, String note) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(newStatus);
        // Order updatedAt is handled by @UpdateTimestamp
        orderRepository.save(order);
        return order;
    }

    @Transactional
    public Order processOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        // Check inventory for all items
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            // Reserve inventory
            var inventory = inventoryRepository.findAll().stream()
                    .filter(inv -> inv.getVariant().getId().equals(item.getVariant().getId()))
                    .findFirst();

            if (inventory.isPresent()) {
                var inv = inventory.get();
                if (inv.getOnHand() < item.getQty()) {
                    throw new IllegalStateException(
                            "Insufficient inventory for variant: " + item.getVariant().getSku());
                }
                inv.setReserved(inv.getReserved() + item.getQty());
                inventoryRepository.save(inv);
            }
        }

        order.setStatus("PROCESSING");
        // Order updatedAt is handled by @UpdateTimestamp
        return orderRepository.save(order);
    }

    @Transactional
    public Order shipOrder(UUID orderId, String trackingNumber) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        // Update inventory - reduce on hand and reserved
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            var inventory = inventoryRepository.findAll().stream()
                    .filter(inv -> inv.getVariant().getId().equals(item.getVariant().getId()))
                    .findFirst();

            if (inventory.isPresent()) {
                var inv = inventory.get();
                inv.setOnHand(inv.getOnHand() - item.getQty());
                inv.setReserved(inv.getReserved() - item.getQty());
                inventoryRepository.save(inv);
            }
        }

        order.setStatus("SHIPPED");
        // Order updatedAt is handled by @UpdateTimestamp
        return orderRepository.save(order);
    }

    @Transactional
    public Order completeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus("COMPLETED");
        // Order updatedAt is handled by @UpdateTimestamp
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(UUID orderId, String reason) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        // Release reserved inventory
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            var inventory = inventoryRepository.findAll().stream()
                    .filter(inv -> inv.getVariant().getId().equals(item.getVariant().getId()))
                    .findFirst();

            if (inventory.isPresent()) {
                var inv = inventory.get();
                inv.setReserved(inv.getReserved() - item.getQty());
                inventoryRepository.save(inv);
            }
        }

        order.setStatus("CANCELLED");
        // Order updatedAt is handled by @UpdateTimestamp
        return orderRepository.save(order);
    }

    public Map<String, Object> getOrderSummary() {
        long totalOrders = orderRepository.count();
        long pendingOrders = 0; // TODO: Add countByStatus method to repository
        long processingOrders = 0;
        long shippedOrders = 0;
        long completedOrders = 0;
        long cancelledOrders = 0;
        Double totalRevenue = orderRepository.sumRevenue();

        return Map.of(
                "total", totalOrders,
                "pending", pendingOrders,
                "processing", processingOrders,
                "shipped", shippedOrders,
                "completed", completedOrders,
                "cancelled", cancelledOrders,
                "revenue", totalRevenue);
    }
}
