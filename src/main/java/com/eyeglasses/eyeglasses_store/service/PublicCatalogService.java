package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.catalog.Category;
import com.eyeglasses.eyeglasses_store.entity.catalog.Product;
import com.eyeglasses.eyeglasses_store.entity.catalog.ProductImage;
import com.eyeglasses.eyeglasses_store.entity.catalog.ProductVariant;
import com.eyeglasses.eyeglasses_store.repository.catalog.CategoryRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductImageRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductVariantRepository;
import com.eyeglasses.eyeglasses_store.repository.analytics.SearchLogRepository;
import com.eyeglasses.eyeglasses_store.repository.inventory.InventoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PublicCatalogService {

        private final CategoryRepository categoryRepository;
        private final ProductRepository productRepository;
        private final ProductVariantRepository variantRepository;
        private final ProductImageRepository imageRepository;
        private final SearchLogRepository searchLogRepository;
        private final InventoryRepository inventoryRepository;

        public PublicCatalogService(CategoryRepository categoryRepository,
                        ProductRepository productRepository,
                        ProductVariantRepository variantRepository,
                        ProductImageRepository imageRepository,
                        SearchLogRepository searchLogRepository,
                        InventoryRepository inventoryRepository) {
                this.categoryRepository = categoryRepository;
                this.productRepository = productRepository;
                this.variantRepository = variantRepository;
                this.imageRepository = imageRepository;
                this.searchLogRepository = searchLogRepository;
                this.inventoryRepository = inventoryRepository;
        }

        @Transactional(readOnly = true)
        public List<Category> getAllCategories() {
                return categoryRepository.findAll();
        }

        @Transactional(readOnly = true)
        public List<Map<String, Object>> getPublishedProductsFull() {
                List<Product> products = productRepository.findAllByPublishedTrue();
                Map<UUID, List<ProductVariant>> variantsByProduct = variantRepository.findAll()
                                .stream().collect(Collectors.groupingBy(v -> v.getProduct().getId()));
                Map<UUID, List<ProductImage>> imagesByProduct = imageRepository.findAll()
                                .stream().collect(Collectors.groupingBy(i -> i.getProduct().getId()));

                return products.stream().map(p -> {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("id", p.getId());
                        m.put("slug", p.getSlug());
                        m.put("name", p.getName());
                        m.put("description", p.getDescription());
                        m.put("brand", p.getBrand());
                        m.put("material", p.getMaterial());
                        m.put("frameShape", p.getFrameShape());
                        m.put("seoTitle", p.getSeoTitle());
                        m.put("seoDescription", p.getSeoDescription());
                        m.put("published", p.isPublished());
                        m.put("categories", p.getCategories().stream().map(c -> Map.of(
                                        "id", c.getId(),
                                        "slug", c.getSlug(),
                                        "name", c.getName())).toList());

                        List<ProductVariant> variants = variantsByProduct.getOrDefault(p.getId(), List.of());
                        m.put("variants", variants.stream().map(v -> {
                                Map<String, Object> vm = new LinkedHashMap<>();
                                vm.put("id", v.getId());
                                vm.put("sku", v.getSku());
                                vm.put("color", v.getColor());
                                vm.put("sizeMm", v.getSizeMm());
                                vm.put("bridgeMm", v.getBridgeMm());
                                vm.put("templeMm", v.getTempleMm());
                                vm.put("costPrice", v.getCostPrice());
                                vm.put("retailPrice", v.getRetailPrice());
                                vm.put("salePrice", v.getSalePrice());
                                vm.put("active", v.isActive());
                                return vm;
                        }).toList());

                        List<ProductImage> images = imagesByProduct.getOrDefault(p.getId(), List.of());
                        m.put("images",
                                        images.stream()
                                                        .sorted(Comparator.comparingInt(i -> Optional
                                                                        .ofNullable(i.getSortOrder()).orElse(0)))
                                                        .map(i -> {
                                                                Map<String, Object> im = new LinkedHashMap<>();
                                                                im.put("id", i.getId());
                                                                im.put("url", i.getUrl());
                                                                im.put("alt", i.getAlt());
                                                                im.put("sortOrder", i.getSortOrder());
                                                                return im;
                                                        })
                                                        .toList());

                        return m;
                }).toList();
        }

        @Transactional(readOnly = true)
        public Optional<Map<String, Object>> getProductDetailBySlug(String slug) {
                return productRepository.findBySlug(slug).map(p -> {
                        Map<UUID, List<ProductVariant>> variantsByProduct = variantRepository.findAll()
                                        .stream().collect(Collectors.groupingBy(v -> v.getProduct().getId()));
                        Map<UUID, List<ProductImage>> imagesByProduct = imageRepository.findAll()
                                        .stream().collect(Collectors.groupingBy(i -> i.getProduct().getId()));
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("id", p.getId());
                        m.put("slug", p.getSlug());
                        m.put("name", p.getName());
                        m.put("description", p.getDescription());
                        m.put("brand", p.getBrand());
                        m.put("material", p.getMaterial());
                        m.put("frameShape", p.getFrameShape());
                        m.put("seoTitle", p.getSeoTitle());
                        m.put("seoDescription", p.getSeoDescription());
                        m.put("published", p.isPublished());
                        m.put("isNew", p.isNew());
                        m.put("isBestSeller", p.isBestSeller());
                        m.put("categories", p.getCategories().stream().map(c -> Map.of(
                                        "id", c.getId(),
                                        "slug", c.getSlug(),
                                        "name", c.getName())).toList());
                        List<ProductVariant> variants = variantsByProduct.getOrDefault(p.getId(), List.of());
                        m.put("variants", variants.stream().map(v -> {
                                Map<String, Object> vm = new LinkedHashMap<>();
                                vm.put("id", v.getId());
                                vm.put("sku", v.getSku());
                                vm.put("color", v.getColor());
                                vm.put("sizeMm", v.getSizeMm());
                                vm.put("bridgeMm", v.getBridgeMm());
                                vm.put("templeMm", v.getTempleMm());
                                vm.put("costPrice", v.getCostPrice());
                                vm.put("retailPrice", v.getRetailPrice());
                                vm.put("salePrice", v.getSalePrice());
                                vm.put("active", v.isActive());
                                return vm;
                        }).toList());
                        List<ProductImage> images = imagesByProduct.getOrDefault(p.getId(), List.of());
                        m.put("images",
                                        images.stream()
                                                        .sorted(Comparator.comparingInt(i -> Optional
                                                                        .ofNullable(i.getSortOrder()).orElse(0)))
                                                        .map(i -> {
                                                                Map<String, Object> im = new LinkedHashMap<>();
                                                                im.put("id", i.getId());
                                                                im.put("url", i.getUrl());
                                                                im.put("alt", i.getAlt());
                                                                im.put("sortOrder", i.getSortOrder());
                                                                return im;
                                                        })
                                                        .toList());
                        return m;
                });
        }

        @Transactional(readOnly = true)
        public Map<String, Object> searchProducts(String q, String categorySlug, String material, String frameShape,
                        Boolean isNew, Boolean isBestSeller,
                        Integer page, Integer size, String sort, String direction,
                        java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice) {
                int p = page != null && page >= 0 ? page : 0;
                int s = size != null && size > 0 ? size : 20;
                Sort sortSpec = Sort.by(
                                (direction != null && direction.equalsIgnoreCase("desc")) ? Sort.Direction.DESC
                                                : Sort.Direction.ASC,
                                (sort != null && !sort.isBlank()) ? sort : "name");
                Pageable pageable = PageRequest.of(p, s, sortSpec);
                Page<Product> pageData = productRepository.search(
                                q != null && !q.isBlank() ? q.trim() : null,
                                categorySlug,
                                material,
                                frameShape,
                                isNew,
                                isBestSeller,
                                minPrice,
                                maxPrice,
                                pageable);

                // log search
                com.eyeglasses.eyeglasses_store.entity.analytics.SearchLog log = new com.eyeglasses.eyeglasses_store.entity.analytics.SearchLog();
                log.setQueryText(q);
                log.setParamsJson("{" +
                                "\"category\":\"" + (categorySlug != null ? categorySlug : "") + "\"," +
                                "\"material\":\"" + (material != null ? material : "") + "\"," +
                                "\"frameShape\":\"" + (frameShape != null ? frameShape : "") + "\"," +
                                "\"isNew\":" + (isNew != null ? isNew : "null") + "," +
                                "\"isBestSeller\":" + (isBestSeller != null ? isBestSeller : "null") + "," +
                                "\"minPrice\":" + (minPrice != null ? minPrice : "null") + "," +
                                "\"maxPrice\":" + (maxPrice != null ? maxPrice : "null") +
                                "}");
                searchLogRepository.save(log);

                List<Map<String, Object>> items = pageData.getContent().stream().map(prod -> {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("id", prod.getId());
                        m.put("slug", prod.getSlug());
                        m.put("name", prod.getName());
                        m.put("brand", prod.getBrand());
                        m.put("material", prod.getMaterial());
                        m.put("frameShape", prod.getFrameShape());
                        m.put("isNew", prod.isNew());
                        m.put("isBestSeller", prod.isBestSeller());

                        // Get variants and calculate price range
                        List<ProductVariant> variants = variantRepository.findByProductId(prod.getId());
                        BigDecimal minVariantPrice = null;
                        BigDecimal maxVariantPrice = null;
                        int totalStock = 0;
                        UUID firstVariantId = null;
                        List<String> colors = new java.util.ArrayList<>();

                        for (ProductVariant v : variants) {
                                if (firstVariantId == null) {
                                        firstVariantId = v.getId();
                                }
                                if (v.getColor() != null && !colors.contains(v.getColor())) {
                                        colors.add(v.getColor());
                                }
                                if (v.getRetailPrice() != null) {
                                        if (minVariantPrice == null
                                                        || v.getRetailPrice().compareTo(minVariantPrice) < 0) {
                                                minVariantPrice = v.getRetailPrice();
                                        }
                                        if (maxVariantPrice == null
                                                        || v.getRetailPrice().compareTo(maxVariantPrice) > 0) {
                                                maxVariantPrice = v.getRetailPrice();
                                        }
                                }
                                Integer available = inventoryRepository.totalAvailableByVariant(v.getId());
                                totalStock += (available != null ? available : 0);
                        }

                        m.put("price", minVariantPrice);
                        m.put("maxPrice", maxVariantPrice);
                        m.put("inStock", totalStock > 0);
                        m.put("variantId", firstVariantId);
                        m.put("colors", colors);

                        // cover image: ảnh sort_order nhỏ nhất
                        List<ProductImage> images = imageRepository.findByProductIdOrderBySortOrderAsc(prod.getId());
                        if (!images.isEmpty()) {
                                Map<String, Object> thumbnail = new LinkedHashMap<>();
                                thumbnail.put("id", images.get(0).getId());
                                thumbnail.put("url", images.get(0).getUrl());
                                thumbnail.put("alt", images.get(0).getAlt());
                                m.put("thumbnail", thumbnail);
                        }
                        return m;
                }).toList();

                Map<String, Object> resp = new LinkedHashMap<>();
                resp.put("items", items);
                resp.put("page", pageData.getNumber());
                resp.put("size", pageData.getSize());
                resp.put("totalElements", pageData.getTotalElements());
                resp.put("totalPages", pageData.getTotalPages());
                return resp;
        }

        @Transactional(readOnly = true)
        public List<String> suggestProductNames(String q, Integer size) {
                int s = size != null && size > 0 ? size : 10;
                return productRepository.suggestNames(q != null && !q.isBlank() ? q.trim() : null,
                                PageRequest.of(0, s));
        }
}
