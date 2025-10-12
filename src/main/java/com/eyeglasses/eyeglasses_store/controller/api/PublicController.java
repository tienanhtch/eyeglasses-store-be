package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.catalog.Category;
import com.eyeglasses.eyeglasses_store.service.PublicCatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Public controller for endpoints that don't require authentication
 */
@RestController
@RequestMapping(ApiConstants.PUBLIC_BASE)
public class PublicController {

    private final PublicCatalogService publicCatalogService;

    public PublicController(PublicCatalogService publicCatalogService) {
        this.publicCatalogService = publicCatalogService;
    }

    /**
     * Get public product categories
     * GET /api/v1/public/categories
     */
    @GetMapping(ApiConstants.PUBLIC_CATEGORIES)
    public ResponseEntity<Map<String, Object>> getCategories() {
        List<Category> categories = publicCatalogService.getAllCategories();
        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories.stream().map(c -> Map.of(
                "id", c.getId(),
                "slug", c.getSlug(),
                "name", c.getName(),
                "description", c.getDescription(),
                "parentId", c.getParent() != null ? c.getParent().getId() : null,
                "sortOrder", c.getSortOrder(),
                "isActive", c.isActive())).toList());
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * Get public products with full nested data
     * GET /api/v1/public/products
     */
    @GetMapping(ApiConstants.PUBLIC_PRODUCTS)
    public ResponseEntity<Map<String, Object>> getPublicProducts() {
        Map<String, Object> response = new HashMap<>();
        response.put("products", publicCatalogService.getPublishedProductsFull());
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * Get product detail by slug
     * GET /api/v1/public/products/{slug}
     */
    @GetMapping(ApiConstants.PUBLIC_PRODUCTS + "/{slug}")
    public ResponseEntity<?> getProductDetail(@PathVariable("slug") String slug) {
        Optional<Map<String, Object>> data = publicCatalogService.getProductDetailBySlug(slug);
        return data.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Search & filter products with pagination
     * GET /api/v1/public/products/search
     */
    @GetMapping(ApiConstants.PUBLIC_PRODUCTS + "/search")
    public ResponseEntity<Map<String, Object>> search(
            @org.springframework.web.bind.annotation.RequestParam(value = "q", required = false) String q,
            @org.springframework.web.bind.annotation.RequestParam(value = "category", required = false) String categorySlug,
            @org.springframework.web.bind.annotation.RequestParam(value = "material", required = false) String material,
            @org.springframework.web.bind.annotation.RequestParam(value = "frameShape", required = false) String frameShape,
            @org.springframework.web.bind.annotation.RequestParam(value = "minPrice", required = false) java.math.BigDecimal minPrice,
            @org.springframework.web.bind.annotation.RequestParam(value = "maxPrice", required = false) java.math.BigDecimal maxPrice,
            @org.springframework.web.bind.annotation.RequestParam(value = "page", required = false) Integer page,
            @org.springframework.web.bind.annotation.RequestParam(value = "size", required = false) Integer size,
            @org.springframework.web.bind.annotation.RequestParam(value = "sort", required = false) String sort,
            @org.springframework.web.bind.annotation.RequestParam(value = "direction", required = false) String direction) {
        return ResponseEntity.ok(publicCatalogService.searchProducts(q, categorySlug, material, frameShape, page, size,
                sort, direction, minPrice, maxPrice));
    }

    /**
     * Suggest product names
     * GET /api/v1/public/products/suggest?q=
     */
    @GetMapping(ApiConstants.PUBLIC_PRODUCTS + "/suggest")
    public ResponseEntity<List<String>> suggest(
            @org.springframework.web.bind.annotation.RequestParam(value = "q", required = false) String q,
            @org.springframework.web.bind.annotation.RequestParam(value = "size", required = false) Integer size) {
        return ResponseEntity.ok(publicCatalogService.suggestProductNames(q, size));
    }

    /**
     * Get store information
     * GET /api/v1/public/store-info
     */
    @GetMapping("/store-info")
    public ResponseEntity<Map<String, Object>> getStoreInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("storeName", "Eyeglasses Store");
        response.put("description", "Premium eyewear for everyone");
        response.put("contact", Map.of(
                "phone", "+84 123 456 789",
                "email", "info@eyeglassesstore.com",
                "address", "123 Main Street, Ho Chi Minh City"));
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
