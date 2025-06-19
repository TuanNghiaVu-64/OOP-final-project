Dưới đây là danh mục tất cả các tệp trong dự án **airline-system** và mục đích sử dụng của từng tệp.
Các tệp được nhóm theo **package/thư mục** để bạn có thể dễ dàng lướt qua hoặc tìm sâu vào chi tiết.

---

### 1. `src/main/java/com/hustairline/airline_system`
**`AirlineSystemApplication.java`** – Điểm khởi chạy của ứng dụng Spring Boot (chứa `main` method), khởi tạo toàn bộ ứng dụng.

---

### 2. `config`
| Tệp | Mục đích |
|------|----------|
| `SecurityConfig.java` | Cấu hình Spring Security: phân quyền URL, trang đăng nhập, mã hóa mật khẩu. |
| `DatabaseConfig.java` | Đọc các thuộc tính `spring.datasource.*` và cung cấp URL / username / password kết nối DB. |
| `CustomAuthenticationSuccessHandler.java` | Điều hướng người dùng sau khi đăng nhập dựa trên vai trò (ADMIN, MANAGER, CUSTOMER). |

---

### 3. `controller` (lớp ánh xạ HTTP request tới logic nghiệp vụ)
| Tệp | Các End-point chính |
|------|---------------------|
| `AuthController.java` | `/register`, `/register/save` – đăng ký tài khoản khách hàng. |
| `LoginController.java` | `/login` (GET) – trả về giao diện đăng nhập. |
| `UserController.java` | Quản lý người dùng bởi admin (`/users/list`, `/users/approve/{id}`, …). |
| `PlaneController.java` | Thêm, liệt kê, duyệt, xóa máy bay. |
| `SeatAssignmentController.java` | Gán loại ghế cho ghế máy bay; giao diện quản lý ghế. |
| `SeatTypeController.java` | CRUD & luồng duyệt cho loại ghế. |
| `LocationController.java` | CRUD sân bay / thành phố. |
| `FlightController.java` | Thêm, liệt kê, duyệt, xóa chuyến bay; hỗ trợ tìm kiếm cho form. |
| `FlightSeatTypeController.java` | Thiết lập, xem, duyệt giá theo loại ghế của chuyến bay. |
| `CustomerController.java` | Bảng điều khiển của khách hàng: tìm chuyến, đặt ghế, xem đặt chỗ. |
| `CustomErrorController.java` | Xử lý route `/error`; hiển thị trang lỗi thân thiện. |

---

### 4. `model` (các đối tượng ánh xạ trực tiếp với bảng DB)
| Tệp | Đại diện cho |
|------|--------------|
| `User.java` | Hàng trong bảng `users` (id, username, password, role, approved). |
| `Plane.java` | Hàng trong bảng `planes`; kế thừa `BaseEntity` để có timestamp, v.v. |
| `Seat.java` | Ghế vật lý trên máy bay. |
| `SeatType.java` | Định nghĩa loại ghế (Business, Economy...) chờ duyệt. |
| `Location.java` | Sân bay / thành phố. |
| `Flight.java` | Chuyến bay đã lên lịch (máy bay, điểm đi, đến, thời gian). |
| `FlightSeatType.java` | Giá của một loại ghế cụ thể trong chuyến bay. |
| `FlightSeatAssignment.java` | Một ghế cụ thể trên chuyến bay (liên kết Seat → FlightSeatType → tình trạng). |
| `Booking.java` | Đặt vé của khách hàng & trạng thái thanh toán. |
| `BaseEntity.java` | Lớp trừu tượng: `id`, `createdAt`, `updatedAt`, `isActive`, `softDelete()`, `isValid()`. |

---

### 5. `repository` (truy cập trực tiếp JDBC; tất cả kế thừa `AbstractJdbcRepository`)
| Tệp | Phạm vi CRUD |
|------|---------------|
| `AbstractJdbcRepository.java` | Cung cấp `getConnection()` thông qua `DatabaseConfig`. |
| `PlaneRepository.java` | Bảng `planes` & truy vấn hỗ trợ đếm ghế. |
| `SeatRepository.java` | Bảng `seats`; gán loại ghế; thống kê ghế đã gán. |
| `SeatTypeRepository.java` | Bảng `seat_types` với kiểm tra trùng tên và duyệt. |
| `LocationRepository.java` | Bảng `locations`. |
| `FlightRepository.java` | Bảng `flights`; truy vấn join để hiển thị chi tiết. |
| `FlightSeatTypeRepository.java` | Bảng `flight_seat_types` (giá); xử lý duyệt giá. |
| `FlightSeatAssignmentRepository.java` | Bảng `flight_seat_assignments`; tạo ghế chuyến bay, chuyển đổi tình trạng. |
| `BookingRepository.java` | Bảng `bookings` – tạo, liệt kê, hủy, đếm trùng lặp. |
| `UserRepository.java` | Bảng `users`: đăng nhập, duyệt người dùng admin/manager, danh sách chờ duyệt. |

---

### 6. `service`
| Tệp | Vai trò |
|------|----------|
| `CustomUserDetailsService.java` | Chuyển hàng `User` thành `UserDetails` của Spring Security để xác thực. |
| `ReferentialIntegrityService.java` | Kiểm tra quy tắc "có thể xóa?" (VD: chuyến bay có giá → không thể xóa). |
| `PlaneValidationService.java` | Triển khai `ValidationService<Plane>` (theo mẫu Strategy) cho máy bay. |
| `ValidationService.java` | Giao diện kiểm tra hợp lệ tổng quát. |
| `NotificationService.java` | Mẫu Observer để phát sự kiện trong hệ thống. |
| `DefaultNotificationService.java` | Chủ thể cụ thể quản lý danh sách observer. |
| `AuditLogObserver.java` | Observer ghi log tất cả sự kiện hệ thống ra console (dạng thử nghiệm). |

---

### 7. `resources/application.properties`
Lưu thông tin: URL DB, username, password, chế độ JPA DDL, cổng server, v.v. (Spring đọc thông qua `@Value` trong `DatabaseConfig`).

---

### 8. `resources/templates` (giao diện Thymeleaf)
| Thư mục | Các trang giao diện |
|----------|----------------------|
| `root` | `login.html`, `dashboard.html`, dashboard cho từng vai trò, `seat-management.html`, v.v. |
| `flights/` | Thêm/xem/liệt kê chuyến bay. |
| `planes/` | Thêm/liệt kê/duyệt máy bay. |
| `seat-types/` | Thêm/liệt kê/duyệt loại ghế. |
| `flight-pricing/` | Liệt kê/duyệt/thiết lập giá. |
| `customer/` | Trang tìm kiếm và đặt vé của khách hàng. |
| `fragments/` | `header.html` – thanh điều hướng dùng chung. |

---

### 9. `resources/static`
- `css/` – style cho từng nhóm trang.
- `js/` – JavaScript đơn thuần cho kiểm tra form, gọi AJAX xóa, dashboard, v.v.
- `login.js` – xử lý form đăng nhập.
- `pages/*.js` – hàm hỗ trợ CRUD/AJAX cho trang quản lý/admin.
- `utils/api.js` – wrapper `fetchJson()` tổng quát, xử lý lỗi.

---

### 10. `Documentation`
| Tệp | Mục đích |
|------|----------|
| `OOP_IMPLEMENTATION_GUIDE.md` | Hướng dẫn toàn bộ phần mở rộng OOP & mẫu thiết kế đã áp dụng. |
| `OOP_TECHNIQUES_EXPLAINED.md` | Phân tích sâu từng kỹ thuật OOP được sử dụng. |
| `OOP_CORE_PRINCIPLES.md` | Tóm tắt trọng tâm về Trừu tượng / Đóng gói / Kế thừa / Đa hình. |

---

### 📌 **Cách đọc danh sách này**

- Muốn debug truy vấn? → đến `repository` tương ứng.  
- Trang web hiển thị thế nào? → bắt đầu từ `controller` → `repository` → `template` → `JS`.  
- Thêm một thực thể mới? → tạo `model` → `repository` (kế thừa `AbstractJdbcRepository`) → `validator` → `controller` → `template`.

📍 **Sơ đồ này giúp bạn không bao giờ phải hỏi “tôi nên tìm ở đâu?” cho bất kỳ tính năng nào trong codebase.**

