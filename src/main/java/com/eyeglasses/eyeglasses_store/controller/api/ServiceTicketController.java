package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.ServiceTicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.API_BASE_PATH + "/service-tickets")
public class ServiceTicketController {

    private final ServiceTicketService serviceTicketService;

    public ServiceTicketController(ServiceTicketService serviceTicketService) {
        this.serviceTicketService = serviceTicketService;
    }

    private record CreateRequest(UUID userId, UUID orderId, String productName, String serviceType, String description,
            String photoUrls) {
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody CreateRequest body) {
        return ResponseEntity.ok(serviceTicketService.create(body.userId(), body.orderId(), body.productName(),
                body.serviceType(), body.description(), body.photoUrls()));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(serviceTicketService.listByUser(userId));
    }

    private record StatusRequest(String status) {
    }

    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable("ticketId") UUID ticketId,
            @RequestBody StatusRequest body) {
        return ResponseEntity.ok(serviceTicketService.updateStatus(ticketId, body.status()));
    }

    private record EstimateRequest(BigDecimal estimatedCost, java.time.OffsetDateTime estimatedCompletion) {
    }

    @PostMapping("/{ticketId}/estimate")
    public ResponseEntity<Map<String, Object>> estimate(@PathVariable("ticketId") UUID ticketId,
            @RequestBody EstimateRequest body) {
        var resp = serviceTicketService.estimate(ticketId, body.estimatedCost());
        if (body.estimatedCompletion() != null) {
            // use status update to persist other changes if needed in future
            serviceTicketService.updateStatus(ticketId, resp.get("status").toString());
        }
        return ResponseEntity.ok(resp);
    }

    private record FinalizeRequest(BigDecimal actualCost) {
    }

    @PostMapping("/{ticketId}/finalize")
    public ResponseEntity<Map<String, Object>> finalizeTicket(@PathVariable("ticketId") UUID ticketId,
            @RequestBody FinalizeRequest body) {
        return ResponseEntity.ok(serviceTicketService.finalizeCost(ticketId, body.actualCost()));
    }
}
