package com.example.doanltdd_ckcdigital.utils

import androidx.compose.runtime.mutableStateListOf
import com.example.doanltdd_ckcdigital.models.ProductModel

data class CartItem(
    val product: ProductModel,
    var quantity: Int
)

object CartManager {
    val cartItems = mutableStateListOf<CartItem>()

    fun addProduct(product: ProductModel) {
        val existingItem = cartItems.find { it.product.ProductID == product.ProductID }

        if (existingItem != null) {
            increaseQuantity(existingItem)
        } else {
            cartItems.add(CartItem(product, 1))
        }
    }

    fun removeProduct(item: CartItem) {
        cartItems.remove(item)
    }

    fun increaseQuantity(item: CartItem) {
        val index = cartItems.indexOf(item)
        if (index != -1) {
            cartItems[index] = item.copy(quantity = item.quantity + 1)
        }
    }

    fun decreaseQuantity(item: CartItem) {
        val index = cartItems.indexOf(item)
        if (index != -1) {
            if (item.quantity > 1) {
                cartItems[index] = item.copy(quantity = item.quantity - 1)
            } else {
            }
        }
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.product.Price * it.quantity }
    }
}