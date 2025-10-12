package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.order.Order;
import com.eyeglasses.eyeglasses_store.entity.service.ServiceTicket;
import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.repository.order.OrderRepository;
import com.eyeglasses.eyeglasses_store.repository.service.ServiceTicketRepository;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ServiceTicketService {

    private final ServiceTicketRepository serviceTicketRepository;
    private final AppUserRepository userRepository;
    private final OrderRepository orderRepository;

    public ServiceTicketService(ServiceTicketRepository serviceTicketRepository,
            AppUserRepository userRepository,
            OrderRepository orderRepository) {
        this.serviceTicketRepository = serviceTicketRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Map<String, Object> create(UUID userId, UUID orderId, String productNameSnapshot, String serviceType,
            String description, String photoUrls) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        Order order = orderId != null ? orderRepository.findById(orderId).orElse(null) : null;
        ServiceTicket st = new ServiceTicket();
        st.setUser(user);
        st.setOrder(order);
        st.setProductNameSnapshot(productNameSnapshot);
        st.setServiceType(serviceType);
        st.setDescription(description);
        st.setPhotoUrls(photoUrls);
        ServiceTicket saved = serviceTicketRepository.save(st);
        return Map.of("id", saved.getId());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listByUser(UUID userId) {
        return serviceTicketRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toPayload).toList();
    }

    @Transactional
    public Map<String, Object> updateStatus(UUID ticketId, String status) {
        ServiceTicket st = serviceTicketRepository.findById(ticketId).orElseThrow();
        st.setStatus(status);
        serviceTicketRepository.save(st);
        return toPayload(st);
    }

    @Transactional
    public Map<String, Object> estimate(UUID ticketId, BigDecimal estimatedCost) {
        ServiceTicket st = serviceTicketRepository.findById(ticketId).orElseThrow();
        st.setEstimatedCost(estimatedCost);
        serviceTicketRepository.save(st);
        return toPayload(st);
    }

    @Transactional
    public Map<String, Object> finalizeCost(UUID ticketId, BigDecimal actualCost) {
        ServiceTicket st = serviceTicketRepository.findById(ticketId).orElseThrow();
        st.setActualCost(actualCost);
        st.setStatus("DONE");
        serviceTicketRepository.save(st);
        return toPayload(st);
    }

    private Map<String, Object> toPayload(ServiceTicket s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getId());
        m.put("userId", s.getUser() != null ? s.getUser().getId() : null);
        m.put("orderId", s.getOrder() != null ? s.getOrder().getId() : null);
        m.put("productNameSnapshot", s.getProductNameSnapshot());
        m.put("serviceType", s.getServiceType());
        m.put("description", s.getDescription());
        m.put("photoUrls", s.getPhotoUrls());
        m.put("status", s.getStatus());
        m.put("estimatedCost", s.getEstimatedCost());
        m.put("estimatedCompletion", s.getEstimatedCompletion());
        m.put("actualCost", s.getActualCost());
        m.put("createdAt", s.getCreatedAt());
        m.put("updatedAt", s.getUpdatedAt());
        return m;
    }
}
