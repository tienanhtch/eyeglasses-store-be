package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.user.SavedFilter;
import com.eyeglasses.eyeglasses_store.service.AdvancedSearchService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.PUBLIC_BASE + "/search")
public class AdvancedSearchController {

    private final AdvancedSearchService advancedSearchService;

    public AdvancedSearchController(AdvancedSearchService advancedSearchService) {
        this.advancedSearchService = advancedSearchService;
    }

    @GetMapping("/advanced")
    public ResponseEntity<Map<String, Object>> advancedSearch(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String frameShape,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            Pageable pageable,
            @RequestParam(required = false) UUID userId) {

        Map<String, Object> results = advancedSearchService.advancedSearch(
                q, category, material, frameShape, minPrice, maxPrice,
                sortBy, sortOrder, pageable, userId);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(
            @RequestParam String q,
            @RequestParam(required = false) UUID userId) {

        List<String> suggestions = advancedSearchService.getSearchSuggestions(q);
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/filters/save")
    public ResponseEntity<Map<String, Object>> saveFilter(
            @RequestParam UUID userId,
            @RequestParam String name,
            @RequestBody Map<String, Object> filterCriteria) {
        try {
            var savedFilter = advancedSearchService.saveFilter(userId, name, filterCriteria);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "filterId", savedFilter.getId(),
                    "name", savedFilter.getName(),
                    "message", "Filter saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/filters")
    public ResponseEntity<List<SavedFilter>> getUserSavedFilters(@RequestParam UUID userId) {
        List<SavedFilter> filters = advancedSearchService.getUserSavedFilters(userId);
        return ResponseEntity.ok(filters);
    }

    @DeleteMapping("/filters/{filterId}")
    public ResponseEntity<Map<String, Object>> deleteSavedFilter(@PathVariable UUID filterId) {
        try {
            advancedSearchService.deleteSavedFilter(filterId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Filter deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }
}
