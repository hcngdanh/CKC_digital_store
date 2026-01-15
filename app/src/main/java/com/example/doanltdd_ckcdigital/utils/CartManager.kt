package com.example.doanltdd_ckcdigital.utils

import androidx.compose.runtime.mutableStateListOf
import com.example.doanltdd_ckcdigital.models.CartItemResponse
import com.example.doanltdd_ckcdigital.models.ProductModel
import com.example.doanltdd_ckcdigital.services.RetrofitClient

data class CartItem(
    val CartItemID: Int = 0,
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

    suspend fun addToCart(userId: Int, product: ProductModel) {
        try {
            val requestBody = com.example.doanltdd_ckcdigital.models.ProductAddToCart(
                userId = userId,
                productID = product.ProductID,
                quantity = 1
            )

            val response = com.example.doanltdd_ckcdigital.services.RetrofitClient.apiService.addToCart(requestBody)

            if (response.success) {
                val refreshResponse = com.example.doanltdd_ckcdigital.services.RetrofitClient.apiService.getCartItems(userId)
                if (refreshResponse.success) {
                    syncCartFromServer(refreshResponse.data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("CartManager", "Lỗi addToCart: ${e.message}")
        }
    }

    suspend fun removeProduct(item: CartItem) {
        try {
            val response = com.example.doanltdd_ckcdigital.services.RetrofitClient.apiService.removeCartItem(item.CartItemID)
            if (response.success) {
                _cartItems.remove(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun increaseQuantity(item: CartItem) {
        val newQuantity = item.quantity + 1
        try {
            val body = mapOf("cartItemId" to item.CartItemID, "quantity" to newQuantity)
            val response = com.example.doanltdd_ckcdigital.services.RetrofitClient.apiService.updateCartQuantity(body)

            if (response.success) {
                val index = _cartItems.indexOf(item)
                if (index != -1) {
                    _cartItems[index] = item.copy(quantity = newQuantity)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun decreaseQuantity(item: CartItem) {
        if (item.quantity > 1) {
            val newQuantity = item.quantity - 1
            try {
                val body = mapOf("cartItemId" to item.CartItemID, "quantity" to newQuantity)
                val response = com.example.doanltdd_ckcdigital.services.RetrofitClient.apiService.updateCartQuantity(body)

                if (response.success) {
                    val index = _cartItems.indexOf(item)
                    if (index != -1) {
                        _cartItems[index] = item.copy(quantity = newQuantity)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            removeProduct(item)
        }
    }

    suspend fun clearCartOnServer() {
        // Tạo bản sao danh sách để tránh lỗi ConcurrentModification khi loop
        val itemsToDelete = _cartItems.toList()

        itemsToDelete.forEach { item ->
            try {
                // Gọi API xóa từng item trong Database
                RetrofitClient.apiService.removeCartItem(item.CartItemID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // Cuối cùng xóa sạch danh sách local
        _cartItems.clear()
    }

    fun getTotalPrice(): Double {
        return _cartItems.sumOf { it.Price * it.quantity }
    }

    fun clearCart() { _cartItems.clear() }
}