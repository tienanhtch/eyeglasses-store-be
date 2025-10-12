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

    private Map<String, Object> toPayload(AppUser u) {
        return Map.of(
                "id", u.getId(),
                "email", u.getEmail(),
                "fullName", u.getFullName(),
                "phone", u.getPhone(),
                "isActive", u.isActive(),
                "roles", u.getRoles().stream().map(Role::getCode).toList(),
                "createdAt", u.getCreatedAt(),
                "updatedAt", u.getUpdatedAt());
    }
}
