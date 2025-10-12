package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.catalog.Category;
import com.eyeglasses.eyeglasses_store.entity.catalog.Product;
import com.eyeglasses.eyeglasses_store.entity.catalog.ProductImage;
import com.eyeglasses.eyeglasses_store.entity.catalog.ProductVariant;
import com.eyeglasses.eyeglasses_store.service.AdminCatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE)
public class AdminCatalogController {

    private final AdminCatalogService adminCatalogService;

    public AdminCatalogController(AdminCatalogService adminCatalogService) {
        this.adminCatalogService = adminCatalogService;
    }

    // Categories
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> listCategories() {
        return ResponseEntity.ok(adminCatalogService.listCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(adminCatalogService.createCategory(body));
    }

    @PatchMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") UUID id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(adminCatalogService.updateCategory(id, body));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable("id") UUID id) {
        adminCatalogService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    // Products
    @GetMapping("/products")
    public ResponseEntity<List<Product>> listProducts() {
        return ResponseEntity.ok(adminCatalogService.listProducts());
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(adminCatalogService.createProduct(body));
    }

    @PatchMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") UUID id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(adminCatalogService.updateProduct(id, body));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable("id") UUID id) {
        adminCatalogService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    // Variants
    @PostMapping("/products/{productId}/variants")
    public ResponseEntity<ProductVariant> addVariant(@PathVariable("productId") UUID productId,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(adminCatalogService.addVariant(productId, body));
    }

    @PatchMapping("/variants/{variantId}")
    public ResponseEntity<ProductVariant> updateVariant(@PathVariable("variantId") UUID variantId,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(adminCatalogService.updateVariant(variantId, body));
    }

    @DeleteMapping("/variants/{variantId}")
    public ResponseEntity<Map<String, String>> deleteVariant(@PathVariable("variantId") UUID variantId) {
        adminCatalogService.deleteVariant(variantId);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    // Images
    @PostMapping("/products/{productId}/images")
    public ResponseEntity<ProductImage> addImage(@PathVariable("productId") UUID productId,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(adminCatalogService.addImage(productId, body));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable("imageId") UUID imageId) {
        adminCatalogService.deleteImage(imageId);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    // Lens packages
    @GetMapping("/lens-packages")
    public ResponseEntity<List<Map<String, Object>>> listLensPackages() {
        return ResponseEntity.ok(adminCatalogService.listLensPackages());
    }

    @PostMapping("/lens-packages")
    public ResponseEntity<com.eyeglasses.eyeglasses_store.entity.lens.LensPackage> createLensPackage(
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(adminCatalogService.createLensPackage(body));
    }

    @PatchMapping("/lens-packages/{id}")
    public ResponseEntity<com.eyeglasses.eyeglasses_store.entity.lens.LensPackage> updateLensPackage(
            @PathVariable("id") UUID id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(adminCatalogService.updateLensPackage(id, body));
    }

    @DeleteMapping("/lens-packages/{id}")
    public ResponseEntity<Map<String, String>> deleteLensPackage(@PathVariable("id") UUID id) {
        adminCatalogService.deleteLensPackage(id);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
