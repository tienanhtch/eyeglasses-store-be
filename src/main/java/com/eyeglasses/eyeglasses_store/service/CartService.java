package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.cart.Cart;
import com.eyeglasses.eyeglasses_store.entity.cart.CartItem;
import com.eyeglasses.eyeglasses_store.entity.catalog.ProductVariant;
import com.eyeglasses.eyeglasses_store.entity.catalog.ProductImage;
import com.eyeglasses.eyeglasses_store.entity.lens.LensPackage;
import com.eyeglasses.eyeglasses_store.entity.lens.Prescription;
import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.repository.cart.CartItemRepository;
import com.eyeglasses.eyeglasses_store.repository.cart.CartRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductVariantRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductImageRepository;
import com.eyeglasses.eyeglasses_store.repository.lens.LensPackageRepository;
import com.eyeglasses.eyeglasses_store.repository.lens.PrescriptionRepository;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AppUserRepository userRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final LensPackageRepository lensPackageRepository;
    private final PrescriptionRepository prescriptionRepository;

    public CartService(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            AppUserRepository userRepository,
            ProductVariantRepository variantRepository,
            ProductImageRepository imageRepository,
            LensPackageRepository lensPackageRepository,
            PrescriptionRepository prescriptionRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.variantRepository = variantRepository;
        this.imageRepository = imageRepository;
        this.lensPackageRepository = lensPackageRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional
    public Map<String, Object> getOrCreateCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            AppUser user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            Cart c = new Cart();
            c.setUser(user);
            return cartRepository.save(c);
        });
        return toCartPayload(cart);
    }

    @Transactional
    public Map<String, Object> addItem(UUID userId, UUID variantId, Integer qty, UUID lensPackageId,
            UUID prescriptionId, BigDecimal customPrice, String note) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            AppUser user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            Cart c = new Cart();
            c.setUser(user);
            return cartRepository.save(c);
        });
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Product variant not found with ID: " + variantId));
        LensPackage lensPackage = lensPackageId != null ? lensPackageRepository.findById(lensPackageId).orElse(null)
                : null;
        Prescription prescription = prescriptionId != null
                ? prescriptionRepository.findById(prescriptionId).orElse(null)
                : null;

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setVariant(variant);
        item.setQty(qty);
        item.setLensPackage(lensPackage);
        item.setPrescription(prescription);
        item.setCustomPrice(customPrice);
        item.setNote(note);
        cartItemRepository.save(item);
        return toCartPayload(cart);
    }

    @Transactional
    public Map<String, Object> updateItem(UUID itemId, Integer qty, UUID lensPackageId, BigDecimal customPrice,
            String note) {
        CartItem item = cartItemRepository.findById(itemId).orElseThrow();
        if (qty != null)
            item.setQty(qty);
        if (lensPackageId != null) {
            item.setLensPackage(lensPackageRepository.findById(lensPackageId).orElse(null));
        }
        if (customPrice != null)
            item.setCustomPrice(customPrice);
        if (note != null)
            item.setNote(note);
        cartItemRepository.save(item);
        return toCartPayload(item.getCart());
    }

    @Transactional
    public Map<String, Object> removeItem(UUID itemId) {
        CartItem item = cartItemRepository.findById(itemId).orElseThrow();
        Cart cart = item.getCart();
        cartItemRepository.delete(item);
        return toCartPayload(cart);
    }

    private Map<String, Object> toCartPayload(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        BigDecimal subtotal = BigDecimal.ZERO;
        List<Map<String, Object>> payloadItems = new ArrayList<>();
        for (CartItem it : items) {
            BigDecimal unitPrice = it.getVariant().getSalePrice() != null ? it.getVariant().getSalePrice()
                    : it.getVariant().getRetailPrice();
            if (it.getCustomPrice() != null)
                unitPrice = it.getCustomPrice();
            BigDecimal lensPrice = it.getLensPackage() != null ? Optional.ofNullable(it.getLensPackage().getSalePrice())
                    .orElse(it.getLensPackage().getRetailPrice()) : BigDecimal.ZERO;
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(it.getQty())).add(lensPrice);
            subtotal = subtotal.add(lineTotal);
            Map<String, Object> pi = new LinkedHashMap<>();
            pi.put("id", it.getId());
            pi.put("qty", it.getQty());
            pi.put("note", it.getNote());
            String productName = null;
            String productSlug = null;
            String productImageUrl = null;
            try {
                if (it.getVariant() != null && it.getVariant().getProduct() != null) {
                    productName = it.getVariant().getProduct().getName();
                    productSlug = it.getVariant().getProduct().getSlug();
                    java.util.List<ProductImage> imgs = imageRepository
                            .findByProductIdOrderBySortOrderAsc(it.getVariant().getProduct().getId());
                    if (!imgs.isEmpty()) {
                        productImageUrl = imgs.get(0).getUrl();
                    }
                }
            } catch (Exception ignored) {
            }

            Map<String, Object> variantPayload = new LinkedHashMap<>();
            variantPayload.put("id", it.getVariant().getId());
            variantPayload.put("sku", it.getVariant().getSku());
            variantPayload.put("color", it.getVariant().getColor());
            variantPayload.put("sizeMm", it.getVariant().getSizeMm());
            variantPayload.put("bridgeMm", it.getVariant().getBridgeMm());
            variantPayload.put("templeMm", it.getVariant().getTempleMm());
            variantPayload.put("retailPrice", it.getVariant().getRetailPrice());
            variantPayload.put("salePrice", it.getVariant().getSalePrice());
            variantPayload.put("productName", productName);
            variantPayload.put("productSlug", productSlug);
            variantPayload.put("productImageUrl", productImageUrl);
            pi.put("variant", variantPayload);
            if (it.getLensPackage() != null) {
                pi.put("lensPackage", Map.of(
                        "id", it.getLensPackage().getId(),
                        "code", it.getLensPackage().getCode(),
                        "name", it.getLensPackage().getName(),
                        "retailPrice", it.getLensPackage().getRetailPrice(),
                        "salePrice", it.getLensPackage().getSalePrice()));
            }
            pi.put("lineTotal", lineTotal);
            payloadItems.add(pi);
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", cart.getId());
        m.put("userId", cart.getUser() != null ? cart.getUser().getId() : null);
        m.put("items", payloadItems);
        m.put("subtotal", subtotal);
        m.put("createdAt", cart.getCreatedAt());
        m.put("updatedAt", cart.getUpdatedAt());
        return m;
    }
}
