package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.catalog.Category;
import com.eyeglasses.eyeglasses_store.entity.catalog.Product;
import com.eyeglasses.eyeglasses_store.entity.catalog.ProductVariant;
import com.eyeglasses.eyeglasses_store.entity.catalog.ProductImage;
import com.eyeglasses.eyeglasses_store.entity.catalog.VariantImage;
import com.eyeglasses.eyeglasses_store.repository.catalog.CategoryRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductVariantRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductImageRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.VariantImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminBulkService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository productImageRepository;
    private final VariantImageRepository variantImageRepository;
    private final CategoryRepository categoryRepository;

    public AdminBulkService(ProductRepository productRepository,
            ProductVariantRepository variantRepository,
            ProductImageRepository productImageRepository,
            VariantImageRepository variantImageRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.productImageRepository = productImageRepository;
        this.categoryRepository = categoryRepository;
        this.variantImageRepository = variantImageRepository;
    }

    @Transactional
    public Product createProductWithVariants(Map<String, Object> productData) {
        // Create product
        Product product = new Product();
        product.setSlug((String) productData.get("slug"));
        product.setName((String) productData.get("name"));
        product.setDescription((String) productData.get("description"));
        product.setBrand((String) productData.get("brand"));
        product.setMaterial((String) productData.get("material"));
        product.setFrameShape((String) productData.get("frameShape"));
        product.setSeoTitle((String) productData.get("seoTitle"));
        product.setSeoDescription((String) productData.get("seoDescription"));
        product.setPublished((Boolean) productData.getOrDefault("published", true));

        // Set categories if provided
        if (productData.get("categoryIds") != null) {
            @SuppressWarnings("unchecked")
            List<String> categoryIdStrings = (List<String>) productData.get("categoryIds");
            for (String categoryIdString : categoryIdStrings) {
                UUID categoryId = UUID.fromString(categoryIdString);
                Category category = categoryRepository.findById(categoryId).orElseThrow();
                product.getCategories().add(category);
            }
        }

        product = productRepository.save(product);

        // Create variants
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> variants = (List<Map<String, Object>>) productData.get("variants");
        if (variants != null) {
            for (Map<String, Object> variantData : variants) {
                ProductVariant variant = new ProductVariant();
                variant.setProduct(product);
                variant.setSku((String) variantData.get("sku"));
                variant.setColor((String) variantData.get("color"));
                variant.setSizeMm((Integer) variantData.get("sizeMm"));
                variant.setBridgeMm((Integer) variantData.get("bridgeMm"));
                variant.setTempleMm((Integer) variantData.get("templeMm"));
                variant.setCostPrice(new BigDecimal(variantData.get("costPrice").toString()));
                variant.setRetailPrice(new BigDecimal(variantData.get("retailPrice").toString()));
                if (variantData.get("salePrice") != null) {
                    variant.setSalePrice(new BigDecimal(variantData.get("salePrice").toString()));
                }
                variant.setActive((Boolean) variantData.getOrDefault("active", true));
                variant = variantRepository.save(variant);

                // Create variant images
                @SuppressWarnings("unchecked")
                List<String> imageUrls = (List<String>) variantData.get("imageUrls");
                if (imageUrls != null) {
                    for (int i = 0; i < imageUrls.size(); i++) {
                        VariantImage variantImage = new VariantImage();
                        variantImage.setVariant(variant);
                        variantImage.setUrl(imageUrls.get(i));
                        variantImage.setSortOrder(i);
                        variantImageRepository.save(variantImage);
                    }
                }
            }
        }

        // Create product images
        @SuppressWarnings("unchecked")
        List<String> productImageUrls = (List<String>) productData.get("imageUrls");
        if (productImageUrls != null) {
            for (int i = 0; i < productImageUrls.size(); i++) {
                ProductImage productImage = new ProductImage();
                productImage.setProduct(product);
                productImage.setUrl(productImageUrls.get(i));
                productImage.setSortOrder(i);
                productImageRepository.save(productImage);
            }
        }

        return product;
    }

    @Transactional
    public void updateProductStatus(UUID productId, boolean published) {
        Product product = productRepository.findById(productId).orElseThrow();
        product.setPublished(published);
        productRepository.save(product);
    }

    @Transactional
    public void updateVariantStatus(UUID variantId, boolean active) {
        ProductVariant variant = variantRepository.findById(variantId).orElseThrow();
        variant.setActive(active);
        variantRepository.save(variant);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        productRepository.delete(product);
    }

    @Transactional
    public void deleteVariant(UUID variantId) {
        ProductVariant variant = variantRepository.findById(variantId).orElseThrow();
        variantRepository.delete(variant);
    }
}
