package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.entity.user.Role;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import com.eyeglasses.eyeglasses_store.repository.user.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.eyeglasses.eyeglasses_store.util.jwt.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AppUserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Map<String, Object> register(String email, String password, String fullName, String phone) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPasswordHash(password != null ? passwordEncoder.encode(password) : null);
        user.setFullName(fullName);
        user.setPhone(phone);
        // assign CUSTOMER role if exists
        Optional<Role> role = roleRepository.findByCode("CUSTOMER");
        role.ifPresent(r -> user.getRoles().add(r));
        AppUser saved = userRepository.save(user);
        return toUserPayload(saved, generateJwt(saved));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> login(String email, String password) {
        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (user.getPasswordHash() == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return toUserPayload(user, generateJwt(user));
    }

    private String generateJwt(AppUser user) {
        String secret = System.getenv().getOrDefault("JWT_SECRET", "dev-secret");
        long expires = Long.parseLong(System.getenv().getOrDefault("JWT_EXPIRES_MS", "86400000"));
        java.util.List<String> roles = user.getRoles().stream().map(Role::getCode).toList();
        return JwtUtil.generateToken(user.getId().toString(), roles, secret, expires);
    }

    public Map<String, Object> toUserPayload(AppUser user, String token) {
        Map<String, Object> m = new HashMap<>();
        m.put("token", token);
        m.put("user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "phone", user.getPhone(),
                "isActive", user.isActive(),
                "roles", user.getRoles().stream().map(Role::getCode).toList(),
                "createdAt", user.getCreatedAt(),
                "updatedAt", user.getUpdatedAt()));
        return m;
    }
}
