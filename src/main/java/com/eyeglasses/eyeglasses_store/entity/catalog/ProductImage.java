package com.eyeglasses.eyeglasses_store.entity.catalog;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

@Entity
@Table(name = "product_image")
public class ProductImage {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "alt")
    private String alt;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
