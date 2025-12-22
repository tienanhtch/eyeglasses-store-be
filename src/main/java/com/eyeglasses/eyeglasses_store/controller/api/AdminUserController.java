package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.AdminUserService;
import com.eyeglasses.eyeglasses_store.util.UuidUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list() {
        return ResponseEntity.ok(adminUserService.listUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable String userId) {
        try {
            UUID uuid = UuidUtil.parseUuid(userId);
            Map<String, Object> user = adminUserService.getUserById(uuid);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> userData) {
        try {
            Map<String, Object> user = adminUserService.createUser(userData);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", user,
                    "message", "User created successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", ex.getMessage()));
        }
    }

    private record AssignRolesRequest(List<String> roles) {
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<Map<String, Object>> assignRoles(@PathVariable("userId") String userId,
            @RequestBody AssignRolesRequest body) {
        try {
            UUID uuid = UuidUtil.parseUuid(userId);
            return ResponseEntity.ok(adminUserService.assignRoles(uuid, body.roles()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", ex.getMessage()));
        }
    }

    @PostMapping("/{userId}/lock")
    public ResponseEntity<Map<String, Object>> lock(
            @PathVariable("userId") String userId,
            @RequestParam(value = "active", defaultValue = "false") boolean active) {
        try {
            UUID uuid = UuidUtil.parseUuid(userId);
            return ResponseEntity.ok(adminUserService.lock(uuid, active));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", ex.getMessage()));
        }
    }

    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @PathVariable("userId") String userId,
            @RequestParam("newPassword") String newPassword) {
        try {
            UUID uuid = UuidUtil.parseUuid(userId);
            return ResponseEntity.ok(adminUserService.resetPassword(uuid, newPassword));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", ex.getMessage()));
        }
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable String userId,
            @RequestBody Map<String, Object> userData) {
        try {
            UUID uuid = UuidUtil.parseUuid(userId);
            Map<String, Object> user = adminUserService.updateUser(uuid, userData);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", user);
            response.put("message", "User updated successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String userId) {
        try {
            UUID uuid = UuidUtil.parseUuid(userId);
            adminUserService.deleteUser(uuid);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User deleted successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", ex.getMessage()));
        }
    }
}
