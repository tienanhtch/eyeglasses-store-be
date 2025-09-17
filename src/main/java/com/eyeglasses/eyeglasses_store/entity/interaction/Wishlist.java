package com.eyeglasses.eyeglasses_store.entity.interaction;

import com.eyeglasses.eyeglasses_store.entity.catalog.Product;
import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "wishlist", uniqueConstraints = {
        @UniqueConstraint(name = "uk_wishlist_user_product", columnNames = { "user_id", "product_id" })
})
public class Wishlist {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
