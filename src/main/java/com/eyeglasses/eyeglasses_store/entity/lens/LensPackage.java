package com.eyeglasses.eyeglasses_store.entity.lens;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "lens_package", indexes = {
        @Index(name = "uk_lens_package_code", columnList = "code", unique = true)
})
public class LensPackage {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "refractive_idx", precision = 3, scale = 2)
    private BigDecimal refractiveIdx;

    @Column(name = "features")
    private String features; // store as JSON string or comma-separated

    @Column(name = "min_sph", precision = 5, scale = 2)
    private BigDecimal minSph;

    @Column(name = "max_sph", precision = 5, scale = 2)
    private BigDecimal maxSph;

    @Column(name = "min_cyl", precision = 5, scale = 2)
    private BigDecimal minCyl;

    @Column(name = "max_cyl", precision = 5, scale = 2)
    private BigDecimal maxCyl;

    @Column(name = "retail_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal retailPrice;

    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getRefractiveIdx() {
        return refractiveIdx;
    }

    public void setRefractiveIdx(BigDecimal refractiveIdx) {
        this.refractiveIdx = refractiveIdx;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public BigDecimal getMinSph() {
        return minSph;
    }

    public void setMinSph(BigDecimal minSph) {
        this.minSph = minSph;
    }

    public BigDecimal getMaxSph() {
        return maxSph;
    }

    public void setMaxSph(BigDecimal maxSph) {
        this.maxSph = maxSph;
    }

    public BigDecimal getMinCyl() {
        return minCyl;
    }

    public void setMinCyl(BigDecimal minCyl) {
        this.minCyl = minCyl;
    }

    public BigDecimal getMaxCyl() {
        return maxCyl;
    }

    public void setMaxCyl(BigDecimal maxCyl) {
        this.maxCyl = maxCyl;
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
