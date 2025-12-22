# Hướng dẫn Setup Backend

## Yêu cầu
- Java 17+
- MySQL 8.0+
- Maven

## Cài đặt

### 1. Tạo Database

Tạo database trong MySQL:
```sql
CREATE DATABASE eyeglasses_store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**Lưu ý:** Không cần tạo bảng thủ công, Hibernate sẽ tự động tạo!

### 2. Cấu hình Database

File: `src/main/resources/application.yaml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/eyeglasses_store?useSSL=false&serverTimezone=UTC
    username: root        # Đổi username nếu cần
    password:             # Thêm password nếu có
```

### 3. Chạy Backend

```bash
# Windows
mvnw.cmd spring-boot:run

# Mac/Linux
./mvnw spring-boot:run
```

Backend sẽ chạy tại: `http://localhost:8080`

## Dữ liệu khởi tạo

Khi chạy lần đầu, hệ thống sẽ tự động:

✅ **Tạo bảng** (Hibernate auto ddl-auto: update)
✅ **Seed dữ liệu khởi tạo:**

### Tài khoản đăng nhập

| Vai trò | Email | Password |
|---------|-------|----------|
| **Admin** | admin@eyeglasses.com | admin123 |
| **Staff** | staff@eyeglasses.com | staff123 |
| **Customer** | customer@example.com | password123 |

### Dữ liệu khác
- ✅ 3 Roles: ADMIN, STAFF, CUSTOMER
- ✅ 4 Categories: Kính cận, Kính râm, Phụ kiện, Tròng kính
- ✅ 3 Stores: Quận 1, Quận 3, Thủ Đức

## API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

### Authentication APIs
- POST `/auth/register` - Đăng ký tài khoản mới
- POST `/auth/login` - Đăng nhập

### Public APIs
- GET `/public/categories` - Lấy danh sách danh mục
- GET `/public/products` - Lấy danh sách sản phẩm
- GET `/public/products/{slug}` - Chi tiết sản phẩm
- GET `/public/products/search` - Tìm kiếm sản phẩm

### Admin APIs (Yêu cầu role ADMIN)
- `/admin/categories` - CRUD danh mục
- `/admin/products` - CRUD sản phẩm
- `/admin/orders` - Quản lý đơn hàng
- `/admin/users` - Quản lý người dùng
- `/admin/stores` - Quản lý cửa hàng

### Postman Collections
Xem folder `postman-collections/` để import vào Postman:
- `admin-apis.json` - Admin APIs
- `authentication.json` - Auth APIs
- `customer-apis.json` - Customer APIs
- `staff-apis.json` - Staff APIs

## Cấu trúc Database

### Core Tables
- `app_user` - Người dùng
- `role` - Vai trò
- `user_role` - Mapping user-role

### Catalog
- `category` - Danh mục
- `product` - Sản phẩm
- `product_variant` - Biến thể sản phẩm
- `product_image` - Hình ảnh sản phẩm

### Orders
- `order_tbl` - Đơn hàng
- `order_item` - Chi tiết đơn hàng
- `payment` - Thanh toán

### Others
- `store` - Cửa hàng
- `appointment` - Lịch hẹn
- `prescription` - Đơn thuốc kính
- `cart`, `cart_item` - Giỏ hàng
- `lens_package` - Gói tròng kính

## Troubleshooting

### Lỗi: "Access denied for user 'root'@'localhost'"
➡️ Kiểm tra username/password trong `application.yaml`

### Lỗi: "Unknown database 'eyeglasses_store'"
➡️ Chạy lệnh CREATE DATABASE ở bước 1

### Lỗi: "Table 'xxx' doesn't exist"
➡️ Đảm bảo `ddl-auto: update` trong application.yaml

### Backend không tự tạo bảng
➡️ Kiểm tra config:
```yaml
jpa:
  hibernate:
    ddl-auto: update  # KHÔNG phải 'none'!
```

### Port 8080 đã được sử dụng
➡️ Đổi port trong application.yaml:
```yaml
server:
  port: 8081  # hoặc port khác
```

## Development Tips

### Xem SQL queries
Đã bật `show-sql: true` trong config, xem console để debug SQL

### Hot reload
Spring DevTools đã được enable, code sẽ tự reload khi thay đổi

### Tắt auto seed data
Comment hoặc xóa file `DataLoader.java` nếu không muốn seed data mỗi lần restart

### Reset database
```sql
DROP DATABASE eyeglasses_store;
CREATE DATABASE eyeglasses_store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
Sau đó restart backend để tạo lại bảng và seed data

## Security

### JWT Configuration
JWT secret được config qua environment variable:
```bash
export JWT_SECRET=your-secret-key-here
export JWT_EXPIRES_MS=86400000  # 24 hours
```

Mặc định (development):
- Secret: `dev-secret`
- Expiry: 24 hours

### CORS
Đã config cho phép localhost:3000 (frontend) trong `SecurityConfig.java`

## Production Deployment

Khi deploy production:

1. **Đổi ddl-auto sang validate:**
```yaml
jpa:
  hibernate:
    ddl-auto: validate  # Không tự động sửa schema
```

2. **Sử dụng Flyway hoặc Liquibase** cho database migration

3. **Set JWT_SECRET environment variable** thật mạnh

4. **Disable DevTools:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

5. **Config CORS chặt chẽ** - không allow all origins

## Contact

Nếu gặp vấn đề, check logs trong console hoặc file logs/

Happy coding! 🚀

