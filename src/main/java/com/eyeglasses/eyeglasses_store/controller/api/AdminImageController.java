package com.eyeglasses.eyeglasses_store.controller.api;

import com.eyeglasses.eyeglasses_store.constant.ApiConstants;
import com.eyeglasses.eyeglasses_store.service.AdminImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(ApiConstants.ADMIN_BASE + "/images")
public class AdminImageController {

    private final AdminImageService adminImageService;

    public AdminImageController(AdminImageService adminImageService) {
        this.adminImageService = adminImageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "products") String folder) {
        try {
            String imageUrl = adminImageService.uploadImage(file, folder);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "url", imageUrl,
                    "filename", file.getOriginalFilename()));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Failed to upload image: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteImage(@RequestParam("url") String imageUrl) {
        try {
            adminImageService.deleteImage(imageUrl);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Image deleted successfully"));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Failed to delete image: " + e.getMessage()));
        }
    }
}

