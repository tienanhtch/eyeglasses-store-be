package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.LensService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.API_BASE_PATH + "/lens")
public class LensController {

    private final LensService lensService;

    public LensController(LensService lensService) {
        this.lensService = lensService;
    }

    @GetMapping("/packages")
    public ResponseEntity<List<Map<String, Object>>> listPackages() {
        return ResponseEntity.ok(lensService.listLensPackages());
    }

    @PostMapping("/packages/{lensPackageId}/validate")
    public ResponseEntity<Map<String, Object>> validate(@PathVariable("lensPackageId") UUID lensPackageId,
            @RequestBody Map<String, Object> presc) {
        return ResponseEntity.ok(lensService.validatePrescriptionForLens(lensPackageId, presc));
    }

    @PostMapping("/prescriptions")
    public ResponseEntity<Map<String, Object>> savePrescription(@RequestParam("userId") UUID userId,
            @RequestBody Map<String, Object> presc) {
        return ResponseEntity.ok(lensService.savePrescription(userId, presc));
    }

    private record QuoteRequest(UUID variantId, UUID lensPackageId, Map<String, Object> prescription) {
    }

    @PostMapping("/quote")
    public ResponseEntity<Map<String, Object>> quote(@RequestBody QuoteRequest body,
            com.eyeglasses.eyeglasses_store.repository.catalog.ProductVariantRepository variantRepository) {
        return ResponseEntity
                .ok(lensService.quote(body.variantId(), body.lensPackageId(), body.prescription(), variantRepository));
    }
}
