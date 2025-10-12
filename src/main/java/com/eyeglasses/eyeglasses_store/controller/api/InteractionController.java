package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.InteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.API_BASE_PATH)
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    // Reviews
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<Map<String, Object>>> listReviews(@PathVariable("productId") UUID productId) {
        return ResponseEntity.ok(interactionService.listReviews(productId));
    }

    private record ReviewRequest(UUID userId, Integer rating, String content) {
    }

    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<Map<String, Object>> createReview(@PathVariable("productId") UUID productId,
            @RequestBody ReviewRequest body) {
        return ResponseEntity
                .ok(interactionService.createReview(body.userId(), productId, body.rating(), body.content()));
    }

    // Wishlist
    @GetMapping("/wishlist")
    public ResponseEntity<List<Map<String, Object>>> wishlist(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(interactionService.listWishlist(userId));
    }

    private record WishlistRequest(UUID userId, UUID productId) {
    }

    @PostMapping("/wishlist")
    public ResponseEntity<Map<String, Object>> addWishlist(@RequestBody WishlistRequest body) {
        return ResponseEntity.ok(interactionService.addToWishlist(body.userId(), body.productId()));
    }

    @DeleteMapping("/wishlist")
    public ResponseEntity<Map<String, Object>> removeWishlist(@RequestParam("userId") UUID userId,
            @RequestParam("productId") UUID productId) {
        return ResponseEntity.ok(interactionService.removeFromWishlist(userId, productId));
    }
}
