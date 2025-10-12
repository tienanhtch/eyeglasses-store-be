package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.entity.catalog.Attribute;
import com.eyeglasses.eyeglasses_store.entity.catalog.AttributeValue;
import com.eyeglasses.eyeglasses_store.entity.catalog.CategoryAttribute;
import com.eyeglasses.eyeglasses_store.entity.catalog.ProductAttribute;
import com.eyeglasses.eyeglasses_store.service.AdminAttributeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/attributes")
public class AdminAttributeController {

    private final AdminAttributeService service;

    public AdminAttributeController(AdminAttributeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Attribute>> listAttributes() {
        return ResponseEntity.ok(service.listAttributes());
    }

    private record AttributeRequest(String code, String name, String type, Integer sortOrder, Boolean active) {
    }

    @PostMapping
    public ResponseEntity<Attribute> createAttribute(@RequestBody AttributeRequest body) {
        return ResponseEntity
                .ok(service.createAttribute(body.code(), body.name(), body.type(), body.sortOrder(), body.active()));
    }

    @PatchMapping("/{attributeId}")
    public ResponseEntity<Attribute> updateAttribute(@PathVariable("attributeId") UUID attributeId,
            @RequestBody AttributeRequest body) {
        return ResponseEntity
                .ok(service.updateAttribute(attributeId, body.name(), body.type(), body.sortOrder(), body.active()));
    }

    @DeleteMapping("/{attributeId}")
    public ResponseEntity<Map<String, String>> deleteAttribute(@PathVariable("attributeId") UUID attributeId) {
        service.deleteAttribute(attributeId);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    // values
    @GetMapping("/{attributeId}/values")
    public ResponseEntity<List<AttributeValue>> listValues(@PathVariable("attributeId") UUID attributeId) {
        return ResponseEntity.ok(service.listAttributeValues(attributeId));
    }

    private record ValueRequest(String value, Integer sortOrder, Boolean active) {
    }

    @PostMapping("/{attributeId}/values")
    public ResponseEntity<AttributeValue> createValue(@PathVariable("attributeId") UUID attributeId,
            @RequestBody ValueRequest body) {
        return ResponseEntity
                .ok(service.createAttributeValue(attributeId, body.value(), body.sortOrder(), body.active()));
    }

    @PatchMapping("/values/{valueId}")
    public ResponseEntity<AttributeValue> updateValue(@PathVariable("valueId") UUID valueId,
            @RequestBody ValueRequest body) {
        return ResponseEntity.ok(service.updateAttributeValue(valueId, body.value(), body.sortOrder(), body.active()));
    }

    @DeleteMapping("/values/{valueId}")
    public ResponseEntity<Map<String, String>> deleteValue(@PathVariable("valueId") UUID valueId) {
        service.deleteAttributeValue(valueId);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    // category mapping
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<CategoryAttribute>> listCategoryAttributes(@PathVariable("categoryId") UUID categoryId) {
        return ResponseEntity.ok(service.listCategoryAttributes(categoryId));
    }

    private record CategoryAttributeRequest(UUID attributeId, Integer sortOrder) {
    }

    @PostMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryAttribute> addCategoryAttribute(@PathVariable("categoryId") UUID categoryId,
            @RequestBody CategoryAttributeRequest body) {
        return ResponseEntity.ok(service.addCategoryAttribute(categoryId, body.attributeId(), body.sortOrder()));
    }

    @DeleteMapping("/categories/map/{id}")
    public ResponseEntity<Map<String, String>> removeCategoryAttribute(@PathVariable("id") UUID id) {
        service.removeCategoryAttribute(id);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    // product assignment
    private record AssignProductAttrRequest(UUID attributeValueId) {
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<ProductAttribute> assignProductAttribute(@PathVariable("productId") UUID productId,
            @RequestBody AssignProductAttrRequest body) {
        return ResponseEntity.ok(service.assignProductAttribute(productId, body.attributeValueId()));
    }

    @DeleteMapping("/products/map/{id}")
    public ResponseEntity<Map<String, String>> removeProductAttribute(@PathVariable("id") UUID id) {
        service.removeProductAttribute(id);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
