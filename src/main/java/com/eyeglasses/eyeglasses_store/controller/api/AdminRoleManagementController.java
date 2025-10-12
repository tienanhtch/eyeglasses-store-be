package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.user.Role;
import com.eyeglasses.eyeglasses_store.service.AdminUserManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/roles")
public class AdminRoleManagementController {

    private final AdminUserManagementService adminUserManagementService;

    public AdminRoleManagementController(AdminUserManagementService adminUserManagementService) {
        this.adminUserManagementService = adminUserManagementService;
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = adminUserManagementService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable UUID roleId) {
        Role role = adminUserManagementService.getRoleById(roleId);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/code/{roleCode}")
    public ResponseEntity<Role> getRoleByCode(@PathVariable String roleCode) {
        Role role = adminUserManagementService.getRoleByCode(roleCode);
        return ResponseEntity.ok(role);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createRole(@RequestBody Map<String, Object> roleData) {
        try {
            Role role = adminUserManagementService.createRole(roleData);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "roleId", role.getId(),
                    "code", role.getCode(),
                    "name", role.getName(),
                    "message", "Role created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @PatchMapping("/{roleId}")
    public ResponseEntity<Map<String, Object>> updateRole(
            @PathVariable UUID roleId,
            @RequestBody Map<String, Object> roleData) {
        try {
            Role role = adminUserManagementService.updateRole(roleId, roleData);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "roleId", role.getId(),
                    "code", role.getCode(),
                    "name", role.getName(),
                    "message", "Role updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Map<String, Object>> deleteRole(@PathVariable UUID roleId) {
        try {
            adminUserManagementService.deleteRole(roleId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Role deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }
}
