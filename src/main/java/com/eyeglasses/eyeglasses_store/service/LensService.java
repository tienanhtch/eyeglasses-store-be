package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.lens.LensPackage;
import com.eyeglasses.eyeglasses_store.entity.lens.Prescription;
import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.repository.lens.LensPackageRepository;
import com.eyeglasses.eyeglasses_store.repository.lens.PrescriptionRepository;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class LensService {

    private final LensPackageRepository lensPackageRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final AppUserRepository userRepository;

    public LensService(LensPackageRepository lensPackageRepository,
            PrescriptionRepository prescriptionRepository,
            AppUserRepository userRepository) {
        this.lensPackageRepository = lensPackageRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listLensPackages() {
        return lensPackageRepository.findAll().stream()
                .filter(LensPackage::isActive)
                .map(lp -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", lp.getId());
                    m.put("code", lp.getCode());
                    m.put("name", lp.getName());
                    m.put("refractiveIdx", lp.getRefractiveIdx());
                    m.put("features", lp.getFeatures());
                    m.put("minSph", lp.getMinSph());
                    m.put("maxSph", lp.getMaxSph());
                    m.put("minCyl", lp.getMinCyl());
                    m.put("maxCyl", lp.getMaxCyl());
                    m.put("retailPrice", lp.getRetailPrice());
                    m.put("salePrice", lp.getSalePrice());
                    return m;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> validatePrescriptionForLens(UUID lensPackageId, Map<String, Object> presc) {
        LensPackage lp = lensPackageRepository.findById(lensPackageId).orElseThrow();
        // Lấy giá trị cần kiểm tra
        BigDecimal sphereRight = toBigDecimal(presc.get("sphereRight"));
        BigDecimal cylinderRight = toBigDecimal(presc.get("cylinderRight"));
        BigDecimal sphereLeft = toBigDecimal(presc.get("sphereLeft"));
        BigDecimal cylinderLeft = toBigDecimal(presc.get("cylinderLeft"));

        boolean ok = checkRange(sphereRight, lp.getMinSph(), lp.getMaxSph())
                && checkRange(sphereLeft, lp.getMinSph(), lp.getMaxSph())
                && checkRange(cylinderRight, lp.getMinCyl(), lp.getMaxCyl())
                && checkRange(cylinderLeft, lp.getMinCyl(), lp.getMaxCyl());

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("compatible", ok);
        if (!ok) {
            m.put("message", "Toa không phù hợp với gói tròng đã chọn");
            m.put("suggestion", "Vui lòng chọn gói tròng cao hơn hoặc liên hệ KTV");
        }
        m.put("lensPackageId", lensPackageId);
        return m;
    }

    @Transactional
    public Map<String, Object> savePrescription(UUID userId, Map<String, Object> presc) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        Prescription p = new Prescription();
        p.setUser(user);
        p.setSphereRight(toBigDecimal(presc.get("sphereRight")));
        p.setCylinderRight(toBigDecimal(presc.get("cylinderRight")));
        p.setAxisRight((Integer) presc.getOrDefault("axisRight", null));
        p.setAddRight(toBigDecimal(presc.get("addRight")));
        p.setSphereLeft(toBigDecimal(presc.get("sphereLeft")));
        p.setCylinderLeft(toBigDecimal(presc.get("cylinderLeft")));
        p.setAxisLeft((Integer) presc.getOrDefault("axisLeft", null));
        p.setAddLeft(toBigDecimal(presc.get("addLeft")));
        p.setPd(toBigDecimal(presc.get("pd")));
        p.setNote((String) presc.getOrDefault("note", null));
        p.setSource((String) presc.getOrDefault("source", null));
        Prescription saved = prescriptionRepository.save(p);
        return Map.of(
                "id", saved.getId(),
                "createdAt", saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> quote(java.util.UUID variantId, java.util.UUID lensPackageId, Map<String, Object> presc,
            com.eyeglasses.eyeglasses_store.repository.catalog.ProductVariantRepository variantRepository) {
        java.math.BigDecimal framePrice = java.math.BigDecimal.ZERO;
        if (variantId != null) {
            var variant = variantRepository.findById(variantId).orElse(null);
            if (variant != null) {
                framePrice = variant.getSalePrice() != null ? variant.getSalePrice() : variant.getRetailPrice();
            }
        }
        var lp = (lensPackageId != null) ? lensPackageRepository.findById(lensPackageId).orElse(null) : null;
        java.math.BigDecimal lensPrice = java.math.BigDecimal.ZERO;
        if (lp != null) {
            lensPrice = lp.getSalePrice() != null ? lp.getSalePrice() : lp.getRetailPrice();
        }
        java.math.BigDecimal total = framePrice.add(lensPrice);
        Map<String, Object> validation = lensPackageId != null ? validatePrescriptionForLens(lensPackageId, presc)
                : java.util.Map.of("compatible", true);
        java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("framePrice", framePrice);
        m.put("lensPrice", lensPrice);
        m.put("total", total);
        m.put("validation", validation);
        return m;
    }

    private boolean checkRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value == null)
            return true; // trường trống không kiểm tra
        if (min != null && value.compareTo(min) < 0)
            return false;
        if (max != null && value.compareTo(max) > 0)
            return false;
        return true;
    }

    private BigDecimal toBigDecimal(Object o) {
        if (o == null)
            return null;
        if (o instanceof BigDecimal b)
            return b;
        if (o instanceof Number n)
            return BigDecimal.valueOf(n.doubleValue());
        try {
            return new BigDecimal(o.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
