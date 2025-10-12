package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.lens.LensPackage;
import com.eyeglasses.eyeglasses_store.entity.lens.Prescription;
import com.eyeglasses.eyeglasses_store.repository.lens.LensPackageRepository;
import com.eyeglasses.eyeglasses_store.repository.lens.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminLensValidationService {

    private final LensPackageRepository lensPackageRepository;
    private final PrescriptionRepository prescriptionRepository;

    public AdminLensValidationService(LensPackageRepository lensPackageRepository,
            PrescriptionRepository prescriptionRepository) {
        this.lensPackageRepository = lensPackageRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public Map<String, Object> validateLensPackage(UUID lensPackageId) {
        LensPackage lensPackage = lensPackageRepository.findById(lensPackageId).orElseThrow();

        Map<String, Object> result = Map.of(
                "lensPackageId", lensPackageId,
                "code", lensPackage.getCode(),
                "name", lensPackage.getName(),
                "isValid", true,
                "warnings", List.<String>of(),
                "errors", List.<String>of());

        // Validate refractive index
        if (lensPackage.getRefractiveIdx() == null || lensPackage.getRefractiveIdx().compareTo(BigDecimal.ZERO) <= 0) {
            return Map.of(
                    "lensPackageId", lensPackageId,
                    "code", lensPackage.getCode(),
                    "name", lensPackage.getName(),
                    "isValid", false,
                    "warnings", List.<String>of(),
                    "errors", List.of("Refractive index must be greater than 0"));
        }

        // Validate price ranges
        if (lensPackage.getRetailPrice() == null || lensPackage.getRetailPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return Map.of(
                    "lensPackageId", lensPackageId,
                    "code", lensPackage.getCode(),
                    "name", lensPackage.getName(),
                    "isValid", false,
                    "warnings", List.<String>of(),
                    "errors", List.of("Retail price must be greater than 0"));
        }

        // Check SPH range consistency
        if (lensPackage.getMinSph() != null && lensPackage.getMaxSph() != null) {
            if (lensPackage.getMinSph().compareTo(lensPackage.getMaxSph()) > 0) {
                return Map.of(
                        "lensPackageId", lensPackageId,
                        "code", lensPackage.getCode(),
                        "name", lensPackage.getName(),
                        "isValid", false,
                        "warnings", List.<String>of(),
                        "errors", List.of("Min SPH cannot be greater than Max SPH"));
            }
        }

        // Check CYL range consistency
        if (lensPackage.getMinCyl() != null && lensPackage.getMaxCyl() != null) {
            if (lensPackage.getMinCyl().compareTo(lensPackage.getMaxCyl()) > 0) {
                return Map.of(
                        "lensPackageId", lensPackageId,
                        "code", lensPackage.getCode(),
                        "name", lensPackage.getName(),
                        "isValid", false,
                        "warnings", List.<String>of(),
                        "errors", List.of("Min CYL cannot be greater than Max CYL"));
            }
        }

        // Check for existing prescriptions that might be incompatible
        List<Prescription> incompatiblePrescriptions = prescriptionRepository.findAll().stream()
                .filter(prescription -> !isPrescriptionCompatible(prescription, lensPackage))
                .toList();

        if (!incompatiblePrescriptions.isEmpty()) {
            return Map.of(
                    "lensPackageId", lensPackageId,
                    "code", lensPackage.getCode(),
                    "name", lensPackage.getName(),
                    "isValid", true,
                    "warnings",
                    List.of("Found " + incompatiblePrescriptions.size() + " prescriptions that may be incompatible"),
                    "errors", List.<String>of());
        }

        return result;
    }

    private boolean isPrescriptionCompatible(Prescription prescription, LensPackage lensPackage) {
        // Check SPH compatibility
        if (lensPackage.getMinSph() != null && lensPackage.getMaxSph() != null) {
            BigDecimal rightSph = prescription.getSphereRight();
            BigDecimal leftSph = prescription.getSphereLeft();

            if (rightSph != null && (rightSph.compareTo(lensPackage.getMinSph()) < 0
                    || rightSph.compareTo(lensPackage.getMaxSph()) > 0)) {
                return false;
            }
            if (leftSph != null && (leftSph.compareTo(lensPackage.getMinSph()) < 0
                    || leftSph.compareTo(lensPackage.getMaxSph()) > 0)) {
                return false;
            }
        }

        // Check CYL compatibility
        if (lensPackage.getMinCyl() != null && lensPackage.getMaxCyl() != null) {
            BigDecimal rightCyl = prescription.getCylinderRight();
            BigDecimal leftCyl = prescription.getCylinderLeft();

            if (rightCyl != null && (rightCyl.compareTo(lensPackage.getMinCyl()) < 0
                    || rightCyl.compareTo(lensPackage.getMaxCyl()) > 0)) {
                return false;
            }
            if (leftCyl != null && (leftCyl.compareTo(lensPackage.getMinCyl()) < 0
                    || leftCyl.compareTo(lensPackage.getMaxCyl()) > 0)) {
                return false;
            }
        }

        return true;
    }

    public Map<String, Object> validateAllLensPackages() {
        List<LensPackage> allPackages = lensPackageRepository.findAll();
        List<Map<String, Object>> results = allPackages.stream()
                .map(pkg -> validateLensPackage(pkg.getId()))
                .toList();

        long validCount = results.stream().mapToLong(r -> (Boolean) r.get("isValid") ? 1 : 0).sum();
        long invalidCount = results.size() - validCount;

        return Map.of(
                "total", allPackages.size(),
                "valid", validCount,
                "invalid", invalidCount,
                "results", results);
    }
}

