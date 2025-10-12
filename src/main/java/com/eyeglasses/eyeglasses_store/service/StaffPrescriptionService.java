package com.eyeglasses.eyeglasses_store.service;

import com.eyeglasses.eyeglasses_store.entity.lens.Prescription;
import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.repository.lens.PrescriptionRepository;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class StaffPrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppUserRepository userRepository;

    public StaffPrescriptionService(PrescriptionRepository prescriptionRepository,
            AppUserRepository userRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.userRepository = userRepository;
    }

    public List<Map<String, Object>> getPrescriptionsByUser(UUID userId) {
        List<Prescription> prescriptions = prescriptionRepository.findAll().stream()
                // .filter(prescription -> prescription.getUserId() != null &&
                // prescription.getUserId().equals(userId)) // TODO: Implement when Prescription
                // entity is properly set up
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();

        return prescriptions.stream().map(prescription -> {
            Map<String, Object> prescriptionData = new HashMap<>();
            prescriptionData.put("id", prescription.getId());
            prescriptionData.put("sphereRight", prescription.getSphereRight());
            prescriptionData.put("cylinderRight", prescription.getCylinderRight());
            prescriptionData.put("axisRight", prescription.getAxisRight());
            prescriptionData.put("sphereLeft", prescription.getSphereLeft());
            prescriptionData.put("cylinderLeft", prescription.getCylinderLeft());
            prescriptionData.put("axisLeft", prescription.getAxisLeft());
            prescriptionData.put("pd", prescription.getPd());
            prescriptionData.put("note", prescription.getNote());
            prescriptionData.put("source", prescription.getSource());
            prescriptionData.put("issuedAt", prescription.getIssuedAt());
            prescriptionData.put("createdAt", prescription.getCreatedAt());
            return prescriptionData;
        }).toList();
    }

    public Map<String, Object> getPrescriptionDetails(UUID prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId).orElseThrow();

        Map<String, Object> details = new HashMap<>();
        details.put("id", prescription.getId());
        details.put("sphereRight", prescription.getSphereRight());
        details.put("cylinderRight", prescription.getCylinderRight());
        details.put("axisRight", prescription.getAxisRight());
        details.put("sphereLeft", prescription.getSphereLeft());
        details.put("cylinderLeft", prescription.getCylinderLeft());
        details.put("axisLeft", prescription.getAxisLeft());
        details.put("pd", prescription.getPd());
        details.put("note", prescription.getNote());
        details.put("source", prescription.getSource());
        details.put("issuedAt", prescription.getIssuedAt());
        details.put("createdAt", prescription.getCreatedAt());

        // Mock customer info
        details.put("customer", Map.of(
                "name", "Customer " + prescription.getId().toString().substring(0, 8),
                "phone", "0123456789",
                "email", "customer@example.com"));

        return details;
    }

    @Transactional
    public Map<String, Object> createPrescription(UUID userId, Map<String, Object> prescriptionData) {
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
        prescription.setSource("MANUAL_ENTRY");
        prescription.setIssuedAt(LocalDate.now());

        prescription = prescriptionRepository.save(prescription);

        return Map.of(
                "success", true,
                "prescriptionId", prescription.getId(),
                "message", "Prescription created successfully");
    }

    @Transactional
    public Map<String, Object> updatePrescription(UUID prescriptionId, Map<String, Object> prescriptionData) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId).orElseThrow();

        if (prescriptionData.containsKey("sphereRight")) {
            prescription.setSphereRight(new BigDecimal(prescriptionData.get("sphereRight").toString()));
        }
        if (prescriptionData.containsKey("cylinderRight")) {
            prescription.setCylinderRight(new BigDecimal(prescriptionData.get("cylinderRight").toString()));
        }
        if (prescriptionData.containsKey("axisRight")) {
            prescription.setAxisRight(Integer.parseInt(prescriptionData.get("axisRight").toString()));
        }
        if (prescriptionData.containsKey("sphereLeft")) {
            prescription.setSphereLeft(new BigDecimal(prescriptionData.get("sphereLeft").toString()));
        }
        if (prescriptionData.containsKey("cylinderLeft")) {
            prescription.setCylinderLeft(new BigDecimal(prescriptionData.get("cylinderLeft").toString()));
        }
        if (prescriptionData.containsKey("axisLeft")) {
            prescription.setAxisLeft(Integer.parseInt(prescriptionData.get("axisLeft").toString()));
        }
        if (prescriptionData.containsKey("pd")) {
            prescription.setPd(new BigDecimal(prescriptionData.get("pd").toString()));
        }
        if (prescriptionData.containsKey("note")) {
            prescription.setNote((String) prescriptionData.get("note"));
        }

        prescriptionRepository.save(prescription);

        return Map.of(
                "success", true,
                "prescriptionId", prescriptionId,
                "message", "Prescription updated successfully");
    }

    @Transactional
    public Map<String, Object> deletePrescription(UUID prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId).orElseThrow();
        prescriptionRepository.delete(prescription);

        return Map.of(
                "success", true,
                "prescriptionId", prescriptionId,
                "message", "Prescription deleted successfully");
    }

    public Map<String, Object> validatePrescription(Map<String, Object> prescriptionData) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        try {
            BigDecimal sphereRight = new BigDecimal(prescriptionData.get("sphereRight").toString());
            BigDecimal sphereLeft = new BigDecimal(prescriptionData.get("sphereLeft").toString());
            BigDecimal cylinderRight = new BigDecimal(prescriptionData.get("cylinderRight").toString());
            BigDecimal cylinderLeft = new BigDecimal(prescriptionData.get("cylinderLeft").toString());
            Integer axisRight = Integer.parseInt(prescriptionData.get("axisRight").toString());
            Integer axisLeft = Integer.parseInt(prescriptionData.get("axisLeft").toString());
            BigDecimal pd = new BigDecimal(prescriptionData.get("pd").toString());

            // Validate SPH range
            if (sphereRight.abs().compareTo(BigDecimal.valueOf(20.0)) > 0) {
                errors.add("Right sphere value is too high");
            }
            if (sphereLeft.abs().compareTo(BigDecimal.valueOf(20.0)) > 0) {
                errors.add("Left sphere value is too high");
            }

            // Validate CYL range
            if (cylinderRight.abs().compareTo(BigDecimal.valueOf(6.0)) > 0) {
                errors.add("Right cylinder value is too high");
            }
            if (cylinderLeft.abs().compareTo(BigDecimal.valueOf(6.0)) > 0) {
                errors.add("Left cylinder value is too high");
            }

            // Validate axis range
            if (axisRight < 0 || axisRight > 180) {
                errors.add("Right axis must be between 0 and 180 degrees");
            }
            if (axisLeft < 0 || axisLeft > 180) {
                errors.add("Left axis must be between 0 and 180 degrees");
            }

            // Validate PD
            if (pd.compareTo(BigDecimal.valueOf(50)) < 0 || pd.compareTo(BigDecimal.valueOf(80)) > 0) {
                warnings.add("PD value seems unusual, please verify");
            }

            // Check for high prescriptions
            if (sphereRight.abs().compareTo(BigDecimal.valueOf(6.0)) > 0
                    || sphereLeft.abs().compareTo(BigDecimal.valueOf(6.0)) > 0) {
                warnings.add("High prescription detected - consider premium lens options");
            }

            // Check for astigmatism
            if (cylinderRight.abs().compareTo(BigDecimal.valueOf(0.5)) > 0
                    || cylinderLeft.abs().compareTo(BigDecimal.valueOf(0.5)) > 0) {
                warnings.add("Astigmatism detected - ensure proper axis alignment");
            }

        } catch (Exception e) {
            errors.add("Invalid prescription data format");
        }

        return Map.of(
                "valid", errors.isEmpty(),
                "errors", errors,
                "warnings", warnings);
    }

    public Map<String, Object> generatePrescriptionReport(UUID prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId).orElseThrow();

        Map<String, Object> report = new HashMap<>();
        report.put("prescriptionId", prescription.getId());
        report.put("generatedAt", OffsetDateTime.now());
        report.put("prescription", Map.of(
                "sphereRight", prescription.getSphereRight(),
                "cylinderRight", prescription.getCylinderRight(),
                "axisRight", prescription.getAxisRight(),
                "sphereLeft", prescription.getSphereLeft(),
                "cylinderLeft", prescription.getCylinderLeft(),
                "axisLeft", prescription.getAxisLeft(),
                "pd", prescription.getPd(),
                "note", prescription.getNote(),
                "source", prescription.getSource(),
                "issuedAt", prescription.getIssuedAt()));

        // Mock customer info
        report.put("customer", Map.of(
                "name", "Customer " + prescription.getId().toString().substring(0, 8),
                "phone", "0123456789",
                "email", "customer@example.com"));

        report.put("downloadUrl", "/api/v1/staff/prescriptions/" + prescriptionId + "/report.pdf");

        return report;
    }

    public List<Map<String, Object>> searchPrescriptions(String query, LocalDate fromDate, LocalDate toDate) {
        List<Prescription> prescriptions = prescriptionRepository.findAll().stream()
                .filter(prescription -> {
                    if (query != null && !query.trim().isEmpty()) {
                        String searchQuery = query.toLowerCase();
                        return prescription.getNote() != null
                                && prescription.getNote().toLowerCase().contains(searchQuery);
                    }
                    return true;
                })
                .filter(prescription -> {
                    if (fromDate != null && prescription.getIssuedAt() != null
                            && prescription.getIssuedAt().isBefore(fromDate)) {
                        return false;
                    }
                    if (toDate != null && prescription.getIssuedAt() != null
                            && prescription.getIssuedAt().isAfter(toDate)) {
                        return false;
                    }
                    return true;
                })
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();

        return prescriptions.stream().map(prescription -> {
            Map<String, Object> prescriptionData = new HashMap<>();
            prescriptionData.put("id", prescription.getId());
            prescriptionData.put("sphereRight", prescription.getSphereRight());
            prescriptionData.put("cylinderRight", prescription.getCylinderRight());
            prescriptionData.put("sphereLeft", prescription.getSphereLeft());
            prescriptionData.put("cylinderLeft", prescription.getCylinderLeft());
            prescriptionData.put("pd", prescription.getPd());
            prescriptionData.put("note", prescription.getNote());
            prescriptionData.put("source", prescription.getSource());
            prescriptionData.put("issuedAt", prescription.getIssuedAt());
            prescriptionData.put("createdAt", prescription.getCreatedAt());
            return prescriptionData;
        }).toList();
    }
}
