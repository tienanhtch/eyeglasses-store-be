package com.eyeglasses.eyeglasses_store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Cấu hình để serve static files từ thư mục uploads/ (ảnh sản phẩm, v.v.)
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.path:uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Lấy đường dẫn tuyệt đối của thư mục uploads
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath();
        String uploadDirUri = uploadDir.toUri().toString();

        // Map URL /uploads/** -> thư mục uploads/ trên disk
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadDirUri);
    }
}
