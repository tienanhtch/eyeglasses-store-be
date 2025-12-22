package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.entity.user.Role;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import com.eyeglasses.eyeglasses_store.repository.user.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AdminUserService {

    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(AppUserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listUsers() {
        return userRepository.findAll().stream().map(this::toPayload).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserById(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toPayload(user);
    }

    @Transactional
    public Map<String, Object> assignRoles(UUID userId, List<String> roleCodes) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        user.getRoles().clear();
        for (String code : roleCodes) {
            roleRepository.findByCode(code).ifPresent(r -> user.getRoles().add(r));
        }
        userRepository.save(user);
        return toPayload(user);
    }

    @Transactional
    public Map<String, Object> lock(UUID userId, boolean active) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        user.setActive(active);
        userRepository.save(user);
        return toPayload(user);
    }

    @Transactional
    public Map<String, Object> resetPassword(UUID userId, String newPassword) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return Map.of("status", "ok");
    }

    @Transactional
    public Map<String, Object> createUser(Map<String, Object> userData) {
        String email = (String) userData.get("email");
        if (email != null && userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode((String) userData.get("password")));
        user.setFullName((String) userData.get("fullName"));
        user.setPhone((String) userData.get("phone"));
        user.setActive((Boolean) userData.getOrDefault("active", true));

        // Assign roles if provided
        if (userData.get("roleIds") != null) {
            @SuppressWarnings("unchecked")
            List<String> roleIds = (List<String>) userData.get("roleIds");
            for (String roleIdStr : roleIds) {
                try {
                    // Handle hex format with 0x prefix (MySQL BINARY(16) format)
                    UUID roleId;
                    if (roleIdStr.startsWith("0x")) {
                        // Convert hex string to UUID
                        String hex = roleIdStr.substring(2);
                        if (hex.length() == 32) {
                            // Convert hex to UUID format
                            String uuidStr = hex.substring(0, 8) + "-" +
                                    hex.substring(8, 12) + "-" +
                                    hex.substring(12, 16) + "-" +
                                    hex.substring(16, 20) + "-" +
                                    hex.substring(20, 32);
                            roleId = UUID.fromString(uuidStr);
                        } else {
                            continue;
                        }
                    } else {
                        roleId = UUID.fromString(roleIdStr);
                    }
                    roleRepository.findById(roleId).ifPresent(r -> user.getRoles().add(r));
                } catch (IllegalArgumentException e) {
                    // Invalid UUID format, skip
                }
            }
        }

        AppUser saved = userRepository.saveAndFlush(user);
        return toPayload(saved);
    }

    @Transactional
    public Map<String, Object> updateUser(UUID userId, Map<String, Object> userData) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userData.get("email") != null) {
            String newEmail = (String) userData.get("email");
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && 
                userRepository.existsByEmailIgnoreCase(newEmail)) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(newEmail);
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
        if (userData.get("password") != null) {
            user.setPasswordHash(passwordEncoder.encode((String) userData.get("password")));
        }

        AppUser saved = userRepository.saveAndFlush(user);
        return toPayload(saved);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
    }

    private Map<String, Object> toPayload(AppUser u) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getId());
        m.put("email", u.getEmail());
        m.put("fullName", u.getFullName());
        m.put("phone", u.getPhone());
        m.put("isActive", u.isActive());
        m.put("roles", u.getRoles().stream().map(Role::getCode).toList());
        m.put("createdAt", u.getCreatedAt());
        m.put("updatedAt", u.getUpdatedAt());
        return m;
    }
}
