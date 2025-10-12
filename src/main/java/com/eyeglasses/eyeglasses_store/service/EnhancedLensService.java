package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.lens.LensPackage;
import com.eyeglasses.eyeglasses_store.entity.lens.Prescription;
import com.eyeglasses.eyeglasses_store.entity.catalog.ProductVariant;
import com.eyeglasses.eyeglasses_store.repository.lens.LensPackageRepository;
import com.eyeglasses.eyeglasses_store.repository.lens.PrescriptionRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class EnhancedLensService {

    private final LensPackageRepository lensPackageRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ProductVariantRepository variantRepository;

    public EnhancedLensService(LensPackageRepository lensPackageRepository,
            PrescriptionRepository prescriptionRepository,
            ProductVariantRepository variantRepository) {
        this.lensPackageRepository = lensPackageRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.variantRepository = variantRepository;
    }

    public Map<String, Object> validatePrescriptionWithLensPackage(UUID lensPackageId,
            Map<String, Object> prescriptionData) {
        LensPackage lensPackage = lensPackageRepository.findById(lensPackageId).orElseThrow();

        // Extract prescription values
        BigDecimal sphereRight = new BigDecimal(prescriptionData.get("sphereRight").toString());
        BigDecimal cylinderRight = new BigDecimal(prescriptionData.get("cylinderRight").toString());
        // BigDecimal axisRight = new
        // BigDecimal(prescriptionData.get("axisRight").toString());
        BigDecimal sphereLeft = new BigDecimal(prescriptionData.get("sphereLeft").toString());
        BigDecimal cylinderLeft = new BigDecimal(prescriptionData.get("cylinderLeft").toString());
        // BigDecimal axisLeft = new
        // BigDecimal(prescriptionData.get("axisLeft").toString());
        // BigDecimal pd = new BigDecimal(prescriptionData.get("pd").toString());

        Map<String, Object> validation = new HashMap<>();
        validation.put("valid", true);
        validation.put("warnings", new ArrayList<String>());
        validation.put("errors", new ArrayList<String>());

        // Validate SPH range
        if (lensPackage.getMinSph() != null && lensPackage.getMaxSph() != null) {
            if (sphereRight.compareTo(lensPackage.getMinSph()) < 0
                    || sphereRight.compareTo(lensPackage.getMaxSph()) > 0) {
                validation.put("valid", false);
                @SuppressWarnings("unchecked")
                List<String> errors = (List<String>) validation.get("errors");
                errors.add("Right sphere value out of range for this lens package");
            }
            if (sphereLeft.compareTo(lensPackage.getMinSph()) < 0
                    || sphereLeft.compareTo(lensPackage.getMaxSph()) > 0) {
                validation.put("valid", false);
                @SuppressWarnings("unchecked")
                List<String> errors = (List<String>) validation.get("errors");
                errors.add("Left sphere value out of range for this lens package");
            }
        }

        // Validate CYL range
        if (lensPackage.getMinCyl() != null && lensPackage.getMaxCyl() != null) {
            if (cylinderRight.compareTo(lensPackage.getMinCyl()) < 0
                    || cylinderRight.compareTo(lensPackage.getMaxCyl()) > 0) {
                validation.put("valid", false);
                @SuppressWarnings("unchecked")
                List<String> errors = (List<String>) validation.get("errors");
                errors.add("Right cylinder value out of range for this lens package");
            }
            if (cylinderLeft.compareTo(lensPackage.getMinCyl()) < 0
                    || cylinderLeft.compareTo(lensPackage.getMaxCyl()) > 0) {
                validation.put("valid", false);
                @SuppressWarnings("unchecked")
                List<String> errors = (List<String>) validation.get("errors");
                errors.add("Left cylinder value out of range for this lens package");
            }
        }

        // Check for high prescriptions that might need special handling
        if (Math.abs(sphereRight.doubleValue()) > 6.0 || Math.abs(sphereLeft.doubleValue()) > 6.0) {
            @SuppressWarnings("unchecked")
            List<String> warnings = (List<String>) validation.get("warnings");
            warnings.add("High prescription detected - consider premium lens options");
        }

        // Check for astigmatism
        if (cylinderRight.abs().compareTo(BigDecimal.valueOf(0.5)) > 0
                || cylinderLeft.abs().compareTo(BigDecimal.valueOf(0.5)) > 0) {
            @SuppressWarnings("unchecked")
            List<String> warnings = (List<String>) validation.get("warnings");
            warnings.add("Astigmatism detected - ensure proper axis alignment");
        }

        return validation;
    }

    public Map<String, Object> calculateLensPricing(UUID frameVariantId, UUID lensPackageId,
            Map<String, Object> prescriptionData) {
        ProductVariant frameVariant = variantRepository.findById(frameVariantId).orElseThrow();
        LensPackage lensPackage = lensPackageRepository.findById(lensPackageId).orElseThrow();

        BigDecimal framePrice = frameVariant.getSalePrice() != null ? frameVariant.getSalePrice()
                : frameVariant.getRetailPrice();
        BigDecimal lensPrice = lensPackage.getSalePrice() != null ? lensPackage.getSalePrice()
                : lensPackage.getRetailPrice();

        // Calculate base total
        BigDecimal baseTotal = framePrice.add(lensPrice);

        // Apply prescription complexity factors
        BigDecimal complexityFactor = calculateComplexityFactor(prescriptionData);
        BigDecimal adjustedLensPrice = lensPrice.multiply(complexityFactor);

        BigDecimal finalTotal = framePrice.add(adjustedLensPrice);

        Map<String, Object> pricing = new HashMap<>();
        pricing.put("framePrice", framePrice);
        pricing.put("lensPrice", lensPrice);
        pricing.put("adjustedLensPrice", adjustedLensPrice);
        pricing.put("complexityFactor", complexityFactor);
        pricing.put("baseTotal", baseTotal);
        pricing.put("finalTotal", finalTotal);
        pricing.put("savings", baseTotal.subtract(finalTotal));

        return pricing;
    }

    private BigDecimal calculateComplexityFactor(Map<String, Object> prescriptionData) {
        BigDecimal sphereRight = new BigDecimal(prescriptionData.get("sphereRight").toString());
        BigDecimal cylinderRight = new BigDecimal(prescriptionData.get("cylinderRight").toString());
        BigDecimal sphereLeft = new BigDecimal(prescriptionData.get("sphereLeft").toString());
        BigDecimal cylinderLeft = new BigDecimal(prescriptionData.get("cylinderLeft").toString());

        // Base factor
        BigDecimal factor = BigDecimal.ONE;

        // High prescription factor
        BigDecimal maxSphere = sphereRight.abs().max(sphereLeft.abs());
        if (maxSphere.compareTo(BigDecimal.valueOf(4.0)) > 0) {
            factor = factor.add(BigDecimal.valueOf(0.1));
        }
        if (maxSphere.compareTo(BigDecimal.valueOf(6.0)) > 0) {
            factor = factor.add(BigDecimal.valueOf(0.2));
        }

        // Astigmatism factor
        BigDecimal maxCylinder = cylinderRight.abs().max(cylinderLeft.abs());
        if (maxCylinder.compareTo(BigDecimal.valueOf(2.0)) > 0) {
            factor = factor.add(BigDecimal.valueOf(0.15));
        }

        return factor;
    }

    public List<Map<String, Object>> getRecommendedLensPackages(Map<String, Object> prescriptionData) {
        BigDecimal sphereRight = new BigDecimal(prescriptionData.get("sphereRight").toString());
        BigDecimal cylinderRight = new BigDecimal(prescriptionData.get("cylinderRight").toString());
        BigDecimal sphereLeft = new BigDecimal(prescriptionData.get("sphereLeft").toString());
        BigDecimal cylinderLeft = new BigDecimal(prescriptionData.get("cylinderLeft").toString());

        List<LensPackage> allPackages = lensPackageRepository.findAll();
        List<Map<String, Object>> recommendations = new ArrayList<>();

        for (LensPackage pkg : allPackages) {
            if (isPackageSuitable(pkg, sphereRight, cylinderRight, sphereLeft, cylinderLeft)) {
                Map<String, Object> recommendation = new HashMap<>();
                recommendation.put("lensPackage", pkg);
                recommendation.put("suitability",
                        calculateSuitability(pkg, sphereRight, cylinderRight, sphereLeft, cylinderLeft));
                recommendation.put("pricing", calculateLensPricing(null, pkg.getId(), prescriptionData));
                recommendations.add(recommendation);
            }
        }

        // Sort by suitability
        recommendations.sort((a, b) -> {
            BigDecimal suitabilityA = (BigDecimal) a.get("suitability");
            BigDecimal suitabilityB = (BigDecimal) b.get("suitability");
            return suitabilityB.compareTo(suitabilityA);
        });

        return recommendations.stream().limit(5).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private boolean isPackageSuitable(LensPackage pkg, BigDecimal sphereRight, BigDecimal cylinderRight,
            BigDecimal sphereLeft, BigDecimal cylinderLeft) {
        // Check SPH range
        if (pkg.getMinSph() != null && pkg.getMaxSph() != null) {
            if (sphereRight.compareTo(pkg.getMinSph()) < 0 || sphereRight.compareTo(pkg.getMaxSph()) > 0)
                return false;
            if (sphereLeft.compareTo(pkg.getMinSph()) < 0 || sphereLeft.compareTo(pkg.getMaxSph()) > 0)
                return false;
        }

        // Check CYL range
        if (pkg.getMinCyl() != null && pkg.getMaxCyl() != null) {
            if (cylinderRight.compareTo(pkg.getMinCyl()) < 0 || cylinderRight.compareTo(pkg.getMaxCyl()) > 0)
                return false;
            if (cylinderLeft.compareTo(pkg.getMinCyl()) < 0 || cylinderLeft.compareTo(pkg.getMaxCyl()) > 0)
                return false;
        }

        return true;
    }

    private BigDecimal calculateSuitability(LensPackage pkg, BigDecimal sphereRight, BigDecimal cylinderRight,
            BigDecimal sphereLeft, BigDecimal cylinderLeft) {
        BigDecimal suitability = BigDecimal.ONE;

        // Higher refractive index = better for high prescriptions
        if (pkg.getRefractiveIdx() != null) {
            suitability = suitability
                    .add(pkg.getRefractiveIdx().subtract(BigDecimal.valueOf(1.5)).multiply(BigDecimal.valueOf(0.5)));
        }

        // Check if prescription is within optimal range
        BigDecimal maxSphere = sphereRight.abs().max(sphereLeft.abs());
        if (pkg.getMinSph() != null && pkg.getMaxSph() != null) {
            BigDecimal rangeMid = pkg.getMinSph().add(pkg.getMaxSph()).divide(BigDecimal.valueOf(2));
            BigDecimal distanceFromMid = maxSphere.subtract(rangeMid).abs();
            suitability = suitability.subtract(distanceFromMid.multiply(BigDecimal.valueOf(0.1)));
        }

        return suitability.max(BigDecimal.ZERO);
    }

    @Transactional
    public Prescription savePrescription(UUID userId, Map<String, Object> prescriptionData) {
        Prescription prescription = new Prescription();
        // prescription.setUserId(userId); // TODO: Implement when Prescription entity
        // is properly set up
        prescription.setSphereRight(new BigDecimal(prescriptionData.get("sphereRight").toString()));
        prescription.setCylinderRight(new BigDecimal(prescriptionData.get("cylinderRight").toString()));
        prescription.setAxisRight(Integer.parseInt(prescriptionData.get("axisRight").toString()));
        prescription.setSphereLeft(new BigDecimal(prescriptionData.get("sphereLeft").toString()));
        prescription.setCylinderLeft(new BigDecimal(prescriptionData.get("cylinderLeft").toString()));
        prescription.setAxisLeft(Integer.parseInt(prescriptionData.get("axisLeft").toString()));
        prescription.setPd(new BigDecimal(prescriptionData.get("pd").toString()));
        prescription.setNote((String) prescriptionData.get("note"));
        prescription.setSource((String) prescriptionData.get("source"));

        return prescriptionRepository.save(prescription);
    }
}
