# 📸 CKC Digital Store - E-commerce App

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple?style=for-the-badge&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-4285F4?style=for-the-badge&logo=android)
![NodeJS](https://img.shields.io/badge/Node.js-Express-green?style=for-the-badge&logo=node.js)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql)

**CKC Digital** là dự án ứng dụng thương mại điện tử trên nền tảng Android, được xây dựng theo kiến trúc Client-Server. Ứng dụng chuyên cung cấp các sản phẩm máy ảnh, ống kính và phụ kiện công nghệ (đặc biệt là hệ sinh thái Sony Alpha), mang lại trải nghiệm mua sắm mượt mà với giao diện hiện đại.

---

## 📱 Demo Giao Diện

| Màn hình chính | Chi tiết sản phẩm | Giỏ hàng | Lịch sử đơn hàng |
|:---:|:---:|:---:|:---:|
| <img src="https://res.cloudinary.com/dczhi464d/image/upload/v1769253060/Screenshot_20260120_203514_csj7ch.png" width="200"> | <img src="https://res.cloudinary.com/dczhi464d/image/upload/v1769253056/Screenshot_20260120_203916_uqzixh.png" width="200"> | <img src="https://res.cloudinary.com/dczhi464d/image/upload/v1769254280/Screenshot_20260120_203834_i6ydfl.png" width="200"> | <img src="https://res.cloudinary.com/dczhi464d/image/upload/v1769253054/Screenshot_20260120_203935_ud9ixy.png" width="200"> |


---

## 🚧 Trạng Thái & Lộ Trình Phát Triển

Dự án hiện đang ở giai đoạn **Beta**. Chúng tôi đang tập trung hoàn thiện các tính năng cốt lõi và tối ưu trải nghiệm người dùng.

### ⏳ Đang phát triển (Coming Soon)
- [ ] Tích hợp cổng thanh toán Online (VNPAY / PayPal)
- [ ] Thông báo đẩy (Push Notifications) khi trạng thái đơn hàng thay đổi
- [ ] Chế độ tối (Dark Mode) cho toàn bộ ứng dụng
- [ ] Chat trực tuyến với Admin
- [ ] Tối ưu hóa hiệu năng load ảnh và caching

---

## ✨ Tính Năng Chính

### 👤 Khách hàng (User)
- **Xác thực:** Đăng nhập, Đăng ký, Quản lý hồ sơ, Đổi mật khẩu.
- **Sản phẩm:**
  - Xem danh sách sản phẩm theo danh mục (FullFrame, APS-C, Lens G/GM...).
  - Tìm kiếm sản phẩm theo tên.
  - Sắp xếp sản phẩm theo giá.
  - Xem chi tiết: Thông số kỹ thuật, mô tả, thư viện ảnh (Slider).
- **Mua sắm:**
  - Thêm vào giỏ hàng, cập nhật số lượng.
  - Thanh toán (Checkout) với đa dạng phương thức (COD, Banking, Momo).
  - Quản lý địa chỉ nhận hàng.
- **Tương tác:**
  - Yêu thích sản phẩm (Wishlist).
  - Đánh giá & bình luận sản phẩm sau khi mua.
  - Theo dõi lịch sử và trạng thái đơn hàng (Chờ xác nhận, Đang giao, Hoàn thành, Hủy).

### 🛠 Quản trị viên (Admin)
- **Dashboard:** Thống kê doanh thu trong ngày, số lượng đơn hàng chờ xử lý.
- **Quản lý đơn hàng:** Xem danh sách toàn bộ đơn hàng, cập nhật trạng thái đơn hàng (Duyệt đơn, Giao hàng, Hủy đơn).

---

## 🛠 Công Nghệ Sử Dụng

### 📱 Android Client (Frontend)
* **Ngôn ngữ:** Kotlin
* **UI Framework:** Jetpack Compose (Material Design 3)
* **Architecture:** MVVM (Model-View-ViewModel)
* **Networking:** Retrofit2, OkHttp, GSON
* **Image Loading:** Coil
* **Navigation:** Navigation Compose
* **State Management:** Kotlin Flow, LiveData, Coroutines

### 🖥 Backend Server
* **Runtime:** Node.js
* **Framework:** Express.js
* **Database:** MySQL
* **Libraries:** mysql2 (Connection Pooling), body-parser, cors

---

## 🗄 Cơ Sở Dữ Liệu (Database Schema)

Database: `ckcdigitalstore`

Hệ thống bao gồm các bảng chính:
- **users:** Thông tin tài khoản, phân quyền (Role).
- **products:** Thông tin sản phẩm, giá bán, hình ảnh.
- **categories:** Danh mục sản phẩm.
- **orders:** Lưu trữ thông tin đơn hàng tổng quan.
- **orderdetails:** Chi tiết từng sản phẩm trong đơn hàng.
- **reviews:** Đánh giá và bình luận của người dùng.
- **wishlist:** Sản phẩm yêu thích.
- **addresses:** Sổ địa chỉ giao hàng.
- **promotions:** Quản lý mã giảm giá/khuyến mãi.

---

## 🚀 Hướng Dẫn Cài Đặt

Dự án này là phần Client (Mobile App). Để ứng dụng hoạt động, bạn cần khởi chạy Backend Server trước.

### 1. Thiết lập Backend (Yêu cầu bắt buộc)
Mã nguồn Backend và hướng dẫn cài đặt chi tiết được lưu trữ tại repository riêng biệt:

👉 **https://github.com/hcngdanh/SERVER-API-CKC-DIGITAL.git**

*Hãy đảm bảo Server đã chạy (mặc định port 3000) trước khi tiếp tục.*

### 2. Thiết lập Android App
Yêu cầu: **Android Studio** phiên bản mới nhất.

1.  Clone và mở dự án này trong Android Studio.
2.  Cấu hình kết nối Server:
    - Mở file `RetrofitClient.kt` (trong package `services`).
    - Cập nhật `BASE_URL` tương ứng với địa chỉ IP máy chạy Server:
      - Máy ảo (Emulator): `http://10.0.2.2:3000/`
      - Máy thật: `http://<IPv4_Của_PC_Chạy_Server>:3000/`
3.  Đồng bộ Gradle (Sync Project) và chạy ứng dụng.

---

## 📬 API Endpoints Chính

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Đăng nhập tài khoản |
| `GET` | `/api/products` | Lấy danh sách sản phẩm |
| `GET` | `/api/products/:id` | Lấy chi tiết sản phẩm |
| `POST` | `/api/orders` | Tạo đơn hàng mới |
| `GET` | `/api/orders/user/:userId` | Lấy lịch sử mua hàng của User |
| `POST` | `/api/wishlist/toggle` | Thêm/Xóa sản phẩm yêu thích |
| `PUT` | `/api/orders/:orderId/status` | Cập nhật trạng thái đơn (Admin) |

---

## 👥 Thành Viên Thực Hiện

Dự án này được thực hiện bởi nhóm sinh viên lớp **[CĐ TH 23 DĐ D/Lập trình Di Động]**:

| STT | Họ và Tên | Mã Số Sinh Viên | Vai Trò Chính |
|:---:|:---|:---|:---|
| 1 | **Hồ Công Danh** | 0306231276 | Leader, Mobile App (UI/UX), Backend API |
| 2 | **Nguyễn Minh Chánh** | 0306231274 | Mobile App (UI/UX), Frontend Logic |
| 3 | **Trần Tuấn Cường** | 0306231008 | Mobile App (API Integration), Tester |
| 4 | **Nguyễn Phong Điền** | 0306231016 | Database Design, Documentation |

---


