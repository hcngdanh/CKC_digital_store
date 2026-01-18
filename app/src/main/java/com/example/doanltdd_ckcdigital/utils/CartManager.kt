package com.example.doanltdd_ckcdigital.utils

import androidx.compose.runtime.mutableStateListOf
import com.example.doanltdd_ckcdigital.models.CartItem
import com.example.doanltdd_ckcdigital.models.CartItemResponse
import com.example.doanltdd_ckcdigital.models.ProductModel
import com.example.doanltdd_ckcdigital.models.ProductAddToCart
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import android.util.Log

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
            val requestBody = ProductAddToCart(
                userId = userId,
                productID = product.ProductID,
                quantity = 1
            )
            val response = RetrofitClient.apiService.addToCart(requestBody)
            if (response.success) {
                val refreshResponse = RetrofitClient.apiService.getCartItems(userId)
                if (refreshResponse.success) {
                    syncCartFromServer(refreshResponse.data)
                }
            }
        } catch (e: Exception) {
            Log.e("CartManager", "Error addToCart: ${e.message}")
        }
    }

    suspend fun increaseQuantity(item: CartItem) {
        val newQuantity = item.quantity + 1
        try {
            val body = mapOf("cartItemId" to item.CartItemID, "quantity" to newQuantity)
            val response = RetrofitClient.apiService.updateCartQuantity(body)
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
                val response = RetrofitClient.apiService.updateCartQuantity(body)
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

    suspend fun removeProduct(item: CartItem) {
        try {
            val response = RetrofitClient.apiService.removeCartItem(item.CartItemID)
            if (response.success) {
                _cartItems.remove(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun clearCartOnServer() {
        val itemsToDelete = _cartItems.toList()
        itemsToDelete.forEach { item ->
            try {
                RetrofitClient.apiService.removeCartItem(item.CartItemID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        _cartItems.clear()
    }

    fun getTotalPrice(): Double {
        return _cartItems.sumOf { it.Price * it.quantity }
    }

    fun clearCart() {
        _cartItems.clear()
    }
}