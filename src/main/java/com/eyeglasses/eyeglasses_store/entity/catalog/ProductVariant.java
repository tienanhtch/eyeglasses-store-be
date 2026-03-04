package com.eyeglasses.eyeglasses_store.entity.catalog;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_variant", indexes = {
        @Index(name = "uk_product_variant_sku", columnList = "sku", unique = true)
})
public class ProductVariant {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(name = "color")
    private String color;

    @Column(name = "size_mm")
    private Integer sizeMm;

    @Column(name = "bridge_mm")
    private Integer bridgeMm;

    @Column(name = "temple_mm")
    private Integer templeMm;

    @Column(name = "cost_price", precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "retail_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal retailPrice;

    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getSizeMm() {
        return sizeMm;
    }

    public void setSizeMm(Integer sizeMm) {
        this.sizeMm = sizeMm;
    }

    public Integer getBridgeMm() {
        return bridgeMm;
    }

    public void setBridgeMm(Integer bridgeMm) {
        this.bridgeMm = bridgeMm;
    }

    public Integer getTempleMm() {
        return templeMm;
    }

    public void setTempleMm(Integer templeMm) {
        this.templeMm = templeMm;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
