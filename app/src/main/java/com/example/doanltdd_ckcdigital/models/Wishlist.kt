package com.example.doanltdd_ckcdigital.models

data class ToggleWishlistResponse(
    val success: Boolean,
    val message: String?,
    val isFavorite: Boolean // Quan trọng: True = Đã like (Đỏ), False = Chưa like (Trắng)
)

// 2. Dùng cho API: GET /api/wishlist/{userId}
// Mục đích: Hiển thị danh sách sản phẩm trong màn hình WishlistScreen
// Class này khớp với câu SQL SELECT w.WishlistID, p.ProductID... ở server
data class WishlistItemResponse(
    val WishlistID: Int,
    val ProductID: Int,
    val ProductName: String,
    val Price: Double,
    val ThumbnailURL: String
)