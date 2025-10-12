package com.eyeglasses.eyeglasses_store.repository.user;

import com.eyeglasses.eyeglasses_store.entity.user.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
