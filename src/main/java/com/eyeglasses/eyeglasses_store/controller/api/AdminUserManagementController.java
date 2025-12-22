package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.service.AdminUserManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/users-management")
public class AdminUserManagementController {

    private final AdminUserManagementService adminUserManagementService;

    public AdminUserManagementController(AdminUserManagementService adminUserManagementService) {
        this.adminUserManagementService = adminUserManagementService;
    }

    @GetMapping
    public ResponseEntity<Page<AppUser>> getAllUsers(Pageable pageable) {
        Page<AppUser> users = adminUserManagementService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{roleCode}")
    public ResponseEntity<List<AppUser>> getUsersByRole(@PathVariable String roleCode) {
        List<AppUser> users = adminUserManagementService.getUsersByRole(roleCode);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AppUser> getUserById(@PathVariable UUID userId) {
        AppUser user = adminUserManagementService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> userData) {
        try {
            AppUser user = adminUserManagementService.createUser(userData);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "userId", user.getId(),
                    "email", user.getEmail(),
                    "message", "User created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable UUID userId,
            @RequestBody Map<String, Object> userData) {
        try {
            AppUser user = adminUserManagementService.updateUser(userId, userData);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "userId", user.getId(),
                    "email", user.getEmail(),
                    "message", "User updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Map<String, Object>> assignRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId) {
        try {
            AppUser user = adminUserManagementService.assignRole(userId, roleId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "userId", user.getId(),
                    "message", "Role assigned successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Map<String, Object>> removeRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId) {
        try {
            AppUser user = adminUserManagementService.removeRole(userId, roleId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "userId", user.getId(),
                    "message", "Role removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/lock")
    public ResponseEntity<Map<String, Object>> lockUser(
            @PathVariable UUID userId,
            @RequestParam(required = false) String reason) {
        try {
            AppUser user = adminUserManagementService.lockUser(userId, reason);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "userId", user.getId(),
                    "active", user.isActive(),
                    "message", "User locked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/unlock")
    public ResponseEntity<Map<String, Object>> unlockUser(@PathVariable UUID userId) {
        try {
            AppUser user = adminUserManagementService.unlockUser(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "userId", user.getId(),
                    "active", user.isActive(),
                    "message", "User unlocked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @PathVariable UUID userId,
            @RequestParam String newPassword) {
        try {
            AppUser user = adminUserManagementService.resetPassword(userId, newPassword);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "userId", user.getId(),
                    "message", "Password reset successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable UUID userId) {
        try {
            adminUserManagementService.deleteUser(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getUserSummary() {
        return ResponseEntity.ok(adminUserManagementService.getUserSummary());
    }
}
