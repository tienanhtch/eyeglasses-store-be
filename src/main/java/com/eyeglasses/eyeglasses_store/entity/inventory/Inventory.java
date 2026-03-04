package com.eyeglasses.eyeglasses_store.entity.inventory;

import com.eyeglasses.eyeglasses_store.entity.catalog.ProductVariant;
import com.eyeglasses.eyeglasses_store.entity.store.Store;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "inventory", uniqueConstraints = {
        @UniqueConstraint(name = "uk_inventory_store_variant", columnNames = { "store_id", "variant_id" })
})
public class Inventory {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "on_hand", nullable = false)
    private Integer onHand = 0;

    @Column(name = "reserved", nullable = false)
    private Integer reserved = 0;

    public UUID getId() {
        return id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public Integer getOnHand() {
        return onHand;
    }

    public void setOnHand(Integer onHand) {
        this.onHand = onHand;
    }

    public Integer getReserved() {
        return reserved;
    }

    public void setReserved(Integer reserved) {
        this.reserved = reserved;
    }
}
