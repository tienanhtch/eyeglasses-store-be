package com.eyeglasses.eyeglasses_store.repository.interaction;

import com.eyeglasses.eyeglasses_store.entity.interaction.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByProductIdAndPublishedTrueOrderByCreatedAtDesc(UUID productId);
}
