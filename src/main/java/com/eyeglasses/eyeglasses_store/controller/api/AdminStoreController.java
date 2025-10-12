package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.store.Store;
import com.eyeglasses.eyeglasses_store.service.AdminStoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/stores")
public class AdminStoreController {

    private final AdminStoreService adminStoreService;

    public AdminStoreController(AdminStoreService adminStoreService) {
        this.adminStoreService = adminStoreService;
    }

    @GetMapping
    public ResponseEntity<List<Store>> list() {
        return ResponseEntity.ok(adminStoreService.list());
    }

    private record StoreRequest(String code, String name, String address, BigDecimal lat, BigDecimal lng, String phone,
            String openHours, Boolean active) {
    }

    @PostMapping
    public ResponseEntity<Store> create(@RequestBody StoreRequest body) {
        return ResponseEntity.ok(adminStoreService.create(body.code(), body.name(), body.address(), body.lat(),
                body.lng(), body.phone(), body.openHours(), body.active()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Store> update(@PathVariable("id") UUID id, @RequestBody StoreRequest body) {
        return ResponseEntity.ok(adminStoreService.update(id, body.name(), body.address(), body.lat(), body.lng(),
                body.phone(), body.openHours(), body.active()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("id") UUID id) {
        adminStoreService.delete(id);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
