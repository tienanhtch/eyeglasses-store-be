package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private record AssignRolesRequest(List<String> roles) {
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<Map<String, Object>> assignRoles(@PathVariable("userId") UUID userId,
            @RequestBody AssignRolesRequest body) {
        return ResponseEntity.ok(adminUserService.assignRoles(userId, body.roles()));
    }

    private record LockRequest(boolean active) {
    }

    @PostMapping("/{userId}/lock")
    public ResponseEntity<Map<String, Object>> lock(@PathVariable("userId") UUID userId,
            @RequestBody LockRequest body) {
        return ResponseEntity.ok(adminUserService.lock(userId, body.active()));
    }

    private record ResetPasswordRequest(String newPassword) {
    }

    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable("userId") UUID userId,
            @RequestBody ResetPasswordRequest body) {
        return ResponseEntity.ok(adminUserService.resetPassword(userId, body.newPassword()));
    }
}
