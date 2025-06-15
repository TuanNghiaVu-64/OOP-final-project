**Hệ Thống Đặt Vé Máy Bay HUSTAirline**

Một hệ thống quản lý và đặt vé máy bay toàn diện được xây dựng bằng Spring Boot, bao gồm điều khiển truy cập theo vai trò, quản lý chuyến bay, phân bổ chỗ ngồi và chức năng đặt vé.

🚀 **Tính Năng**

👥 **Quản Lý Người Dùng & Xác Thực**

Phân quyền theo vai trò: Vai trò Admin, Quản lý (Manager) và Khách hàng (Customer)

Đăng ký người dùng: Khách hàng có thể tự đăng ký, được duyệt tự động

Tạo tài khoản Admin: Admin có thể tạo tài khoản Admin/Manager, cần được Quản lý duyệt

Xác thực an toàn: Tích hợp Spring Security với chức năng đăng nhập/đăng xuất tùy chỉnh

✈️ **Quản Lý Chuyến Bay**

Tạo chuyến bay: Admin có thể thêm chuyến bay mới với giá vé

Quy trình phê duyệt chuyến bay: Quản lý xem xét và phê duyệt trước khi cho phép khách hàng đặt vé

Giá vé linh hoạt: Hệ thống giá động theo loại ghế tùy chuyến bay

Tìm kiếm chuyến bay: Khách hàng có thể tìm và lọc chuyến bay theo nhu cầu

🪑 **Quản Lý Ghế Ngồi**

Quản lý loại ghế: Tạo và quản lý các loại ghế (Phổ thông, Thương gia, Hạng nhất)

Phân bổ ghế: Giao diện phân bổ ghế trực quan cho từng cấu hình máy bay

Theo dõi tình trạng ghế: Cập nhật trạng thái ghế theo thời gian thực

Quản lý máy bay: Hỗ trợ nhiều loại máy bay (Nhỏ: 50 ghế, Lớn: 100 ghế)

📋 **Hệ Thống Đặt Vé**

Đặt vé: Khách hàng có thể chọn chuyến bay và chỗ ngồi để đặt vé

Quản lý đặt vé: Xem và quản lý đơn đặt vé cá nhân

Hủy vé: Hủy vé và tự động giải phóng ghế

Lịch sử đặt vé: Theo dõi toàn bộ lịch sử đặt vé

📊 **Tính Năng Quản Trị**

Trang tổng quan: Tùy biến theo vai trò người dùng

Phê duyệt tài khoản: Quản lý có thể duyệt hoặc từ chối tài khoản chờ

Giám sát hệ thống: Theo dõi chuyến bay, đơn đặt vé, và hoạt động người dùng

🛠️ **Công Nghệ Sử Dụng**

Backend: Spring Boot 3.5.0

Bảo mật: Spring Security 6

Cơ sở dữ liệu: PostgreSQL

Frontend: Thymeleaf, HTML5, CSS3, JavaScript

Công cụ build: Maven

Phiên bản Java: 23

📋 **Yêu Cầu Cài Đặt**

Java 23 trở lên

PostgreSQL 12 trở lên

Maven 3.6 trở lên

Git

🔧 **Cài Đặt & Khởi Chạy**

1. **Clone Dự Án**

2. **Cấu Hình Cơ Sở Dữ Liệu**

Tạo cơ sở dữ liệu PostgreSQL:

CREATE DATABASE HUSTAirline (tên nếu khác phải cập nhật code tương ứng);

Cập nhật application.properties:

spring.datasource.url=jdbc:postgresql://localhost:5432/HUSTAirline

spring.datasource.username=your_username

spring.datasource.password=your_password

(nếu chạy local với tên postgres và mật khẩu khác phải đổi tương ứng tất cả trong code)

3. **Build và Chạy**

Import vào eclipse hoặc chạy terminal

4. **Truy Cập Ứng Dụng**

Truy cập tại: http://localhost:8080

Giao diện đăng nhập mặc định sẽ hiển thị

5. **Các tài nguyên hỗ trợ**

HUSTAirline.sql: file tạo bảng cho DB, chỉ cần copy và dán vào query tool sau khi đã tạo DB mới (lưu ý tên phải trùng với trong code)

Reset.sql: Sau khi tạo bảng, muốn query sẽ phải thêm 'public.' trước các bảng, copy dòng đầu tiên vào query tool DB mới tạo để bỏ

Reset.sql: Trong quá trình chạy muốn xóa hết data, reset số đếm serial và thêm lại các users mặc định thì copy từ đây

DB.txt: Code DBML của DB, paste lên dbdiagram.io để vẽ ra 

👤 **Tài Khoản Mặc Định**

Customer: Tự đăng ký qua form

Admin: Tạo thủ công qua database hoặc dùng hệ thống

Mânger: Tài khoản cần được phê duyệt bởi quản lý

(Lưu ý: trong file reset.sql đã có 4 tài khoản mặc định)

🏗️ **Kiến Trúc Hệ Thống**

**Cấu Trúc Thư Mục**

src/

├── main/

│   ├── java/com/hustairline/airline_system/

│   │   ├── controller/         # REST controllers

│   │   ├── model/              # Entity models

│   │   ├── repository/         # Data access layer

│   │   ├── service/            # Business logic

│   │   └── config/             # Configuration classes

│   └── resources/

│       ├── templates/          # Thymeleaf templates

│       ├── static/             # CSS, JS, images

│       └── application.properties

🎯 **Vai Trò & Quyền Hạn**

🔴 **Admin**

Tạo tài khoản Manager/Admin

Xóa tài khoản Manager/Admin

Thêm máy bay vào hệ thống và chờ duyệt

Thêm các loại ghế ngồi và chờ duyệt

Gán các loại ghế cho máy bay

Thêm các địa điểm

Tạo chuyến bay và chờ duyệt

Cài đặt giá cho từng loại ghế trên từng chuyến bay và chờ duyệt

🟡 **Quản Lý**

Duyệt tất cả các yêu cầu từ admin

Xóa máy bay, loại ghế, chuyến bay,...

Phê duyệt giá vé

Giám sát hoạt động

Quản lý và duyệt các tài khoản

🟢 **Khách Hàng**

Đăng ký tài khoản

Tìm kiếm và đặt chuyến bay

Quản lý và hủy đơn đặt vé

Chọn chỗ khi đặt

Xem lịch sử đặt vé

📊 **Cơ Sở Dữ Liệu**

**Các Bảng Chính**

users: Người dùng

planes: Máy bay

seat_types: Loại ghế

seats: từng ghế trên từng máy bay

locations: các địa điểm dùng cho chuyến 

flights: Chuyến bay

flight_seat_types: giá vé từng loại ghế trên từng chuyến 

flight_seat_assignments: các ghế đã được gán giá để người dùng đặt 

bookings: Đặt vé

🔄 **Quy Trình Làm Việc**

(Để hiểu rõ hệ thống cần hiểu các quy trình tương ứng với thêm/sửa/xóa gì trong cơ sở dữ liệu)

**Tạo máy bay**

Admin tạo máy bay có tên và kích thước

Small 50 chỗ, Big 100 chỗ (hệ thống chỉ hỗ trợ 2 size với layout cố định mỗi size)

Gửi để manager duyệt

**Tạo loại ghế ngồi**

Admin tạo loại ghế với tên và các dịch vụ của ghế

Gửi để manager duyệt

**Gán chỗ cho máy bay**
Admin gán các loại chỗ đã duyệt vào vị trí cụ thể trên máy bay đã duyệt

**Tạo và Phê Duyệt Chuyến Bay**

Admin tạo chuyến bay với các địa điểm đã thêm, thời gian khởi hành và đến nơi

Manager duyệt hoặc từ chối

Sau khi duyệt, chuyến bay đã tồn tại những khách hàng chưa thể đặt

**Gán giá vé cho từng loại chỗ cho chuyến bay**

Admin gán với chuyến bay này giá vé loại ghế này là bao nhiêu

Gửi lên để manager duyệt

Nếu manaager duyệt thì khách hàng mới có thể đặt

**Tạo Tài Khoản Người Dùng**

Khách hàng: Tự đăng ký

Admin/Manager: Được tạo bởi Admin và cần phê duyệt

Manager: Phê duyệt hoặc từ chối

**Đặt Vé**

Khách hàng tìm chuyến

Chọn chuyến và ghế

Xác nhận và thanh toán

Nhận xác nhận đặt vé

Có thể hủy vé (giải phóng ghế)
