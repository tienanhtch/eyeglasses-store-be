package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.catalog.Product;
import com.eyeglasses.eyeglasses_store.entity.interaction.Review;
import com.eyeglasses.eyeglasses_store.entity.interaction.Wishlist;
import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductRepository;
import com.eyeglasses.eyeglasses_store.repository.interaction.ReviewRepository;
import com.eyeglasses.eyeglasses_store.repository.interaction.WishlistRepository;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class InteractionService {

    private final ReviewRepository reviewRepository;
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final AppUserRepository userRepository;

    public InteractionService(ReviewRepository reviewRepository,
            WishlistRepository wishlistRepository,
            ProductRepository productRepository,
            AppUserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listReviews(UUID productId) {
        return reviewRepository.findByProductIdAndPublishedTrueOrderByCreatedAtDesc(productId)
                .stream().map(r -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", r.getId());
                    m.put("userId", r.getUser() != null ? r.getUser().getId() : null);
                    m.put("productId", r.getProduct() != null ? r.getProduct().getId() : null);
                    m.put("rating", r.getRating());
                    m.put("content", r.getContent());
                    m.put("createdAt", r.getCreatedAt());
                    return m;
                }).toList();
    }

    @Transactional
    public Map<String, Object> createReview(UUID userId, UUID productId, Integer rating, String content) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        Review r = new Review();
        r.setUser(user);
        r.setProduct(product);
        r.setRating(rating);
        r.setContent(content);
        r.setPublished(true);
        Review saved = reviewRepository.save(r);
        return Map.of("id", saved.getId());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listWishlist(UUID userId) {
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(w -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", w.getId());
            m.put("productId", w.getProduct().getId());
            m.put("createdAt", w.getCreatedAt());
            return m;
        }).toList();
    }

    @Transactional
    public Map<String, Object> addToWishlist(UUID userId, UUID productId) {
        if (wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            return Map.of("status", "exists");
        }
        AppUser user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        Wishlist w = new Wishlist();
        w.setUser(user);
        w.setProduct(product);
        Wishlist saved = wishlistRepository.save(w);
        return Map.of("id", saved.getId());
    }

    @Transactional
    public Map<String, Object> removeFromWishlist(UUID userId, UUID productId) {
        wishlistRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(wishlistRepository::delete);
        return Map.of("status", "ok");
    }
}
