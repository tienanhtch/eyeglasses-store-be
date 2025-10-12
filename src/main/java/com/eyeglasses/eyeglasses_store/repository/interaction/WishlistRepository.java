package com.eyeglasses.eyeglasses_store.repository.interaction;

import com.eyeglasses.eyeglasses_store.entity.interaction.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    List<Wishlist> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Wishlist> findByUserIdAndProductId(UUID userId, UUID productId);
}
