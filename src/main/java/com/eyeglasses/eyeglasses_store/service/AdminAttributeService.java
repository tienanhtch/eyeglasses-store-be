package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.catalog.*;
import com.eyeglasses.eyeglasses_store.repository.catalog.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AdminAttributeService {

    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;
    private final ProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;

    public AdminAttributeService(AttributeRepository attributeRepository,
            AttributeValueRepository attributeValueRepository,
            CategoryRepository categoryRepository,
            CategoryAttributeRepository categoryAttributeRepository,
            ProductRepository productRepository,
            ProductAttributeRepository productAttributeRepository) {
        this.attributeRepository = attributeRepository;
        this.attributeValueRepository = attributeValueRepository;
        this.categoryRepository = categoryRepository;
        this.categoryAttributeRepository = categoryAttributeRepository;
        this.productRepository = productRepository;
        this.productAttributeRepository = productAttributeRepository;
    }

    // Attribute CRUD
    @Transactional(readOnly = true)
    public List<Attribute> listAttributes() {
        return attributeRepository.findAll();
    }

    @Transactional
    public Attribute createAttribute(String code, String name, String type, Integer sortOrder, Boolean active) {
        Attribute a = new Attribute();
        a.setCode(code);
        a.setName(name);
        if (type != null)
            a.setType(type);
        if (sortOrder != null)
            a.setSortOrder(sortOrder);
        if (active != null)
            a.setActive(active);
        return attributeRepository.save(a);
    }

    @Transactional
    public Attribute updateAttribute(java.util.UUID id, String name, String type, Integer sortOrder, Boolean active) {
        Attribute a = attributeRepository.findById(id).orElseThrow();
        if (name != null)
            a.setName(name);
        if (type != null)
            a.setType(type);
        if (sortOrder != null)
            a.setSortOrder(sortOrder);
        if (active != null)
            a.setActive(active);
        return attributeRepository.save(a);
    }

    @Transactional
    public void deleteAttribute(java.util.UUID id) {
        attributeRepository.deleteById(id);
    }

    // Attribute values
    @Transactional(readOnly = true)
    public java.util.List<AttributeValue> listAttributeValues(java.util.UUID attributeId) {
        return attributeValueRepository.findByAttributeIdOrderBySortOrderAsc(attributeId);
    }

    @Transactional
    public AttributeValue createAttributeValue(java.util.UUID attributeId, String value, Integer sortOrder,
            Boolean active) {
        Attribute attr = attributeRepository.findById(attributeId).orElseThrow();
        AttributeValue v = new AttributeValue();
        v.setAttribute(attr);
        v.setValue(value);
        if (sortOrder != null)
            v.setSortOrder(sortOrder);
        if (active != null)
            v.setActive(active);
        return attributeValueRepository.save(v);
    }

    @Transactional
    public AttributeValue updateAttributeValue(java.util.UUID valueId, String value, Integer sortOrder,
            Boolean active) {
        AttributeValue v = attributeValueRepository.findById(valueId).orElseThrow();
        if (value != null)
            v.setValue(value);
        if (sortOrder != null)
            v.setSortOrder(sortOrder);
        if (active != null)
            v.setActive(active);
        return attributeValueRepository.save(v);
    }

    @Transactional
    public void deleteAttributeValue(java.util.UUID valueId) {
        attributeValueRepository.deleteById(valueId);
    }

    // Category filter mapping
    @Transactional(readOnly = true)
    public java.util.List<CategoryAttribute> listCategoryAttributes(java.util.UUID categoryId) {
        return categoryAttributeRepository.findByCategoryIdOrderBySortOrderAsc(categoryId);
    }

    @Transactional
    public CategoryAttribute addCategoryAttribute(java.util.UUID categoryId, java.util.UUID attributeId,
            Integer sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        Attribute attr = attributeRepository.findById(attributeId).orElseThrow();
        CategoryAttribute ca = new CategoryAttribute();
        ca.setCategory(category);
        ca.setAttribute(attr);
        if (sortOrder != null)
            ca.setSortOrder(sortOrder);
        return categoryAttributeRepository.save(ca);
    }

    @Transactional
    public void removeCategoryAttribute(java.util.UUID categoryAttributeId) {
        categoryAttributeRepository.deleteById(categoryAttributeId);
    }

    // Product assignment
    @Transactional
    public ProductAttribute assignProductAttribute(java.util.UUID productId, java.util.UUID attributeValueId) {
        Product p = productRepository.findById(productId).orElseThrow();
        AttributeValue av = attributeValueRepository.findById(attributeValueId).orElseThrow();
        ProductAttribute pa = new ProductAttribute();
        pa.setProduct(p);
        pa.setAttributeValue(av);
        return productAttributeRepository.save(pa);
    }

    @Transactional
    public void removeProductAttribute(java.util.UUID productAttributeId) {
        productAttributeRepository.deleteById(productAttributeId);
    }
}
