package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.analytics.SearchLog;
import com.eyeglasses.eyeglasses_store.entity.catalog.Product;
import com.eyeglasses.eyeglasses_store.entity.catalog.ProductVariant;
import com.eyeglasses.eyeglasses_store.entity.user.SavedFilter;
import com.eyeglasses.eyeglasses_store.repository.analytics.SearchLogRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductRepository;
import com.eyeglasses.eyeglasses_store.repository.user.SavedFilterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdvancedSearchService {

    private final ProductRepository productRepository;
    private final SearchLogRepository searchLogRepository;
    private final SavedFilterRepository savedFilterRepository;

    public AdvancedSearchService(ProductRepository productRepository,
            SearchLogRepository searchLogRepository,
            SavedFilterRepository savedFilterRepository) {
        this.productRepository = productRepository;
        this.searchLogRepository = searchLogRepository;
        this.savedFilterRepository = savedFilterRepository;
    }

    public Map<String, Object> advancedSearch(String query, String categorySlug, String material,
            String frameShape, BigDecimal minPrice, BigDecimal maxPrice,
            String sortBy, String sortOrder, Pageable pageable, UUID userId) {

        // Log search query
        if (query != null && !query.trim().isEmpty()) {
            logSearchQuery(query, userId);
        }

        // Build search criteria
        Map<String, Object> searchCriteria = new HashMap<>();
        if (query != null && !query.trim().isEmpty()) {
            searchCriteria.put("query", query.trim());
        }
        if (categorySlug != null)
            searchCriteria.put("categorySlug", categorySlug);
        if (material != null)
            searchCriteria.put("material", material);
        if (frameShape != null)
            searchCriteria.put("frameShape", frameShape);
        if (minPrice != null)
            searchCriteria.put("minPrice", minPrice);
        if (maxPrice != null)
            searchCriteria.put("maxPrice", maxPrice);

        // Get products with filters
        List<Product> allProducts = productRepository.findAll();
        List<Product> filteredProducts = allProducts.stream()
                .filter(product -> matchesCriteria(product, searchCriteria))
                .collect(Collectors.toList());

        // Sort results
        if (sortBy != null) {
            filteredProducts = sortProducts(filteredProducts, sortBy, sortOrder);
        }

        // Paginate results
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredProducts.size());
        List<Product> paginatedProducts = filteredProducts.subList(start, end);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("products", paginatedProducts);
        response.put("totalElements", filteredProducts.size());
        response.put("totalPages", (int) Math.ceil((double) filteredProducts.size() / pageable.getPageSize()));
        response.put("currentPage", pageable.getPageNumber());
        response.put("pageSize", pageable.getPageSize());
        response.put("hasNext", end < filteredProducts.size());
        response.put("hasPrevious", pageable.getPageNumber() > 0);

        // Add search suggestions
        if (query != null && !query.trim().isEmpty()) {
            response.put("suggestions", generateSearchSuggestions(query));
        }

        return response;
    }

    private boolean matchesCriteria(Product product, Map<String, Object> criteria) {
        // Query match
        if (criteria.containsKey("query")) {
            String query = criteria.get("query").toString().toLowerCase();
            if (!product.getName().toLowerCase().contains(query) &&
                    !product.getDescription().toLowerCase().contains(query) &&
                    !product.getBrand().toLowerCase().contains(query)) {
                return false;
            }
        }

        // Category match
        if (criteria.containsKey("categorySlug")) {
            String categorySlug = criteria.get("categorySlug").toString();
            boolean categoryMatch = product.getCategories().stream()
                    .anyMatch(cat -> cat.getSlug().equals(categorySlug));
            if (!categoryMatch)
                return false;
        }

        // Material match
        if (criteria.containsKey("material")) {
            String material = criteria.get("material").toString();
            if (!material.equals(product.getMaterial()))
                return false;
        }

        // Frame shape match
        if (criteria.containsKey("frameShape")) {
            String frameShape = criteria.get("frameShape").toString();
            if (!frameShape.equals(product.getFrameShape()))
                return false;
        }

        // Price range match - simplified for now
        if (criteria.containsKey("minPrice") || criteria.containsKey("maxPrice")) {
            // TODO: Implement price filtering when Product-Variant relationship is properly
            // set up
            return true;
        }

        return true;
    }

    private List<Product> sortProducts(List<Product> products, String sortBy, String sortOrder) {
        boolean ascending = "asc".equalsIgnoreCase(sortOrder);

        switch (sortBy.toLowerCase()) {
            case "name":
                products.sort((p1, p2) -> ascending ? p1.getName().compareTo(p2.getName())
                        : p2.getName().compareTo(p1.getName()));
                break;
            case "price":
                products.sort((p1, p2) -> {
                    BigDecimal price1 = getMinPrice(p1);
                    BigDecimal price2 = getMinPrice(p2);
                    return ascending ? price1.compareTo(price2) : price2.compareTo(price1);
                });
                break;
            case "created":
                products.sort((p1, p2) -> ascending ? p1.getCreatedAt().compareTo(p2.getCreatedAt())
                        : p2.getCreatedAt().compareTo(p1.getCreatedAt()));
                break;
            default:
                // Default sort by relevance (name)
                products.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
        }

        return products;
    }

    private BigDecimal getMinPrice(Product product) {
        // TODO: Implement when Product-Variant relationship is properly set up
        return BigDecimal.valueOf(100000); // Mock price
    }

    private List<String> generateSearchSuggestions(String query) {
        // TODO: Implement when SearchLog entity is properly set up
        return List.of("kính cận", "kính râm", "gọng kính", "tròng kính");
    }

    @Transactional
    public void logSearchQuery(String query, UUID userId) {
        // TODO: Implement when SearchLog entity is properly set up
        System.out.println("Search logged: " + query + " by user: " + userId);
    }

    public List<String> getSearchSuggestions(String partialQuery) {
        if (partialQuery == null || partialQuery.trim().length() < 2) {
            return Collections.emptyList();
        }

        String query = partialQuery.trim().toLowerCase();

        // Get suggestions from product names
        List<String> productSuggestions = productRepository.findAll().stream()
                .map(Product::getName)
                .filter(name -> name.toLowerCase().contains(query))
                .distinct()
                .limit(5)
                .collect(Collectors.toList());

        return productSuggestions;
    }

    @Transactional
    public SavedFilter saveFilter(UUID userId, String name, Map<String, Object> filterCriteria) {
        // TODO: Implement when SavedFilter entity is properly set up
        SavedFilter savedFilter = new SavedFilter();
        return savedFilter;
    }

    public List<SavedFilter> getUserSavedFilters(UUID userId) {
        // TODO: Implement when SavedFilter entity is properly set up
        return Collections.emptyList();
    }

    @Transactional
    public void deleteSavedFilter(UUID filterId) {
        savedFilterRepository.deleteById(filterId);
    }
}
