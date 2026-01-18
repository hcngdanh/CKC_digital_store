package com.example.doanltdd_ckcdigital.models

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("OrderID")
    val orderId: Int,

    @SerializedName("UserID")
    val userId: Int,

    @SerializedName("FullName")
    val userFullName: String?,

    @SerializedName("ReceiverName")
    val receiverName: String?,

    @SerializedName("OrderDate")
    val orderDate: String?,

    @SerializedName("TotalAmount")
    val totalAmount: Double,

    @SerializedName("Status")
    val status: String?,

    @SerializedName("OrderItems")
    val items: List<OrderItem>?
)


data class OrderItem(
    @SerializedName("productName")
    val productName: String,

    @SerializedName("quantity")
    val quantity: Int
)