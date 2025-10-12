package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.store.Store;
import com.eyeglasses.eyeglasses_store.service.AdminStoreManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/stores")
public class AdminStoreManagementController {

    private final AdminStoreManagementService adminStoreManagementService;

    public AdminStoreManagementController(AdminStoreManagementService adminStoreManagementService) {
        this.adminStoreManagementService = adminStoreManagementService;
    }

    @GetMapping
    public ResponseEntity<Page<Store>> getAllStores(Pageable pageable) {
        Page<Store> stores = adminStoreManagementService.getAllStores(pageable);
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Store>> getActiveStores() {
        List<Store> stores = adminStoreManagementService.getAllActiveStores();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<Store> getStoreById(@PathVariable UUID storeId) {
        Store store = adminStoreManagementService.getStoreById(storeId);
        return ResponseEntity.ok(store);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createStore(@RequestBody Map<String, Object> storeData) {
        try {
            Store store = adminStoreManagementService.createStore(storeData);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "storeId", store.getId(),
                    "code", store.getCode(),
                    "name", store.getName(),
                    "message", "Store created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{storeId}")
    public ResponseEntity<Map<String, Object>> updateStore(
            @PathVariable UUID storeId,
            @RequestBody Map<String, Object> storeData) {
        try {
            Store store = adminStoreManagementService.updateStore(storeId, storeData);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "storeId", store.getId(),
                    "name", store.getName(),
                    "message", "Store updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{storeId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleStoreStatus(
            @PathVariable UUID storeId,
            @RequestParam boolean active) {
        try {
            Store store = adminStoreManagementService.toggleStoreStatus(storeId, active);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "storeId", store.getId(),
                    "active", store.isActive(),
                    "message", "Store status updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Map<String, Object>> deleteStore(@PathVariable UUID storeId) {
        try {
            adminStoreManagementService.deleteStore(storeId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Store deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getStoreSummary() {
        return ResponseEntity.ok(adminStoreManagementService.getStoreSummary());
    }
}

