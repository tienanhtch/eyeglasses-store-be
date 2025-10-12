package com.eyeglasses.eyeglasses_store.repository.user;

import com.eyeglasses.eyeglasses_store.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByCode(String code);
}
