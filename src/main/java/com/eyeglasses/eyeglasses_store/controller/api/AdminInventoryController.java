package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.inventory.Inventory;
import com.eyeglasses.eyeglasses_store.service.AdminInventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/inventory")
public class AdminInventoryController {

    private final AdminInventoryService adminInventoryService;

    public AdminInventoryController(AdminInventoryService adminInventoryService) {
        this.adminInventoryService = adminInventoryService;
    }

    private record SetStockRequest(UUID storeId, UUID variantId, Integer onHand, Integer reserved) {
    }

    @PostMapping("/set")
    public ResponseEntity<Map<String, Object>> setStock(@RequestBody SetStockRequest body) {
        Inventory inv = adminInventoryService.setStock(body.storeId(), body.variantId(), body.onHand(),
                body.reserved());
        return ResponseEntity.ok(Map.of(
                "id", inv.getId(),
                "storeId", inv.getStore().getId(),
                "variantId", inv.getVariant().getId(),
                "onHand", inv.getOnHand(),
                "reserved", inv.getReserved()));
    }
}
