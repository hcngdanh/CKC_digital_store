# 📸 CKC Digital Store - E-commerce App

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple?style=for-the-badge&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-4285F4?style=for-the-badge&logo=android)
![NodeJS](https://img.shields.io/badge/Node.js-Express-green?style=for-the-badge&logo=node.js)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql)

**CKC Digital** is an Android e-commerce application project built on a Client-Server architecture. The application specializes in providing cameras, lenses, and tech accessories (especially the Sony Alpha ecosystem), delivering a seamless shopping experience with a modern interface.

---

## 📱 UI Demo

| Home Screen | Product Details | Cart | Order History |
|:---:|:---:|:---:|:---:|
| <img src="https://res.cloudinary.com/dczhi464d/image/upload/v1769253060/Screenshot_20260120_203514_csj7ch.png" width="200"> | <img src="https://res.cloudinary.com/dczhi464d/image/upload/v1769253056/Screenshot_20260120_203916_uqzixh.png" width="200"> | <img src="https://res.cloudinary.com/dczhi464d/image/upload/v1769254280/Screenshot_20260120_203834_i6ydfl.png" width="200"> | <img src="https://res.cloudinary.com/dczhi464d/image/upload/v1769253054/Screenshot_20260120_203935_ud9ixy.png" width="200"> |

---

## 🚧 Status & Development Roadmap

The project is currently in the **Beta** phase. We are focusing on refining core features and optimizing the user experience.

### ⏳ Under Development (Coming Soon)
- [ ] Online payment gateway integration (VNPAY / PayPal)
- [ ] Push Notifications for order status changes
- [ ] Dark Mode for the entire application
- [ ] Live chat with Admin
- [ ] Optimize image loading performance and caching

---

## ✨ Key Features

### 👤 Customer (User)
- **Authentication:** Login, Register, Profile Management, Change Password.
- **Products:**
  - View product lists by category (FullFrame, APS-C, Lens G/GM...).
  - Search products by name.
  - Sort products by price.
  - View details: Specifications, description, image gallery (Slider).
- **Shopping:**
  - Add to cart, update quantity.
  - Checkout with various payment methods (COD, Banking, Momo).
  - Manage delivery addresses.
- **Interaction:**
  - Favorite products (Wishlist).
  - Review & comment on products after purchase.
  - Track order history and status (Pending, Delivering, Completed, Canceled).

### 🛠 Administrator (Admin)
- **Dashboard:** Daily revenue statistics, number of pending orders.
- **Order Management:** View the list of all orders, update order status (Approve, Deliver, Cancel).

---

## 🛠 Technologies Used

### 📱 Android Client (Frontend)
* **Language:** Kotlin
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

## 🗄 Database Schema

Database: `ckcdigitalstore`

The system includes the following main tables:
- **users:** Account information, roles.
- **products:** Product information, prices, images.
- **categories:** Product categories.
- **orders:** Stores general order information.
- **orderdetails:** Details of each product in the order.
- **reviews:** User reviews and comments.
- **wishlist:** Favorite products.
- **addresses:** Delivery address book.
- **promotions:** Discount/promotion code management.

---

## 🚀 Installation Guide

This project is the Client side (Mobile App). For the application to work, you need to run the Backend Server first.

### 1. Backend Setup (Mandatory)
The Backend source code and detailed installation instructions are hosted in a separate repository:

👉 **https://github.com/hcngdanh/SERVER-API-CKC-DIGITAL.git**

*Please ensure the Server is running (default port 3000) before continuing.*

### 2. Android App Setup
Requirement: The latest version of **Android Studio**.

1.  Clone and open this project in Android Studio.
2.  Configure Server connection:
    - Open the `RetrofitClient.kt` file (in the `services` package).
    - Update `BASE_URL` to match the IP address of the machine running the Server:
      - Emulator: `http://10.0.2.2:3000/`
      - Physical Device: `http://<Server_PC_IPv4>:3000/`
3.  Sync the Gradle project and run the application.

---

## 📬 Main API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Account login |
| `GET` | `/api/products` | Get product list |
| `GET` | `/api/products/:id` | Get product details |
| `POST` | `/api/orders` | Create a new order |
| `GET` | `/api/orders/user/:userId` | Get User's purchase history |
| `POST` | `/api/wishlist/toggle` | Add/Remove favorite products |
| `PUT` | `/api/orders/:orderId/status` | Update order status (Admin) |

---

## 👥 Project Members

This project was developed by a group of students from class **[CĐ TH 23 DĐ D/Mobile Programming]**:

| No. | Full Name | Student ID | Main Role |
|:---:|:---|:---|:---|
| 1 | **Hồ Công Danh** | 0306231276 | Leader, Mobile App (UI/UX), Backend API |
| 2 | **Nguyễn Minh Chánh** | 0306231274 | Mobile App (UI/UX), Frontend Logic |
| 3 | **Trần Tuấn Cường** | 0306231008 | Mobile App (API Integration), Tester |
| 4 | **Nguyễn Phong Điền** | 0306231016 | Database Design, Documentation |
