package com.eyeglasses.eyeglasses_store.config.database;

import com.eyeglasses.eyeglasses_store.entity.catalog.Category;
import com.eyeglasses.eyeglasses_store.entity.catalog.Product;
import com.eyeglasses.eyeglasses_store.entity.catalog.ProductImage;
import com.eyeglasses.eyeglasses_store.entity.catalog.ProductVariant;
import com.eyeglasses.eyeglasses_store.entity.inventory.Inventory;
import com.eyeglasses.eyeglasses_store.entity.store.Store;
import com.eyeglasses.eyeglasses_store.entity.user.AppUser;
import com.eyeglasses.eyeglasses_store.entity.user.Role;
import com.eyeglasses.eyeglasses_store.repository.catalog.CategoryRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductImageRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductRepository;
import com.eyeglasses.eyeglasses_store.repository.catalog.ProductVariantRepository;
import com.eyeglasses.eyeglasses_store.repository.inventory.InventoryRepository;
import com.eyeglasses.eyeglasses_store.repository.store.StoreRepository;
import com.eyeglasses.eyeglasses_store.repository.user.AppUserRepository;
import com.eyeglasses.eyeglasses_store.repository.user.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * DataLoader để seed dữ liệu khởi tạo cho database
 */
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            RoleRepository roleRepository,
            AppUserRepository userRepository,
            CategoryRepository categoryRepository,
            StoreRepository storeRepository,
            ProductRepository productRepository,
            ProductVariantRepository variantRepository,
            ProductImageRepository imageRepository,
            InventoryRepository inventoryRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // 1. Tạo Roles nếu chưa có
            if (roleRepository.count() == 0) {
                System.out.println("🔄 Seeding Roles...");

                Role adminRole = new Role();
                adminRole.setCode("ADMIN");
                adminRole.setName("Administrator");
                roleRepository.save(adminRole);

                Role staffRole = new Role();
                staffRole.setCode("STAFF");
                staffRole.setName("Staff/KTV");
                roleRepository.save(staffRole);

                Role customerRole = new Role();
                customerRole.setCode("CUSTOMER");
                customerRole.setName("Customer");
                roleRepository.save(customerRole);

                System.out.println("✅ Created 3 roles: ADMIN, STAFF, CUSTOMER");
            }

            // 2. Tạo Admin user nếu chưa có
            if (userRepository.findByEmailIgnoreCase("admin@eyeglasses.com").isEmpty()) {
                System.out.println("🔄 Creating Admin user...");

                AppUser admin = new AppUser();
                admin.setEmail("admin@eyeglasses.com");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setFullName("Admin User");
                admin.setPhone("0123456789");
                admin.setActive(true);

                // Assign ADMIN role
                roleRepository.findByCode("ADMIN").ifPresent(role -> admin.getRoles().add(role));
                userRepository.save(admin);

                System.out.println("✅ Created Admin user:");
                System.out.println("   Email: admin@eyeglasses.com");
                System.out.println("   Password: admin123");
            }

            // 3. Tạo Staff user nếu chưa có
            if (userRepository.findByEmailIgnoreCase("staff@eyeglasses.com").isEmpty()) {
                System.out.println("🔄 Creating Staff user...");

                AppUser staff = new AppUser();
                staff.setEmail("staff@eyeglasses.com");
                staff.setPasswordHash(passwordEncoder.encode("staff123"));
                staff.setFullName("Staff User");
                staff.setPhone("0987654321");
                staff.setActive(true);

                // Assign STAFF role
                roleRepository.findByCode("STAFF").ifPresent(role -> staff.getRoles().add(role));
                userRepository.save(staff);

                System.out.println("✅ Created Staff user:");
                System.out.println("   Email: staff@eyeglasses.com");
                System.out.println("   Password: staff123");
            }

            // 4. Tạo Customer user nếu chưa có
            if (userRepository.findByEmailIgnoreCase("customer@example.com").isEmpty()) {
                System.out.println("🔄 Creating Customer user...");

                AppUser customer = new AppUser();
                customer.setEmail("customer@example.com");
                customer.setPasswordHash(passwordEncoder.encode("password123"));
                customer.setFullName("Nguyễn Văn A");
                customer.setPhone("0901234567");
                customer.setActive(true);

                // Assign CUSTOMER role
                roleRepository.findByCode("CUSTOMER").ifPresent(role -> customer.getRoles().add(role));
                userRepository.save(customer);

                System.out.println("✅ Created Customer user:");
                System.out.println("   Email: customer@example.com");
                System.out.println("   Password: password123");
            }

            // 5. Tạo Categories nếu chưa có
            if (categoryRepository.count() == 0) {
                System.out.println("🔄 Seeding Categories...");

                Category kinhCan = new Category();
                kinhCan.setSlug("kinh-can");
                kinhCan.setName("Kính cận");
                kinhCan.setDescription("Kính cận thị cho người cận");
                kinhCan.setSortOrder(1);
                kinhCan.setActive(true);
                categoryRepository.save(kinhCan);

                Category kinhRam = new Category();
                kinhRam.setSlug("kinh-ram");
                kinhRam.setName("Kính râm");
                kinhRam.setDescription("Kính mát thời trang");
                kinhRam.setSortOrder(2);
                kinhRam.setActive(true);
                categoryRepository.save(kinhRam);

                Category phuKien = new Category();
                phuKien.setSlug("phu-kien");
                phuKien.setName("Phụ kiện");
                phuKien.setDescription("Phụ kiện cho kính mắt");
                phuKien.setSortOrder(3);
                phuKien.setActive(true);
                categoryRepository.save(phuKien);

                Category trongKinh = new Category();
                trongKinh.setSlug("trong-kinh");
                trongKinh.setName("Tròng kính");
                trongKinh.setDescription("Các loại tròng kính");
                trongKinh.setSortOrder(4);
                trongKinh.setActive(true);
                categoryRepository.save(trongKinh);

                System.out.println("✅ Created 4 categories");
            }

            // 6. Tạo Stores nếu chưa có
            if (storeRepository.count() == 0) {
                System.out.println("🔄 Seeding Stores...");

                Store store1 = new Store();
                store1.setCode("ST001");
                store1.setName("QuangVu - Cửa hàng Quận 1");
                store1.setAddress("123 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP.HCM");
                store1.setPhone("0283456789");
                store1.setOpenHours("8:00-20:00");
                store1.setLat(new BigDecimal("10.7769"));
                store1.setLng(new BigDecimal("106.7009"));
                store1.setActive(true);
                store1 = storeRepository.save(store1);

                Store store2 = new Store();
                store2.setCode("ST002");
                store2.setName("QuangVu - Cửa hàng Quận 3");
                store2.setAddress("456 Đường Võ Văn Tần, Phường 5, Quận 3, TP.HCM");
                store2.setPhone("0283456790");
                store2.setOpenHours("8:30-21:00");
                store2.setLat(new BigDecimal("10.7870"));
                store2.setLng(new BigDecimal("106.6920"));
                store2.setActive(true);
                store2 = storeRepository.save(store2);

                Store store3 = new Store();
                store3.setCode("ST003");
                store3.setName("QuangVu - Cửa hàng Thủ Đức");
                store3.setAddress("789 Đường Võ Văn Ngân, Phường Linh Chiểu, Thủ Đức, TP.HCM");
                store3.setPhone("0283456791");
                store3.setOpenHours("9:00-21:00");
                store3.setLat(new BigDecimal("10.8505"));
                store3.setLng(new BigDecimal("106.7717"));
                store3.setActive(true);
                store3 = storeRepository.save(store3);

                System.out.println("✅ Created 3 stores:");
                System.out.println("   Store 1 UUID: " + store1.getId());
                System.out.println("   Store 2 UUID: " + store2.getId());
                System.out.println("   Store 3 UUID: " + store3.getId());
            }

            // 7. Tạo Products nếu chưa có
            if (productRepository.count() == 0) {
                System.out.println("🔄 Seeding Products...");

                // Get stores for inventory
                List<Store> stores = storeRepository.findAll();
                Store mainStore = !stores.isEmpty() ? stores.get(0) : null;

                Category kinhCan = categoryRepository.findBySlug("kinh-can").orElse(null);
                Category kinhRam = categoryRepository.findBySlug("kinh-ram").orElse(null);
                Category phuKien = categoryRepository.findBySlug("phu-kien").orElse(null);

                // Product 1: Gọng kính cận Titanium
                Product p1 = new Product();
                p1.setSlug("gong-kinh-can-titanium-tk001");
                p1.setName("Gọng kính cận Titanium TK001");
                p1.setDescription("Gọng kính titan siêu nhẹ, bền bỉ, thiết kế hiện đại phù hợp với mọi gương mặt");
                p1.setBrand("Titanium Pro");
                p1.setMaterial("Titanium");
                p1.setFrameShape("Oval");
                p1.setSeoTitle("Gọng kính cận Titanium TK001 - Nhẹ, Bền, Sang Trọng");
                p1.setSeoDescription(
                        "Gọng kính titanium cao cấp TK001 với thiết kế hiện đại, siêu nhẹ chỉ 8g. Phù hợp cho cả nam và nữ.");
                p1.setPublished(true);
                if (kinhCan != null)
                    p1.getCategories().add(kinhCan);
                p1 = productRepository.save(p1);

                // Images cho Product 1
                createImage(imageRepository, p1, "https://images.unsplash.com/photo-1574258495973-f010dfbb5371?w=800",
                        "Gọng kính Titanium TK001", 1);
                createImage(imageRepository, p1, "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800",
                        "Gọng kính Titanium TK001 - Góc 2", 2);

                // Variants cho Product 1
                ProductVariant v1_1 = createVariant(variantRepository, p1, "TK001-BLK-52", "Đen", 52, 18, 140,
                        new BigDecimal("1500000"),
                        new BigDecimal("1800000"), null);
                ProductVariant v1_2 = createVariant(variantRepository, p1, "TK001-SIL-52", "Bạc", 52, 18, 140,
                        new BigDecimal("1500000"),
                        new BigDecimal("1800000"), null);
                ProductVariant v1_3 = createVariant(variantRepository, p1, "TK001-BLU-54", "Xanh Navy", 54, 19, 142,
                        new BigDecimal("1500000"), new BigDecimal("1800000"), null);

                // Tạo inventory cho Product 1
                if (mainStore != null) {
                    createInventory(inventoryRepository, mainStore, v1_1, 10);
                    createInventory(inventoryRepository, mainStore, v1_2, 15);
                    createInventory(inventoryRepository, mainStore, v1_3, 8);
                    System.out.println("✅ Created inventory for Product 1: 10, 15, 8");
                } else {
                    System.out.println("⚠️ No store found, skipping inventory creation");
                }

                // Product 2: Kính râm Ray-Ban style
                Product p2 = new Product();
                p2.setSlug("kinh-ram-rayban-style-rb2140");
                p2.setName("Kính râm Wayfarer Classic RB2140");
                p2.setDescription("Kính râm phong cách Wayfarer cổ điển, chống tia UV400, mắt kính polarized cao cấp");
                p2.setBrand("Rayban Style");
                p2.setMaterial("Acetate");
                p2.setFrameShape("Wayfarer");
                p2.setSeoTitle("Kính râm Wayfarer RB2140 - Phong cách cổ điển");
                p2.setSeoDescription(
                        "Kính râm Wayfarer RB2140 với thiết kế iconic, chống tia UV400, phù hợp mọi phong cách");
                p2.setPublished(true);
                p2.setBestSeller(true); // Đánh dấu là bestseller
                if (kinhRam != null)
                    p2.getCategories().add(kinhRam);
                p2 = productRepository.save(p2);

                // Images cho Product 2
                createImage(imageRepository, p2, "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=800",
                        "Kính râm Wayfarer RB2140", 1);
                createImage(imageRepository, p2, "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800",
                        "Kính râm Wayfarer RB2140 - Góc 2", 2);

                // Variants cho Product 2
                ProductVariant v2_1 = createVariant(variantRepository, p2, "RB2140-BLK-50", "Đen bóng", 50, 22, 150,
                        new BigDecimal("800000"),
                        new BigDecimal("1200000"), new BigDecimal("999000"));
                ProductVariant v2_2 = createVariant(variantRepository, p2, "RB2140-TOR-50", "Tortoise", 50, 22, 150,
                        new BigDecimal("800000"),
                        new BigDecimal("1200000"), new BigDecimal("999000"));
                ProductVariant v2_3 = createVariant(variantRepository, p2, "RB2140-BRN-52", "Nâu", 52, 22, 150,
                        new BigDecimal("800000"),
                        new BigDecimal("1250000"), new BigDecimal("1050000"));

                // Tạo inventory cho Product 2
                if (mainStore != null) {
                    createInventory(inventoryRepository, mainStore, v2_1, 20);
                    createInventory(inventoryRepository, mainStore, v2_2, 12);
                    createInventory(inventoryRepository, mainStore, v2_3, 15);
                }

                // Product 3: Gọng kính nữ Gentle Monster
                Product p3 = new Product();
                p3.setSlug("gong-kinh-nu-gentle-monster-gm001");
                p3.setName("Gọng kính nữ Gentle Monster GM001");
                p3.setDescription(
                        "Gọng kính thời trang cho nữ, thiết kế mắt mèo sang trọng, chất liệu kim loại cao cấp");
                p3.setBrand("Gentle Monster");
                p3.setMaterial("Metal Alloy");
                p3.setFrameShape("Cat Eye");
                p3.setSeoTitle("Gọng kính nữ Gentle Monster GM001 - Mắt mèo thời trang");
                p3.setSeoDescription("Gọng kính nữ GM001 thiết kế mắt mèo độc đáo, phong cách Hàn Quốc hiện đại");
                p3.setPublished(true);
                p3.setNew(true); // Đánh dấu là sản phẩm mới
                if (kinhCan != null)
                    p3.getCategories().add(kinhCan);
                p3 = productRepository.save(p3);

                // Images cho Product 3
                createImage(imageRepository, p3, "https://images.unsplash.com/photo-1577803645773-f96470509666?w=800",
                        "Gọng kính nữ Gentle Monster", 1);
                createImage(imageRepository, p3, "https://images.unsplash.com/photo-1622519407650-3df9883f76e8?w=800",
                        "Gọng kính nữ Gentle Monster - Góc 2", 2);

                // Variants cho Product 3
                ProductVariant v3_1 = createVariant(variantRepository, p3, "GM001-GLD-53", "Vàng hồng", 53, 17, 140,
                        new BigDecimal("1200000"), new BigDecimal("1600000"), null);
                ProductVariant v3_2 = createVariant(variantRepository, p3, "GM001-SIL-53", "Bạc", 53, 17, 140,
                        new BigDecimal("1200000"),
                        new BigDecimal("1600000"), null);

                // Tạo inventory cho Product 3
                if (mainStore != null) {
                    createInventory(inventoryRepository, mainStore, v3_1, 18);
                    createInventory(inventoryRepository, mainStore, v3_2, 22);
                }

                // Product 4: Gọng kính nam thể thao
                Product p4 = new Product();
                p4.setSlug("gong-kinh-nam-the-thao-sp100");
                p4.setName("Gọng kính nam thể thao SP100");
                p4.setDescription(
                        "Gọng kính thể thao cho nam, chất liệu TR90 siêu bền, chống va đập, phù hợp vận động");
                p4.setBrand("Sport Vision");
                p4.setMaterial("TR90");
                p4.setFrameShape("Rectangle");
                p4.setSeoTitle("Gọng kính nam thể thao SP100 - TR90 bền bỉ");
                p4.setSeoDescription("Gọng kính TR90 dành cho nam, thiết kế thể thao năng động, siêu nhẹ và bền");
                p4.setPublished(true);
                if (kinhCan != null)
                    p4.getCategories().add(kinhCan);
                p4 = productRepository.save(p4);

                // Images cho Product 4
                createImage(imageRepository, p4, "https://images.unsplash.com/photo-1556306535-0f09a537f0a3?w=800",
                        "Gọng kính nam thể thao SP100", 1);

                // Variants cho Product 4
                ProductVariant v4_1 = createVariant(variantRepository, p4, "SP100-BLK-56", "Đen mờ", 56, 19, 145,
                        new BigDecimal("600000"),
                        new BigDecimal("900000"), new BigDecimal("750000"));
                ProductVariant v4_2 = createVariant(variantRepository, p4, "SP100-GRY-56", "Xám", 56, 19, 145,
                        new BigDecimal("600000"),
                        new BigDecimal("900000"), new BigDecimal("750000"));
                ProductVariant v4_3 = createVariant(variantRepository, p4, "SP100-BLU-58", "Xanh", 58, 20, 145,
                        new BigDecimal("600000"),
                        new BigDecimal("950000"), new BigDecimal("800000"));

                // Tạo inventory cho Product 4
                if (mainStore != null) {
                    createInventory(inventoryRepository, mainStore, v4_1, 25);
                    createInventory(inventoryRepository, mainStore, v4_2, 30);
                    createInventory(inventoryRepository, mainStore, v4_3, 20);
                }

                // Product 5: Kính râm Aviator
                Product p5 = new Product();
                p5.setSlug("kinh-ram-aviator-av3025");
                p5.setName("Kính râm Aviator AV3025");
                p5.setDescription("Kính râm phi công kinh điển, gọng kim loại, mắt kính phân cực chống chói");
                p5.setBrand("Aviator Classic");
                p5.setMaterial("Metal");
                p5.setFrameShape("Aviator");
                p5.setSeoTitle("Kính râm Aviator AV3025 - Phong cách phi công");
                p5.setSeoDescription("Kính râm Aviator cổ điển với mắt kính polarized, thiết kế phi công huyền thoại");
                p5.setPublished(true);
                if (kinhRam != null)
                    p5.getCategories().add(kinhRam);
                p5 = productRepository.save(p5);

                // Images cho Product 5
                createImage(imageRepository, p5, "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800",
                        "Kính râm Aviator AV3025", 1);
                createImage(imageRepository, p5, "https://images.unsplash.com/photo-1473496169904-658ba7c44d8a?w=800",
                        "Kính râm Aviator AV3025 - Góc 2", 2);

                // Variants cho Product 5
                ProductVariant v5_1 = createVariant(variantRepository, p5, "AV3025-GLD-58", "Vàng", 58, 14, 135,
                        new BigDecimal("700000"),
                        new BigDecimal("1100000"), null);
                ProductVariant v5_2 = createVariant(variantRepository, p5, "AV3025-SIL-58", "Bạc", 58, 14, 135,
                        new BigDecimal("700000"),
                        new BigDecimal("1100000"), null);
                ProductVariant v5_3 = createVariant(variantRepository, p5, "AV3025-BLK-62", "Đen", 62, 14, 140,
                        new BigDecimal("700000"),
                        new BigDecimal("1150000"), null);

                // Tạo inventory cho Product 5
                if (mainStore != null) {
                    createInventory(inventoryRepository, mainStore, v5_1, 14);
                    createInventory(inventoryRepository, mainStore, v5_2, 16);
                    createInventory(inventoryRepository, mainStore, v5_3, 12);
                }

                // Product 6: Hộp đựng kính cao cấp
                Product p6 = new Product();
                p6.setSlug("hop-dung-kinh-cao-cap-leather");
                p6.setName("Hộp đựng kính da thật Leather Case");
                p6.setDescription("Hộp đựng kính bằng da thật cao cấp, bảo vệ kính tốt nhất, thiết kế sang trọng");
                p6.setBrand("Accessories Pro");
                p6.setMaterial("Genuine Leather");
                p6.setFrameShape(null);
                p6.setSeoTitle("Hộp đựng kính da thật cao cấp");
                p6.setSeoDescription("Hộp đựng kính da thật, bảo vệ kính an toàn, thiết kế sang trọng");
                p6.setPublished(true);
                if (phuKien != null)
                    p6.getCategories().add(phuKien);
                p6 = productRepository.save(p6);

                // Images cho Product 6
                createImage(imageRepository, p6, "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=800",
                        "Hộp đựng kính da", 1);

                // Variants cho Product 6
                ProductVariant v6_1 = createVariant(variantRepository, p6, "LC-BLK-001", "Đen", null, null, null,
                        new BigDecimal("150000"),
                        new BigDecimal("250000"), new BigDecimal("199000"));
                ProductVariant v6_2 = createVariant(variantRepository, p6, "LC-BRN-001", "Nâu", null, null, null,
                        new BigDecimal("150000"),
                        new BigDecimal("250000"), new BigDecimal("199000"));

                // Tạo inventory cho Product 6
                if (mainStore != null) {
                    createInventory(inventoryRepository, mainStore, v6_1, 50);
                    createInventory(inventoryRepository, mainStore, v6_2, 45);
                }

                // Product 7: Khăn lau kính microfiber
                Product p7 = new Product();
                p7.setSlug("khan-lau-kinh-microfiber");
                p7.setName("Khăn lau kính Microfiber cao cấp");
                p7.setDescription("Khăn lau kính sợi microfiber, không làm xước mắt kính, thấm hút tốt");
                p7.setBrand("Clean Vision");
                p7.setMaterial("Microfiber");
                p7.setFrameShape(null);
                p7.setSeoTitle("Khăn lau kính Microfiber cao cấp");
                p7.setSeoDescription("Khăn lau kính sợi microfiber an toàn, không xước kính, siêu thấm hút");
                p7.setPublished(true);
                if (phuKien != null)
                    p7.getCategories().add(phuKien);
                p7 = productRepository.save(p7);

                // Images cho Product 7
                createImage(imageRepository, p7, "https://images.unsplash.com/photo-1585399000684-d2f72660f092?w=800",
                        "Khăn lau kính Microfiber", 1);

                // Variants cho Product 7
                ProductVariant v7_1 = createVariant(variantRepository, p7, "MF-001", "Xanh dương", null, null, null,
                        new BigDecimal("20000"),
                        new BigDecimal("50000"), new BigDecimal("35000"));
                ProductVariant v7_2 = createVariant(variantRepository, p7, "MF-002", "Xám", null, null, null,
                        new BigDecimal("20000"),
                        new BigDecimal("50000"), new BigDecimal("35000"));

                // Tạo inventory cho Product 7
                if (mainStore != null) {
                    createInventory(inventoryRepository, mainStore, v7_1, 100);
                    createInventory(inventoryRepository, mainStore, v7_2, 80);
                }

                // Product 8: Gọng kính trong suốt
                Product p8 = new Product();
                p8.setSlug("gong-kinh-trong-suot-clear-frame");
                p8.setName("Gọng kính trong suốt Clear Frame");
                p8.setDescription("Gọng kính trong suốt thời trang, nhẹ nhàng thanh lịch, phù hợp mọi lứa tuổi");
                p8.setBrand("Modern Optic");
                p8.setMaterial("Acetate");
                p8.setFrameShape("Round");
                p8.setSeoTitle("Gọng kính trong suốt Clear Frame - Thời trang");
                p8.setSeoDescription("Gọng kính trong suốt trendy, thiết kế tròn nhẹ nhàng, phong cách Hàn Quốc");
                p8.setPublished(true);
                p8.setNew(true); // Đánh dấu là sản phẩm mới if (kinhCan != null)
                p8.getCategories().add(kinhCan);
                p8 = productRepository.save(p8);

                // Images cho Product 8
                createImage(imageRepository, p8, "https://images.unsplash.com/photo-1592962120796-18c1d3fa0638?w=800",
                        "Gọng kính trong suốt", 1);
                createImage(imageRepository, p8, "https://images.unsplash.com/photo-1622519407650-3df9883f76e8?w=800",
                        "Gọng kính trong suốt - Góc 2", 2);

                // Variants cho Product 8
                ProductVariant v8_1 = createVariant(variantRepository, p8, "CF-CLR-50", "Trong suốt", 50, 20, 140,
                        new BigDecimal("400000"),
                        new BigDecimal("650000"), new BigDecimal("550000"));
                ProductVariant v8_2 = createVariant(variantRepository, p8, "CF-PNK-50", "Hồng nhạt", 50, 20, 140,
                        new BigDecimal("400000"),
                        new BigDecimal("650000"), new BigDecimal("550000"));

                // Tạo inventory cho Product 8
                if (mainStore != null) {
                    createInventory(inventoryRepository, mainStore, v8_1, 28);
                    createInventory(inventoryRepository, mainStore, v8_2, 35);
                }

                // Product 9: Hộp đựng kính cao cấp
                Product p9 = new Product();
                p9.setSlug("hop-dung-kinh-cao-cap");
                p9.setName("Hộp đựng kính cứng cao cấp");
                p9.setDescription("Hộp đựng kính cứng chống va đập, bảo vệ kính tối ưu khi mang theo");
                p9.setBrand("Lens Guard");
                p9.setMaterial("Plastic ABS");
                p9.setFrameShape(null);
                p9.setSeoTitle("Hộp đựng kính cứng cao cấp - Bảo vệ tối ưu");
                p9.setSeoDescription("Hộp đựng kính cứng, chống va đập, thiết kế nhỏ gọn tiện lợi");
                p9.setPublished(true);
                if (phuKien != null)
                    p9.getCategories().add(phuKien);
                p9 = productRepository.save(p9);

                createImage(imageRepository, p9, "https://images.unsplash.com/photo-1556656793-08538906a9f8?w=800",
                        "Hộp đựng kính cứng", 1);

                ProductVariant v9_1 = createVariant(variantRepository, p9, "HOP-001", "Đen", null, null, null,
                        new BigDecimal("50000"),
                        new BigDecimal("120000"), new BigDecimal("99000"));
                ProductVariant v9_2 = createVariant(variantRepository, p9, "HOP-002", "Xanh navy", null, null, null,
                        new BigDecimal("50000"),
                        new BigDecimal("120000"), new BigDecimal("99000"));

                if (mainStore != null) {
                    createInventory(inventoryRepository, mainStore, v9_1, 150);
                    createInventory(inventoryRepository, mainStore, v9_2, 120);
                }

                // Product 10: Dây đeo kính
                Product p10 = new Product();
                p10.setSlug("day-deo-kinh-the-thao");
                p10.setName("Dây đeo kính thể thao");
                p10.setDescription("Dây đeo kính silicon mềm mại, chống trượt, phù hợp cho hoạt động thể thao");
                p10.setBrand("Sport Vision");
                p10.setMaterial("Silicon");
                p10.setFrameShape(null);
                p10.setSeoTitle("Dây đeo kính thể thao silicon");
                p10.setSeoDescription("Dây đeo kính chống trượt, mềm mại, an toàn cho hoạt động thể thao");
                p10.setPublished(true);
                if (phuKien != null)
                    p10.getCategories().add(phuKien);
                p10 = productRepository.save(p10);

                createImage(imageRepository, p10, "https://images.unsplash.com/photo-1577741314755-048d8525d31e?w=800",
                        "Dây đeo kính", 1);

                ProductVariant v10_1 = createVariant(variantRepository, p10, "DAY-001", "Đen", null, null, null,
                        new BigDecimal("15000"),
                        new BigDecimal("45000"), new BigDecimal("35000"));
                ProductVariant v10_2 = createVariant(variantRepository, p10, "DAY-002", "Xanh dương", null, null, null,
                        new BigDecimal("15000"),
                        new BigDecimal("45000"), new BigDecimal("35000"));
                ProductVariant v10_3 = createVariant(variantRepository, p10, "DAY-003", "Đỏ", null, null, null,
                        new BigDecimal("15000"),
                        new BigDecimal("45000"), new BigDecimal("35000"));

                if (mainStore != null) {
                    createInventory(inventoryRepository, mainStore, v10_1, 200);
                    createInventory(inventoryRepository, mainStore, v10_2, 180);
                    createInventory(inventoryRepository, mainStore, v10_3, 150);
                }

                // Product 11: Dung dịch vệ sinh kính
                Product p11 = new Product();
                p11.setSlug("dung-dich-ve-sinh-kinh");
                p11.setName("Dung dịch vệ sinh kính 100ml");
                p11.setDescription("Dung dịch vệ sinh kính chuyên dụng, không gây hại lớp phủ, làm sạch hoàn hảo");
                p11.setBrand("Lens Care");
                p11.setMaterial("Dung dịch");
                p11.setFrameShape(null);
                p11.setSeoTitle("Dung dịch vệ sinh kính 100ml");
                p11.setSeoDescription("Dung dịch làm sạch kính chuyên dụng, an toàn, hiệu quả");
                p11.setPublished(true);
                if (phuKien != null)
                    p11.getCategories().add(phuKien);
                p11 = productRepository.save(p11);

                createImage(imageRepository, p11, "https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=800",
                        "Dung dịch vệ sinh kính", 1);

                ProductVariant v11_1 = createVariant(variantRepository, p11, "DD-100ML", "Trong suốt", null, null, null,
                        new BigDecimal("30000"),
                        new BigDecimal("80000"), new BigDecimal("65000"));

                if (mainStore != null) {
                    createInventory(inventoryRepository, mainStore, v11_1, 250);
                }

                System.out.println("✅ Created 11 products with variants and inventory");
            }

            System.out.println("\n✨ Database initialization completed!");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🔐 Login credentials:");
            System.out.println("   Admin:    admin@eyeglasses.com / admin123");
            System.out.println("   Staff:    staff@eyeglasses.com / staff123");
            System.out.println("   Customer: customer@example.com / password123");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        };
    }

    private ProductVariant createVariant(ProductVariantRepository variantRepository, Product product, String sku,
            String color, Integer sizeMm, Integer bridgeMm, Integer templeMm,
            BigDecimal costPrice, BigDecimal retailPrice, BigDecimal salePrice) {
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(sku);
        variant.setColor(color);
        variant.setSizeMm(sizeMm);
        variant.setBridgeMm(bridgeMm);
        variant.setTempleMm(templeMm);
        variant.setCostPrice(costPrice);
        variant.setRetailPrice(retailPrice);
        variant.setSalePrice(salePrice);
        variant.setActive(true);
        return variantRepository.save(variant);
    }

    private void createInventory(InventoryRepository inventoryRepository, Store store, ProductVariant variant,
            int onHand) {
        Inventory inventory = new Inventory();
        inventory.setStore(store);
        inventory.setVariant(variant);
        inventory.setOnHand(onHand);
        inventory.setReserved(0);
        inventoryRepository.save(inventory);
    }

    private void createImage(ProductImageRepository imageRepository, Product product,
            String url, String alt, int sortOrder) {
        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setUrl(url);
        image.setAlt(alt);
        image.setSortOrder(sortOrder);
        imageRepository.save(image);
    }
}
