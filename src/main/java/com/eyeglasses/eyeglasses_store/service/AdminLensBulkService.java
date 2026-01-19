package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.lens.LensPackage;
import com.eyeglasses.eyeglasses_store.repository.lens.LensPackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminLensBulkService {

    private final LensPackageRepository lensPackageRepository;

    public AdminLensBulkService(LensPackageRepository lensPackageRepository) {
        this.lensPackageRepository = lensPackageRepository;
    }

    @Transactional(readOnly = true)
    public List<LensPackage> getAllLensPackages() {
        return lensPackageRepository.findAll();
    }

    @Transactional
    public LensPackage createLensPackage(Map<String, Object> packageData) {
        LensPackage lensPackage = new LensPackage();
        lensPackage.setCode((String) packageData.get("code"));
        lensPackage.setName((String) packageData.get("name"));
        lensPackage.setRefractiveIdx(new BigDecimal(packageData.get("refractiveIdx").toString()));

        // Set features as comma-separated string
        @SuppressWarnings("unchecked")
        List<String> features = (List<String>) packageData.get("features");
        if (features != null) {
            lensPackage.setFeatures(String.join(",", features));
        }

        // Set SPH range
        if (packageData.get("minSph") != null) {
            lensPackage.setMinSph(new BigDecimal(packageData.get("minSph").toString()));
        }
        if (packageData.get("maxSph") != null) {
            lensPackage.setMaxSph(new BigDecimal(packageData.get("maxSph").toString()));
        }

        // Set CYL range
        if (packageData.get("minCyl") != null) {
            lensPackage.setMinCyl(new BigDecimal(packageData.get("minCyl").toString()));
        }
        if (packageData.get("maxCyl") != null) {
            lensPackage.setMaxCyl(new BigDecimal(packageData.get("maxCyl").toString()));
        }

        lensPackage.setRetailPrice(new BigDecimal(packageData.get("retailPrice").toString()));
        if (packageData.get("salePrice") != null) {
            lensPackage.setSalePrice(new BigDecimal(packageData.get("salePrice").toString()));
        }
        lensPackage.setActive((Boolean) packageData.getOrDefault("active", true));

        return lensPackageRepository.save(lensPackage);
    }

    @Transactional
    public LensPackage updateLensPackage(UUID lensPackageId, Map<String, Object> packageData) {
        LensPackage lensPackage = lensPackageRepository.findById(lensPackageId).orElseThrow();
        // Do NOT update code to avoid unique constraint violations if it's not changed
        // or conflicts
        // If code update is needed, must check uniqueness. For now, we allow updating
        // other fields.
        // If code is passed and different, we might want to check, but usually ID is
        // immutable.
        // Let's assume CODE is immutable for now or handled carefully.

        if (packageData.containsKey("name"))
            lensPackage.setName((String) packageData.get("name"));
        if (packageData.containsKey("refractiveIdx"))
            lensPackage.setRefractiveIdx(new BigDecimal(packageData.get("refractiveIdx").toString()));

        if (packageData.containsKey("features")) {
            @SuppressWarnings("unchecked")
            List<String> features = (List<String>) packageData.get("features");
            if (features != null) {
                lensPackage.setFeatures(String.join(",", features));
            }
        }

        if (packageData.containsKey("minSph"))
            lensPackage.setMinSph(new BigDecimal(packageData.get("minSph").toString()));
        if (packageData.containsKey("maxSph"))
            lensPackage.setMaxSph(new BigDecimal(packageData.get("maxSph").toString()));
        if (packageData.containsKey("minCyl"))
            lensPackage.setMinCyl(new BigDecimal(packageData.get("minCyl").toString()));
        if (packageData.containsKey("maxCyl"))
            lensPackage.setMaxCyl(new BigDecimal(packageData.get("maxCyl").toString()));

        if (packageData.containsKey("retailPrice"))
            lensPackage.setRetailPrice(new BigDecimal(packageData.get("retailPrice").toString()));
        if (packageData.containsKey("salePrice")) {
            Object salePrice = packageData.get("salePrice");
            if (salePrice != null) {
                lensPackage.setSalePrice(new BigDecimal(salePrice.toString()));
            } else {
                lensPackage.setSalePrice(null);
            }
        }

        return lensPackageRepository.save(lensPackage);
    }

    @Transactional
    public void updateLensPackageStatus(UUID lensPackageId, boolean active) {
        LensPackage lensPackage = lensPackageRepository.findById(lensPackageId).orElseThrow();
        lensPackage.setActive(active);
        lensPackageRepository.save(lensPackage);
    }

    @Transactional
    public void updateLensPackagePricing(UUID lensPackageId, BigDecimal retailPrice, BigDecimal salePrice) {
        LensPackage lensPackage = lensPackageRepository.findById(lensPackageId).orElseThrow();
        lensPackage.setRetailPrice(retailPrice);
        if (salePrice != null) {
            lensPackage.setSalePrice(salePrice);
        } else {
            lensPackage.setSalePrice(null);
        }
        lensPackageRepository.save(lensPackage);
    }

    @Transactional
    public void updateLensPackageRanges(UUID lensPackageId, BigDecimal minSph, BigDecimal maxSph,
            BigDecimal minCyl, BigDecimal maxCyl) {
        LensPackage lensPackage = lensPackageRepository.findById(lensPackageId).orElseThrow();
        lensPackage.setMinSph(minSph);
        lensPackage.setMaxSph(maxSph);
        lensPackage.setMinCyl(minCyl);
        lensPackage.setMaxCyl(maxCyl);
        lensPackageRepository.save(lensPackage);
    }

    @Transactional
    public void deleteLensPackage(UUID lensPackageId) {
        LensPackage lensPackage = lensPackageRepository.findById(lensPackageId).orElseThrow();
        lensPackageRepository.delete(lensPackage);
    }

    @Transactional
    public void bulkUpdateStatus(List<UUID> lensPackageIds, boolean active) {
        List<LensPackage> packages = lensPackageRepository.findAllById(lensPackageIds);
        packages.forEach(pkg -> pkg.setActive(active));
        lensPackageRepository.saveAll(packages);
    }

    @Transactional
    public void bulkUpdatePricing(List<UUID> lensPackageIds, BigDecimal retailPrice, BigDecimal salePrice) {
        List<LensPackage> packages = lensPackageRepository.findAllById(lensPackageIds);
        packages.forEach(pkg -> {
            pkg.setRetailPrice(retailPrice);
            if (salePrice != null) {
                pkg.setSalePrice(salePrice);
            } else {
                pkg.setSalePrice(null);
            }
        });
        lensPackageRepository.saveAll(packages);
    }
}
