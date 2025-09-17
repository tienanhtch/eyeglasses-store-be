package com.eyeglasses.eyeglasses_store.entity.order;

import com.eyeglasses.eyeglasses_store.entity.catalog.ProductVariant;
import com.eyeglasses.eyeglasses_store.entity.lens.LensPackage;
import com.eyeglasses.eyeglasses_store.entity.lens.Prescription;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "name_snapshot", nullable = false)
    private String nameSnapshot;

    @Column(name = "sku_snapshot", nullable = false)
    private String skuSnapshot;

    @Column(name = "price_unit", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceUnit;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lens_package_id")
    private LensPackage lensPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    public UUID getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public String getNameSnapshot() {
        return nameSnapshot;
    }

    public void setNameSnapshot(String nameSnapshot) {
        this.nameSnapshot = nameSnapshot;
    }

    public String getSkuSnapshot() {
        return skuSnapshot;
    }

    public void setSkuSnapshot(String skuSnapshot) {
        this.skuSnapshot = skuSnapshot;
    }

    public BigDecimal getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(BigDecimal priceUnit) {
        this.priceUnit = priceUnit;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public LensPackage getLensPackage() {
        return lensPackage;
    }

    public void setLensPackage(LensPackage lensPackage) {
        this.lensPackage = lensPackage;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
