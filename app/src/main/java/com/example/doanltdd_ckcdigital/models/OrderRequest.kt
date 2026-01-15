package com.example.doanltdd_ckcdigital.models

data class OrderRequest(
    val userId: Int,
    val totalAmount: Double,
    val shipAddress: String,
    // --- THÊM 2 TRƯỜNG NÀY ---
    val receiverName: String,
    val phoneNumber: String,
    // -------------------------
    val paymentMethodId: Int,
    val shippingMethodId: Int,
    val items: List<OrderDetailRequest>
)

data class OrderDetailRequest(
    val productId: Int,
    val quantity: Int,
    val price: Double
)

data class CancelOrderRequest(
    val orderId: Int,
    val reason: String
)