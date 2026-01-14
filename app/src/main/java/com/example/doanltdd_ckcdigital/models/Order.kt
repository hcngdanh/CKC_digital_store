package com.example.doanltdd_ckcdigital.models

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("OrderID")
    val orderId: Int,

    @SerializedName("UserID")
    val userId: Int,

    @SerializedName("FullName")
    val userFullName: String?,

    @SerializedName("OrderDate")
    val orderDate: String,
    @SerializedName("TotalAmount")
    val totalAmount: Double,

    @SerializedName("Status")
    val status: String,
    @SerializedName("OrderItems")
    val items: List<OrderItem>?
)
data class OrderItem(
    @SerializedName("OrderItemID")
    val orderItemId: Int,

    @SerializedName("ProductID")
    val productId: Int,

    @SerializedName("ProductName")
    val productName: String,

    @SerializedName("Quantity")
    val quantity: Int,

    @SerializedName("Price")
    val price: Double,

    @SerializedName("ThumbnailURL")
    val thumbnailURL: String?
)
