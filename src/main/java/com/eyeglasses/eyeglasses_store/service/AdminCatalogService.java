package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.catalog.*;
import com.eyeglasses.eyeglasses_store.entity.inventory.Inventory;
import com.eyeglasses.eyeglasses_store.repository.catalog.CategoryRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductImageRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductVariantRepository;
import com.eyeglasses.eyeglasses_store.repository.inventory.InventoryRepository;
import com.eyeglasses.eyeglasses_store.repository.lens.LensPackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AdminCatalogService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final LensPackageRepository lensPackageRepository;
    private final InventoryRepository inventoryRepository;

    public AdminCatalogService(CategoryRepository categoryRepository,
            ProductRepository productRepository,
            ProductVariantRepository variantRepository,
            ProductImageRepository imageRepository,
            LensPackageRepository lensPackageRepository,
            InventoryRepository inventoryRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.imageRepository = imageRepository;
        this.lensPackageRepository = lensPackageRepository;
        this.inventoryRepository = inventoryRepository;
    }

    // Categories
    @Transactional(readOnly = true)
    public List<Category> listCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Category createCategory(Map<String, Object> body) {
        Category c = new Category();
        if (body.get("parentId") != null) {
            UUID parentId = UUID.fromString(body.get("parentId").toString());
            categoryRepository.findById(parentId).ifPresent(c::setParent);
        }
        c.setSlug((String) body.get("slug"));
        c.setName((String) body.get("name"));
        c.setDescription((String) body.get("description"));
        if (body.get("sortOrder") != null)
            c.setSortOrder(((Number) body.get("sortOrder")).intValue());
        if (body.get("isActive") != null)
            c.setActive(Boolean.TRUE.equals(body.get("isActive")));
        return categoryRepository.save(c);
    }

    @Transactional
    public Category updateCategory(UUID id, Map<String, Object> body) {
        Category c = categoryRepository.findById(id).orElseThrow();
        if (body.containsKey("parentId")) {
            Object val = body.get("parentId");
            if (val == null)
                c.setParent(null);
            else
                categoryRepository.findById(UUID.fromString(val.toString())).ifPresent(c::setParent);
        }
        if (body.get("slug") != null)
            c.setSlug((String) body.get("slug"));
        if (body.get("name") != null)
            c.setName((String) body.get("name"));
        if (body.get("description") != null)
            c.setDescription((String) body.get("description"));
        if (body.get("sortOrder") != null)
            c.setSortOrder(((Number) body.get("sortOrder")).intValue());
        if (body.get("isActive") != null)
            c.setActive(Boolean.TRUE.equals(body.get("isActive")));
        return categoryRepository.save(c);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }

    // Products
    @Transactional(readOnly = true)
    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public Product createProduct(Map<String, Object> body) {
        Product p = new Product();
        p.setSlug((String) body.get("slug"));
        p.setName((String) body.get("name"));
        p.setDescription((String) body.get("description"));
        p.setBrand((String) body.get("brand"));
        p.setMaterial((String) body.get("material"));
        p.setFrameShape((String) body.get("frameShape"));
        p.setSeoTitle((String) body.get("seoTitle"));
        p.setSeoDescription((String) body.get("seoDescription"));
        if (body.get("isPublished") != null)
            p.setPublished(Boolean.TRUE.equals(body.get("isPublished")));
        // categories by ids
        if (body.get("categoryIds") instanceof Collection<?> ids) {
            for (Object id : ids) {
                categoryRepository.findById(UUID.fromString(id.toString())).ifPresent(p.getCategories()::add);
            }
        }
        return productRepository.save(p);
    }

    @Transactional
    public Product updateProduct(UUID id, Map<String, Object> body) {
        Product p = productRepository.findById(id).orElseThrow();
        if (body.get("slug") != null)
            p.setSlug((String) body.get("slug"));
        if (body.get("name") != null)
            p.setName((String) body.get("name"));
        if (body.get("description") != null)
            p.setDescription((String) body.get("description"));
        if (body.get("brand") != null)
            p.setBrand((String) body.get("brand"));
        if (body.get("material") != null)
            p.setMaterial((String) body.get("material"));
        if (body.get("frameShape") != null)
            p.setFrameShape((String) body.get("frameShape"));
        if (body.get("seoTitle") != null)
            p.setSeoTitle((String) body.get("seoTitle"));
        if (body.get("seoDescription") != null)
            p.setSeoDescription((String) body.get("seoDescription"));
        if (body.get("isPublished") != null)
            p.setPublished(Boolean.TRUE.equals(body.get("isPublished")));
        if (body.get("categoryIds") instanceof Collection<?> ids) {
            p.getCategories().clear();
            for (Object cid : ids) {
                categoryRepository.findById(UUID.fromString(cid.toString())).ifPresent(p.getCategories()::add);
            }
        }
        return productRepository.save(p);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        // Xóa đúng thứ tự FK: inventory → variants → images → product
        List<ProductVariant> variants = variantRepository.findByProductId(id);
        for (ProductVariant v : variants) {
            // Xóa inventory của variant này
            List<Inventory> invList = inventoryRepository.findByVariantId(v.getId());
            inventoryRepository.deleteAll(invList);
        }
        inventoryRepository.flush();
        // Xóa variants
        variantRepository.deleteAll(variants);
        variantRepository.flush();
        // Xóa images
        List<ProductImage> images = imageRepository.findByProductIdOrderBySortOrderAsc(id);
        imageRepository.deleteAll(images);
        imageRepository.flush();
        // Cuối cùng xóa product
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getProductDetail(UUID id) {
        Product p = productRepository.findById(id).orElseThrow();
        List<ProductVariant> variants = variantRepository.findByProductId(id);
        List<ProductImage> images = imageRepository.findByProductIdOrderBySortOrderAsc(id);

        Map<String, Object> m = new java.util.LinkedHashMap<>();
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
                "id", c.getId(), "name", c.getName(), "slug", c.getSlug())).toList());
        m.put("variants", variants.stream().map(v -> {
            Map<String, Object> vm = new java.util.LinkedHashMap<>();
            vm.put("id", v.getId());
            vm.put("sku", v.getSku());
            vm.put("color", v.getColor());
            vm.put("sizeMm", v.getSizeMm());
            vm.put("bridgeMm", v.getBridgeMm());
            vm.put("templeMm", v.getTempleMm());
            vm.put("retailPrice", v.getRetailPrice());
            vm.put("salePrice", v.getSalePrice());
            vm.put("isActive", v.isActive());
            // Inventory per store cho admin
            List<Inventory> invList = inventoryRepository.findByVariantId(v.getId());
            Map<String, Integer> stockByStore = new LinkedHashMap<>();
            for (Inventory inv : invList) {
                stockByStore.put(inv.getStore().getId().toString(), inv.getOnHand());
            }
            vm.put("stockByStore", stockByStore);
            return vm;
        }).toList());
        m.put("images", images.stream().map(i -> {
            Map<String, Object> im = new java.util.LinkedHashMap<>();
            im.put("id", i.getId());
            im.put("url", i.getUrl());
            im.put("alt", i.getAlt());
            im.put("sortOrder", i.getSortOrder());
            return im;
        }).toList());
        return m;
    }

    // Variants
    @Transactional
    public ProductVariant addVariant(UUID productId, Map<String, Object> body) {
        Product product = productRepository.findById(productId).orElseThrow();
        ProductVariant v = new ProductVariant();
        v.setProduct(product);
        v.setSku((String) body.get("sku"));
        v.setColor((String) body.get("color"));
        v.setSizeMm(asInteger(body.get("sizeMm")));
        v.setBridgeMm(asInteger(body.get("bridgeMm")));
        v.setTempleMm(asInteger(body.get("templeMm")));
        v.setCostPrice(asDecimal(body.get("costPrice")));
        v.setRetailPrice(asDecimal(body.get("retailPrice")));
        v.setSalePrice(asDecimal(body.get("salePrice")));
        if (body.get("isActive") != null)
            v.setActive(Boolean.TRUE.equals(body.get("isActive")));
        return variantRepository.save(v);
    }

    @Transactional
    public ProductVariant updateVariant(UUID variantId, Map<String, Object> body) {
        ProductVariant v = variantRepository.findById(variantId).orElseThrow();
        if (body.get("sku") != null)
            v.setSku((String) body.get("sku"));
        if (body.get("color") != null)
            v.setColor((String) body.get("color"));
        if (body.get("sizeMm") != null)
            v.setSizeMm(asInteger(body.get("sizeMm")));
        if (body.get("bridgeMm") != null)
            v.setBridgeMm(asInteger(body.get("bridgeMm")));
        if (body.get("templeMm") != null)
            v.setTempleMm(asInteger(body.get("templeMm")));
        if (body.get("costPrice") != null)
            v.setCostPrice(asDecimal(body.get("costPrice")));
        if (body.get("retailPrice") != null)
            v.setRetailPrice(asDecimal(body.get("retailPrice")));
        if (body.get("salePrice") != null)
            v.setSalePrice(asDecimal(body.get("salePrice")));
        if (body.get("isActive") != null)
            v.setActive(Boolean.TRUE.equals(body.get("isActive")));
        return variantRepository.save(v);
    }

    @Transactional
    public void deleteVariant(UUID variantId) {
        // Xóa inventory trước khi xóa variant
        List<Inventory> invList = inventoryRepository.findByVariantId(variantId);
        inventoryRepository.deleteAll(invList);
        inventoryRepository.flush();
        variantRepository.deleteById(variantId);
    }

    // Images
    @Transactional
    public ProductImage addImage(UUID productId, Map<String, Object> body) {
        Product product = productRepository.findById(productId).orElseThrow();
        ProductImage img = new ProductImage();
        img.setProduct(product);
        img.setUrl((String) body.get("url"));
        img.setAlt((String) body.get("alt"));
        if (body.get("sortOrder") != null)
            img.setSortOrder(((Number) body.get("sortOrder")).intValue());
        return imageRepository.save(img);
    }

    @Transactional
    public void deleteImage(UUID imageId) {
        imageRepository.deleteById(imageId);
    }

    // Lens packages
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listLensPackages() {
        return new LensService(lensPackageRepository, null, null).listLensPackages();
    }

    @Transactional
    public com.eyeglasses.eyeglasses_store.entity.lens.LensPackage createLensPackage(Map<String, Object> body) {
        com.eyeglasses.eyeglasses_store.entity.lens.LensPackage lp = new com.eyeglasses.eyeglasses_store.entity.lens.LensPackage();
        lp.setCode((String) body.get("code"));
        lp.setName((String) body.get("name"));
        lp.setRefractiveIdx(asDecimal(body.get("refractiveIdx")));
        lp.setFeatures((String) body.get("features"));
        lp.setMinSph(asDecimal(body.get("minSph")));
        lp.setMaxSph(asDecimal(body.get("maxSph")));
        lp.setMinCyl(asDecimal(body.get("minCyl")));
        lp.setMaxCyl(asDecimal(body.get("maxCyl")));
        lp.setRetailPrice(asDecimal(body.get("retailPrice")));
        lp.setSalePrice(asDecimal(body.get("salePrice")));
        if (body.get("isActive") != null)
            lp.setActive(Boolean.TRUE.equals(body.get("isActive")));
        return lensPackageRepository.save(lp);
    }

    @Transactional
    public com.eyeglasses.eyeglasses_store.entity.lens.LensPackage updateLensPackage(UUID id,
            Map<String, Object> body) {
        com.eyeglasses.eyeglasses_store.entity.lens.LensPackage lp = lensPackageRepository.findById(id).orElseThrow();
        if (body.get("code") != null)
            lp.setCode((String) body.get("code"));
        if (body.get("name") != null)
            lp.setName((String) body.get("name"));
        if (body.get("refractiveIdx") != null)
            lp.setRefractiveIdx(asDecimal(body.get("refractiveIdx")));
        if (body.get("features") != null)
            lp.setFeatures((String) body.get("features"));
        if (body.get("minSph") != null)
            lp.setMinSph(asDecimal(body.get("minSph")));
        if (body.get("maxSph") != null)
            lp.setMaxSph(asDecimal(body.get("maxSph")));
        if (body.get("minCyl") != null)
            lp.setMinCyl(asDecimal(body.get("minCyl")));
        if (body.get("maxCyl") != null)
            lp.setMaxCyl(asDecimal(body.get("maxCyl")));
        if (body.get("retailPrice") != null)
            lp.setRetailPrice(asDecimal(body.get("retailPrice")));
        if (body.get("salePrice") != null)
            lp.setSalePrice(asDecimal(body.get("salePrice")));
        if (body.get("isActive") != null)
            lp.setActive(Boolean.TRUE.equals(body.get("isActive")));
        return lensPackageRepository.save(lp);
    }

    @Transactional
    public void deleteLensPackage(UUID id) {
        lensPackageRepository.deleteById(id);
    }

    private Integer asInteger(Object o) {
        return o == null ? null : ((Number) o).intValue();
    }

    private BigDecimal asDecimal(Object o) {
        if (o == null)
            return null;
        if (o instanceof BigDecimal b)
            return b;
        if (o instanceof Number n)
            return BigDecimal.valueOf(n.doubleValue());
        try {
            return new BigDecimal(o.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
