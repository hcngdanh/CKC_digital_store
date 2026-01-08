package com.example.doanltdd_ckcdigital.utils

import androidx.compose.runtime.mutableStateListOf
import com.example.doanltdd_ckcdigital.models.ProductModel

data class CartItem(
    val product: ProductModel,
    var quantity: Int
)

object CartManager {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    fun addToCart(product: ProductModel) {
        val existingItem = _cartItems.find { it.product.ProductID == product.ProductID }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            _cartItems.add(CartItem(product, 1))
        }
    }

    fun removeProduct(item: CartItem) {
        _cartItems.remove(item)
    }

    fun increaseQuantity(item: CartItem) {
        val index = _cartItems.indexOf(item)
        if (index != -1) {
            item.quantity++
        }
    }

    fun decreaseQuantity(item: CartItem) {
        if (item.quantity > 1) {
            item.quantity--
        } else {
            removeProduct(item)
        }
    }

    fun getTotalPrice(): Double {
        return _cartItems.sumOf { it.product.Price * it.quantity }
    }

    fun clearCart() {
        _cartItems.clear()
    }
}