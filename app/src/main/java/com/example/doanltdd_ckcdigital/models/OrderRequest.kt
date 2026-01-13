package com.example.doanltdd_ckcdigital.models

data class OrderRequest(
    val userId: Int,
    val totalAmount: Double,
    val shipAddress: String,
    val paymentMethodId: Int,
    val shippingMethodId: Int, // Thêm ID vận chuyển
    val items: List<OrderDetailRequest>
)

data class OrderDetailRequest(
    val productId: Int,
    val quantity: Int,
    val price: Double
)