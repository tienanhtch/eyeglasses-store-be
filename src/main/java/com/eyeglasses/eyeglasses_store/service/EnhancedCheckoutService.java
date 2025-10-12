package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.promotion.Promotion;
import com.eyeglasses.eyeglasses_store.entity.order.Order;
import com.eyeglasses.eyeglasses_store.entity.order.OrderItem;
import com.eyeglasses.eyeglasses_store.entity.cart.Cart;
import com.eyeglasses.eyeglasses_store.entity.cart.CartItem;
import com.eyeglasses.eyeglasses_store.repository.promotion.PromotionRepository;
import com.eyeglasses.eyeglasses_store.repository.order.OrderRepository;
import com.eyeglasses.eyeglasses_store.repository.cart.CartRepository;
import com.eyeglasses.eyeglasses_store.repository.cart.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class EnhancedCheckoutService {

    private final PromotionRepository promotionRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public EnhancedCheckoutService(PromotionRepository promotionRepository,
            OrderRepository orderRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository) {
        this.promotionRepository = promotionRepository;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Map<String, Object> validatePromotionCode(String code, UUID userId, BigDecimal orderTotal) {
        Optional<Promotion> promotionOpt = promotionRepository.findValidPromotionByCode(code, OffsetDateTime.now());

        if (promotionOpt.isEmpty()) {
            return Map.of(
                    "valid", false,
                    "error", "Promotion code not found or expired");
        }

        Promotion promotion = promotionOpt.get();

        // Check if user has already used this promotion
        // if (hasUserUsedPromotion(userId, promotion.getId())) { // TODO: Implement
        // when user promotion tracking is set up
        // return Map.of(
        // "valid", false,
        // "error", "You have already used this promotion code"
        // );
        // }

        // Check minimum order amount
        if (promotion.getMinOrderAmount() != null && orderTotal.compareTo(promotion.getMinOrderAmount()) < 0) {
            return Map.of(
                    "valid", false,
                    "error", "Minimum order amount not met");
        }

        // Calculate discount
        BigDecimal discount = calculateDiscount(promotion, orderTotal);

        return Map.of(
                "valid", true,
                "promotion", Map.of(
                        "id", promotion.getId(),
                        "code", promotion.getCode(),
                        "name", promotion.getName(),
                        "type", promotion.getType(),
                        "value", promotion.getValue(),
                        "discount", discount));
    }

    private boolean hasUserUsedPromotion(UUID userId, UUID promotionId) {
        // In real implementation, check order history for this promotion
        return false;
    }

    private BigDecimal calculateDiscount(Promotion promotion, BigDecimal orderTotal) {
        BigDecimal discount = BigDecimal.ZERO;

        switch (promotion.getType()) {
            case "PERCENTAGE":
                discount = orderTotal.multiply(promotion.getValue()).divide(BigDecimal.valueOf(100));
                break;
            case "FIXED_AMOUNT":
                discount = promotion.getValue();
                break;
            case "FREE_SHIPPING":
                // Handle shipping discount separately
                discount = BigDecimal.ZERO;
                break;
        }

        // Apply maximum discount limit
        if (promotion.getMaxDiscountAmount() != null && discount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
            discount = promotion.getMaxDiscountAmount();
        }

        // Ensure discount doesn't exceed order total
        if (discount.compareTo(orderTotal) > 0) {
            discount = orderTotal;
        }

        return discount;
    }

    public Map<String, Object> calculateOrderTotals(UUID cartId, String promotionCode, BigDecimal shippingFee) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        List<CartItem> cartItems = cartItemRepository.findAll().stream()
                .filter(item -> item.getCart().getId().equals(cartId))
                .toList();

        // Calculate subtotal
        BigDecimal subtotal = cartItems.stream()
                .map(item -> {
                    BigDecimal itemPrice = item.getCustomPrice() != null ? item.getCustomPrice()
                            : (item.getVariant().getSalePrice() != null ? item.getVariant().getSalePrice()
                                    : item.getVariant().getRetailPrice());
                    return itemPrice.multiply(BigDecimal.valueOf(item.getQty()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate discount
        BigDecimal discount = BigDecimal.ZERO;
        if (promotionCode != null && !promotionCode.trim().isEmpty()) {
            // Map<String, Object> promotionValidation =
            // validatePromotionCode(promotionCode, cart.getUserId(), subtotal); // TODO:
            // Implement when Cart entity is properly set up
            Map<String, Object> promotionValidation = validatePromotionCode(promotionCode, UUID.randomUUID(), subtotal); // Mock
                                                                                                                         // user
                                                                                                                         // ID
            if ((Boolean) promotionValidation.get("valid")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> promotionData = (Map<String, Object>) promotionValidation.get("promotion");
                discount = (BigDecimal) promotionData.get("discount");
            }
        }

        // Calculate tax (8% demo)
        BigDecimal taxRate = BigDecimal.valueOf(0.08);
        BigDecimal taxableAmount = subtotal.subtract(discount);
        BigDecimal tax = taxableAmount.multiply(taxRate);

        // Calculate total
        BigDecimal total = subtotal.subtract(discount).add(tax).add(shippingFee);

        return Map.of(
                "subtotal", subtotal,
                "discount", discount,
                "tax", tax,
                "shippingFee", shippingFee,
                "total", total,
                "promotionCode", promotionCode,
                "itemCount", cartItems.size());
    }

    @Transactional
    public Order createOrderWithPromotion(UUID cartId, UUID shippingAddressId, UUID billingAddressId,
            String fulfillment, UUID storeId, String promotionCode) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        List<CartItem> cartItems = cartItemRepository.findAll().stream()
                .filter(item -> item.getCart().getId().equals(cartId))
                .toList();

        // Calculate totals
        Map<String, Object> totals = calculateOrderTotals(cartId, promotionCode, BigDecimal.valueOf(30000));

        // Create order
        Order order = new Order();
        order.setOrderNo("ORD-" + System.currentTimeMillis());
        // order.setUserId(cart.getUserId()); // TODO: Implement when Cart entity is
        // properly set up
        order.setStatus("PENDING");
        order.setFulfillment(fulfillment);
        // order.setShippingAddressId(shippingAddressId); // TODO: Implement when Order
        // entity is properly set up
        // order.setBillingAddressId(billingAddressId); // TODO: Implement when Order
        // entity is properly set up
        order.setSubtotal((BigDecimal) totals.get("subtotal"));
        order.setDiscountTotal((BigDecimal) totals.get("discount"));
        order.setShippingFee((BigDecimal) totals.get("shippingFee"));
        order.setTaxTotal((BigDecimal) totals.get("tax"));
        order.setGrandTotal((BigDecimal) totals.get("total"));

        order = orderRepository.save(order);

        // Create order items
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariant(cartItem.getVariant());
            orderItem.setNameSnapshot(cartItem.getVariant().getProduct().getName());
            orderItem.setSkuSnapshot(cartItem.getVariant().getSku());
            orderItem.setPriceUnit(cartItem.getCustomPrice() != null ? cartItem.getCustomPrice()
                    : (cartItem.getVariant().getSalePrice() != null ? cartItem.getVariant().getSalePrice()
                            : cartItem.getVariant().getRetailPrice()));
            orderItem.setQty(cartItem.getQty());
            orderItem.setLineTotal(orderItem.getPriceUnit().multiply(BigDecimal.valueOf(cartItem.getQty())));

            if (cartItem.getLensPackage() != null) {
                // orderItem.setLensPackageId(cartItem.getLensPackage().getId()); // TODO:
                // Implement when OrderItem entity is properly set up
            }
            if (cartItem.getPrescription() != null) {
                // orderItem.setPrescriptionId(cartItem.getPrescription().getId()); // TODO:
                // Implement when OrderItem entity is properly set up
            }

            // Save order item
            // orderItemRepository.save(orderItem);
        }

        // Clear cart
        // cartItemRepository.deleteAll(cartItems); // TODO: Implement when CartItem
        // entity is properly set up

        return order;
    }

    public Map<String, Object> getCheckoutSummary(UUID cartId) {
        // Cart cart = cartRepository.findById(cartId).orElseThrow(); // TODO: Implement
        // when Cart entity is properly set up
        List<CartItem> cartItems = cartItemRepository.findAll().stream()
                .filter(item -> item.getCart().getId().equals(cartId))
                .toList();

        Map<String, Object> summary = new HashMap<>();
        summary.put("cartId", cartId);
        summary.put("itemCount", cartItems.size());
        summary.put("items", cartItems);

        // Calculate totals without promotion
        Map<String, Object> totals = calculateOrderTotals(cartId, null, BigDecimal.valueOf(30000));
        summary.put("totals", totals);

        return summary;
    }
}
