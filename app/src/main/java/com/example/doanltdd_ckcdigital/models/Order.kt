package com.example.doanltdd_ckcdigital.models

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("OrderID")
    val orderId: Int,

    @SerializedName("UserID")
    val userId: Int,

    @SerializedName("FullName") // Tên tài khoản
    val userFullName: String?,

    // --- THÊM DÒNG NÀY ---
    @SerializedName("ReceiverName") // Tên người nhận hàng thực tế
    val receiverName: String?,
    // ---------------------

    @SerializedName("OrderDate")
    val orderDate: String?, // Chấp nhận null để tránh lỗi

    @SerializedName("TotalAmount")
    val totalAmount: Double,

    @SerializedName("Status") // Hoặc "OrderStatus" tùy API trả về
    val status: String?,

    @SerializedName("OrderItems")
    val items: List<OrderItem>?
)

// ... (Class OrderItem giữ nguyên)

data class OrderItem(
    @SerializedName("productName")
    val productName: String,

    @SerializedName("quantity")
    val quantity: Int
)