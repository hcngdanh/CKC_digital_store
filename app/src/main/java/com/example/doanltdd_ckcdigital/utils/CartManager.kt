package com.example.doanltdd_ckcdigital.utils

import androidx.compose.runtime.mutableStateListOf
import com.example.doanltdd_ckcdigital.models.CartItemResponse
import com.example.doanltdd_ckcdigital.models.ProductModel

// 1. Cấu trúc lại CartItem: Gọn nhẹ, không chứa ProductModel to đùng
data class CartItem(
    val CartItemID: Int = 0, // Mặc định là 0 nếu thêm từ Client chưa có ID
    val ProductID: Int,
    val ProductName: String,
    val Price: Double,
    val ThumbnailURL: String?,
    var quantity: Int
)

object CartManager {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    val cartCount: Int get() = _cartItems.sumOf { it.quantity }
    val badgeCartCount: Int get() = _cartItems.size

    // Hàm mới: Đồng bộ từ Server về (Sử dụng model Response mới)
    fun syncCartFromServer(serverItems: List<CartItemResponse>) {
        _cartItems.clear()
        serverItems.forEach { res ->
            _cartItems.add(
                CartItem(
                    CartItemID = res.CartItemID,
                    ProductID = res.ProductID,
                    ProductName = res.ProductName,
                    Price = res.Price,
                    ThumbnailURL = res.ThumbnailURL,
                    quantity = res.Quantity
                )
            )
        }
    }

    // Sửa lại hàm AddToCart: Chuyển đổi từ ProductModel sang CartItem nhẹ
    suspend fun addToCart(userId: Int, product: ProductModel) {
        try {
            // 1. Gửi yêu cầu thêm vào giỏ
            val requestBody = com.example.doanltdd_ckcdigital.models.ProductAddToCart(
                userId = userId,
                productID = product.ProductID,
                quantity = 1
            )

            val response = com.example.doanltdd_ckcdigital.services.RetrofitClient.apiService.addToCart(requestBody)

            if (response.success) {
                // 2. REALTIME: Thêm xong -> Gọi ngay API lấy danh sách mới nhất
                // Để icon giỏ hàng nhảy số và cập nhật ID chuẩn
                val refreshResponse = com.example.doanltdd_ckcdigital.services.RetrofitClient.apiService.getCartItems(userId)

                if (refreshResponse.success) {
                    syncCartFromServer(refreshResponse.data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("CartManager", "Lỗi thêm giỏ hàng: ${e.message}")
        }
    }

    // Các hàm xử lý khác (giữ nguyên logic nhưng sửa tham chiếu)
    suspend fun removeProduct(item: CartItem) {
        try {
            // 1. Gọi API Xóa
            val response = com.example.doanltdd_ckcdigital.services.RetrofitClient.apiService.removeCartItem(item.CartItemID)

            if (response.success) {
                // 2. Xóa khỏi list UI
                _cartItems.remove(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun increaseQuantity(item: CartItem) {
        val newQuantity = item.quantity + 1
        try {
            // 1. Gọi API cập nhật
            val body = mapOf("cartItemId" to item.CartItemID, "quantity" to newQuantity)
            val response = com.example.doanltdd_ckcdigital.services.RetrofitClient.apiService.updateCartQuantity(body)

            if (response.success) {
                // 2. Nếu Server OK, cập nhật UI Local
                val index = _cartItems.indexOf(item)
                if (index != -1) {
                    _cartItems[index] = item.copy(quantity = newQuantity)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // Xử lý lỗi (VD: Show Toast)
        }
    }

    suspend fun decreaseQuantity(item: CartItem) {
        if (item.quantity > 1) {
            val newQuantity = item.quantity - 1
            try {
                // 1. Gọi API cập nhật
                val body = mapOf("cartItemId" to item.CartItemID, "quantity" to newQuantity)
                val response = com.example.doanltdd_ckcdigital.services.RetrofitClient.apiService.updateCartQuantity(body)

                if (response.success) {
                    // 2. Cập nhật UI
                    val index = _cartItems.indexOf(item)
                    if (index != -1) {
                        _cartItems[index] = item.copy(quantity = newQuantity)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // Nếu số lượng là 1 mà bấm giảm -> Gọi hàm xóa
            removeProduct(item)
        }
    }

    fun getTotalPrice(): Double {
        return _cartItems.sumOf { it.Price * it.quantity }
    }

    fun clearCart() { _cartItems.clear() }
}