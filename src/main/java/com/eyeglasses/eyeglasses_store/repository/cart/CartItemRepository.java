package com.eyeglasses.eyeglasses_store.repository.cart;

import com.eyeglasses.eyeglasses_store.entity.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByCartId(UUID cartId);
}
