package com.eyeglasses.eyeglasses_store.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class AdminImageService {

    @Value("${app.upload.path:uploads}")
    private String uploadPath;

    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    public String uploadImage(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // Create directory if not exists
        Path uploadDir = Paths.get(uploadPath, folder);
        Files.createDirectories(uploadDir);

        // Save file
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return public URL
        return baseUrl + "/" + uploadPath + "/" + folder + "/" + filename;
    }

    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || !imageUrl.startsWith(baseUrl)) {
            return;
        }

        String relativePath = imageUrl.substring(baseUrl.length() + 1);
        Path filePath = Paths.get(relativePath);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}

