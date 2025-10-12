package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.order.Order;
import com.eyeglasses.eyeglasses_store.entity.user.Address;
import com.eyeglasses.eyeglasses_store.repository.order.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AdminShippingService {

    private final OrderRepository orderRepository;

    public AdminShippingService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Map<String, Object> generateShippingLabel(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        // Get shipping address
        Address shippingAddress = order.getShippingAddress();

        // Generate mock shipping label data
        String trackingNumber = "TRK" + System.currentTimeMillis();
        String carrier = "Viettel Post";
        String serviceType = "Standard";

        return Map.of(
                "orderId", orderId,
                "orderNumber", order.getOrderNo(),
                "trackingNumber", trackingNumber,
                "carrier", carrier,
                "serviceType", serviceType,
                "shippingAddress", shippingAddress != null ? Map.of(
                        "recipient", shippingAddress.getRecipient(),
                        "phone", shippingAddress.getPhone(),
                        "line1", shippingAddress.getLine1(),
                        "line2", shippingAddress.getLine2(),
                        "city", shippingAddress.getCity(),
                        "district", shippingAddress.getDistrict(),
                        "ward", shippingAddress.getWard(),
                        "postalCode", shippingAddress.getPostalCode()) : null,
                "labelUrl", "/api/v1/admin/shipping/labels/" + orderId + "/download",
                "barcode",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");
    }

    public Map<String, Object> getShippingRates(UUID orderId) {
        // Mock shipping rates based on order weight/value
        return Map.of(
                "orderId", orderId,
                "rates", Map.of(
                        "standard", Map.of(
                                "carrier", "Viettel Post",
                                "service", "Standard",
                                "cost", 30000,
                                "estimatedDays", "3-5"),
                        "express", Map.of(
                                "carrier", "Viettel Post",
                                "service", "Express",
                                "cost", 50000,
                                "estimatedDays", "1-2"),
                        "overnight", Map.of(
                                "carrier", "Viettel Post",
                                "service", "Overnight",
                                "cost", 80000,
                                "estimatedDays", "1")));
    }

    public Map<String, Object> trackShipment(String trackingNumber) {
        // Mock tracking information
        return Map.of(
                "trackingNumber", trackingNumber,
                "status", "In Transit",
                "carrier", "Viettel Post",
                "events", Map.of(
                        "2024-01-15T10:00:00Z", "Package picked up",
                        "2024-01-15T14:30:00Z", "Package in transit",
                        "2024-01-16T09:15:00Z", "Out for delivery"),
                "estimatedDelivery", "2024-01-16T18:00:00Z");
    }
}
