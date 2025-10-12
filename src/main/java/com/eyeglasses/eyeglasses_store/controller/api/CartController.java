package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.API_BASE_PATH + "/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private record AddItemRequest(UUID variantId, Integer qty, UUID lensPackageId, UUID prescriptionId,
            BigDecimal customPrice, String note) {
    }

    private record UpdateItemRequest(Integer qty, UUID lensPackageId, BigDecimal customPrice, String note) {
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> addItem(@RequestParam("userId") UUID userId,
            @RequestBody AddItemRequest body) {
        return ResponseEntity.ok(cartService.addItem(userId, body.variantId(), body.qty(), body.lensPackageId(),
                body.prescriptionId(), body.customPrice(), body.note()));
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<Map<String, Object>> updateItem(@PathVariable("itemId") UUID itemId,
            @RequestBody UpdateItemRequest body) {
        return ResponseEntity
                .ok(cartService.updateItem(itemId, body.qty(), body.lensPackageId(), body.customPrice(), body.note()));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Map<String, Object>> deleteItem(@PathVariable("itemId") UUID itemId) {
        return ResponseEntity.ok(cartService.removeItem(itemId));
    }
}
