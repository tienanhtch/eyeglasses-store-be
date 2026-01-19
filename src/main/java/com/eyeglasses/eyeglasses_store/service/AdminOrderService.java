package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.order.Order;
import com.eyeglasses.eyeglasses_store.entity.order.OrderItem;
import com.eyeglasses.eyeglasses_store.entity.order.Payment;
import com.eyeglasses.eyeglasses_store.repository.order.OrderItemRepository;
import com.eyeglasses.eyeglasses_store.repository.order.OrderRepository;
import com.eyeglasses.eyeglasses_store.repository.order.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    public AdminOrderService(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listAll() {
        return orderRepository.findAll().stream().map(this::toPayload).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDetail(UUID orderId) {
        Order o = orderRepository.findById(orderId).orElseThrow();
        return toPayload(o);
    }

    @Transactional
    public Map<String, Object> updateStatus(UUID orderId, String status) {
        Order o = orderRepository.findById(orderId).orElseThrow();
        o.setStatus(status);
        orderRepository.save(o);
        return toPayload(o);
    }

    @Transactional
    public Map<String, Object> refundOrder(UUID orderId) {
        Order o = orderRepository.findById(orderId).orElseThrow();
        // Mark payment refunded if exists
        List<Payment> payments = paymentRepository.findAll().stream().filter(p -> p.getOrder().getId().equals(orderId))
                .toList();
        for (Payment p : payments) {
            p.setStatus("REFUNDED");
            p.setPaidAt(OffsetDateTime.now());
            paymentRepository.save(p);
        }
        o.setStatus("REFUNDED");
        orderRepository.save(o);
        return toPayload(o);
    }

    private Map<String, Object> toPayload(Order o) {
        List<OrderItem> items = orderItemRepository.findByOrderId(o.getId());
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", o.getId());
        m.put("orderNo", o.getOrderNo());
        m.put("userId", o.getUser() != null ? o.getUser().getId() : null);
        m.put("status", o.getStatus());
        m.put("fulfillment", o.getFulfillment());
        m.put("subtotal", o.getSubtotal());
        m.put("discountTotal", o.getDiscountTotal());
        m.put("shippingFee", o.getShippingFee());
        m.put("taxTotal", o.getTaxTotal());
        m.put("grandTotal", o.getGrandTotal());
        m.put("createdAt", o.getCreatedAt());
        m.put("updatedAt", o.getUpdatedAt());
        Map<String, Object> customer = new LinkedHashMap<>();
        if (o.getUser() != null) {
            customer.put("id", o.getUser().getId());
            customer.put("name", o.getUser().getFullName());
            customer.put("email", o.getUser().getEmail());
            customer.put("phone", o.getUser().getPhone());
        }
        m.put("customer", customer);

        m.put("items", items.stream().map(oi -> Map.of(
                "id", oi.getId(),
                "nameSnapshot", oi.getNameSnapshot(),
                "skuSnapshot", oi.getSkuSnapshot(),
                "priceUnit", oi.getPriceUnit(),
                "qty", oi.getQty(),
                "lineTotal", oi.getLineTotal())).toList());
        return m;
    }
}
