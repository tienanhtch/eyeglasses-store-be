package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.entity.user.Role;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import com.eyeglasses.eyeglasses_store.repository.user.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminUserManagementService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserManagementService(AppUserRepository appUserRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<AppUser> getAllUsers(Pageable pageable) {
        return appUserRepository.findAll(pageable);
    }

    public List<AppUser> getUsersByRole(String roleCode) {
        return appUserRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getCode().equals(roleCode)))
                .toList();
    }

    public AppUser getUserById(UUID userId) {
        return appUserRepository.findById(userId).orElseThrow();
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(UUID roleId) {
        return roleRepository.findById(roleId).orElseThrow();
    }

    public Role getRoleByCode(String roleCode) {
        return roleRepository.findAll().stream()
                .filter(role -> role.getCode().equals(roleCode))
                .findFirst()
                .orElseThrow();
    }

    @Transactional
    public AppUser createUser(Map<String, Object> userData) {
        AppUser user = new AppUser();
        user.setEmail((String) userData.get("email"));
        user.setPasswordHash(passwordEncoder.encode((String) userData.get("password")));
        user.setFullName((String) userData.get("fullName"));
        user.setPhone((String) userData.get("phone"));
        user.setActive((Boolean) userData.getOrDefault("active", true));

        return appUserRepository.save(user);
    }

    @Transactional
    public AppUser updateUser(UUID userId, Map<String, Object> userData) {
        AppUser user = appUserRepository.findById(userId).orElseThrow();

        if (userData.get("email") != null) {
            user.setEmail((String) userData.get("email"));
        }
        if (userData.get("fullName") != null) {
            user.setFullName((String) userData.get("fullName"));
        }
        if (userData.get("phone") != null) {
            user.setPhone((String) userData.get("phone"));
        }
        if (userData.get("active") != null) {
            user.setActive((Boolean) userData.get("active"));
        }

        return appUserRepository.save(user);
    }

    @Transactional
    public AppUser assignRole(UUID userId, UUID roleId) {
        AppUser user = appUserRepository.findById(userId).orElseThrow();
        Role role = roleRepository.findById(roleId).orElseThrow();

        user.getRoles().add(role);
        return appUserRepository.save(user);
    }

    @Transactional
    public AppUser removeRole(UUID userId, UUID roleId) {
        AppUser user = appUserRepository.findById(userId).orElseThrow();
        Role role = roleRepository.findById(roleId).orElseThrow();

        user.getRoles().remove(role);
        return appUserRepository.save(user);
    }

    @Transactional
    public AppUser lockUser(UUID userId, String reason) {
        AppUser user = appUserRepository.findById(userId).orElseThrow();
        user.setActive(false);
        return appUserRepository.save(user);
    }

    @Transactional
    public AppUser unlockUser(UUID userId) {
        AppUser user = appUserRepository.findById(userId).orElseThrow();
        user.setActive(true);
        return appUserRepository.save(user);
    }

    @Transactional
    public AppUser resetPassword(UUID userId, String newPassword) {
        AppUser user = appUserRepository.findById(userId).orElseThrow();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return appUserRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        AppUser user = appUserRepository.findById(userId).orElseThrow();
        appUserRepository.delete(user);
    }

    @Transactional
    public Role createRole(Map<String, Object> roleData) {
        Role role = new Role();
        role.setCode((String) roleData.get("code"));
        role.setName((String) roleData.get("name"));

        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(UUID roleId, Map<String, Object> roleData) {
        Role role = roleRepository.findById(roleId).orElseThrow();

        if (roleData.get("code") != null) {
            role.setCode((String) roleData.get("code"));
        }
        if (roleData.get("name") != null) {
            role.setName((String) roleData.get("name"));
        }

        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(UUID roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow();
        roleRepository.delete(role);
    }

    public Map<String, Object> getUserSummary() {
        List<AppUser> allUsers = appUserRepository.findAll();
        long totalUsers = allUsers.size();
        long activeUsers = allUsers.stream().mapToLong(u -> u.isActive() ? 1 : 0).sum();
        long inactiveUsers = totalUsers - activeUsers;

        long adminUsers = allUsers.stream()
                .mapToLong(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getCode().equals("ADMIN")) ? 1 : 0)
                .sum();

        long staffUsers = allUsers.stream()
                .mapToLong(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getCode().equals("STAFF")) ? 1 : 0)
                .sum();

        long customerUsers = allUsers.stream()
                .mapToLong(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getCode().equals("CUSTOMER")) ? 1 : 0)
                .sum();

        return Map.of(
                "total", totalUsers,
                "active", activeUsers,
                "inactive", inactiveUsers,
                "admins", adminUsers,
                "staff", staffUsers,
                "customers", customerUsers);
    }
}
