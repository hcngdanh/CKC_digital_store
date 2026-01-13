package com.example.doanltdd_ckcdigital.utils

import androidx.compose.runtime.mutableStateListOf
import com.example.doanltdd_ckcdigital.models.ProductModel

data class CartItem(
    val product: ProductModel,
    val ProductID: Int,
    val ProductName: String,
    val Price: Double,
    val ThumbnailURL: String?,
    val quantity: Int
)

object CartManager {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    val cartCount: Int get() = _cartItems.sumOf { it.quantity }

    val badgeCartCount: Int get() = _cartItems.size

    fun addToCart(product: ProductModel) {
        val index = _cartItems.indexOfFirst { it.product.ProductID == product.ProductID }
        if (index != -1) {
            val item = _cartItems[index]
            _cartItems[index] = item.copy(quantity = item.quantity + 1)
        } else {
            _cartItems.add(
                CartItem(
                    product = product,
                    ProductID = product.ProductID,
                    ProductName = product.ProductName,
                    Price = product.Price,
                    ThumbnailURL = product.ThumbnailURL,
                    quantity = 1
                )
            )
        }
    }

    fun buyNow(product: ProductModel) {
        val index = _cartItems.indexOfFirst { it.product.ProductID == product.ProductID }
        if (index == -1) {
            addToCart(product)
        }
    }

    fun removeProduct(item: CartItem) {
        _cartItems.remove(item)
    }

    fun increaseQuantity(item: CartItem) {
        val index = _cartItems.indexOf(item)
        if (index != -1) {
            _cartItems[index] = item.copy(quantity = item.quantity + 1)
        }
    }

    fun decreaseQuantity(item: CartItem) {
        val index = _cartItems.indexOf(item)
        if (index != -1) {
            if (item.quantity > 1) {
                _cartItems[index] = item.copy(quantity = item.quantity - 1)
            } else {
                _cartItems.removeAt(index)
            }
        }
    }

    fun getTotalPrice(): Double {
        return _cartItems.sumOf { it.Price * it.quantity }
    }

    fun clearCart() {
        _cartItems.clear()
    }
}