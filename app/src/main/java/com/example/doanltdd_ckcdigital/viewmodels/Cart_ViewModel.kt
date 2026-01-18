package com.example.doanltdd_ckcdigital.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd_ckcdigital.models.CartItem
import com.example.doanltdd_ckcdigital.utils.CartManager
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    val cartItems = CartManager.cartItems


    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch {
            CartManager.increaseQuantity(item)
        }
    }

    fun decreaseQuantity(item: CartItem) {
        viewModelScope.launch {
            CartManager.decreaseQuantity(item)
        }
    }

    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            CartManager.removeProduct(item)
        }
    }

    fun getTotalPrice(): Double {
        return CartManager.getTotalPrice()
    }
}