package com.eyeglasses.eyeglasses_store.entity.lens;

import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "prescription")
public class Prescription {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(name = "sphere_right", precision = 5, scale = 2)
    private BigDecimal sphereRight;

    @Column(name = "cylinder_right", precision = 5, scale = 2)
    private BigDecimal cylinderRight;

    @Column(name = "axis_right")
    private Integer axisRight;

    @Column(name = "add_right", precision = 4, scale = 2)
    private BigDecimal addRight;

    @Column(name = "sphere_left", precision = 5, scale = 2)
    private BigDecimal sphereLeft;

    @Column(name = "cylinder_left", precision = 5, scale = 2)
    private BigDecimal cylinderLeft;

    @Column(name = "axis_left")
    private Integer axisLeft;

    @Column(name = "add_left", precision = 4, scale = 2)
    private BigDecimal addLeft;

    @Column(name = "pd", precision = 4, scale = 1)
    private BigDecimal pd;

    @Column(name = "note")
    private String note;

    @Column(name = "issued_at")
    private LocalDate issuedAt;

    @Column(name = "source")
    private String source;

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

    public BigDecimal getSphereRight() {
        return sphereRight;
    }

    public void setSphereRight(BigDecimal sphereRight) {
        this.sphereRight = sphereRight;
    }

    public BigDecimal getCylinderRight() {
        return cylinderRight;
    }

    public void setCylinderRight(BigDecimal cylinderRight) {
        this.cylinderRight = cylinderRight;
    }

    public Integer getAxisRight() {
        return axisRight;
    }

    public void setAxisRight(Integer axisRight) {
        this.axisRight = axisRight;
    }

    public BigDecimal getAddRight() {
        return addRight;
    }

    public void setAddRight(BigDecimal addRight) {
        this.addRight = addRight;
    }

    public BigDecimal getSphereLeft() {
        return sphereLeft;
    }

    public void setSphereLeft(BigDecimal sphereLeft) {
        this.sphereLeft = sphereLeft;
    }

    public BigDecimal getCylinderLeft() {
        return cylinderLeft;
    }

    public void setCylinderLeft(BigDecimal cylinderLeft) {
        this.cylinderLeft = cylinderLeft;
    }

    public Integer getAxisLeft() {
        return axisLeft;
    }

    public void setAxisLeft(Integer axisLeft) {
        this.axisLeft = axisLeft;
    }

    public BigDecimal getAddLeft() {
        return addLeft;
    }

    public void setAddLeft(BigDecimal addLeft) {
        this.addLeft = addLeft;
    }

    public BigDecimal getPd() {
        return pd;
    }

    public void setPd(BigDecimal pd) {
        this.pd = pd;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDate issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
