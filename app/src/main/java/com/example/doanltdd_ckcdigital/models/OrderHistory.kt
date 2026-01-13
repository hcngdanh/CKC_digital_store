package com.example.doanltdd_ckcdigital.models
import com.google.gson.annotations.SerializedName

data class OrderHistoryModel(
    val OrderID: Int,
    val OrderDate: String,
    val TotalAmount: Double,
    val OrderStatus: String,
    val ProductName: String?,
    @SerializedName("ThumbnailURL") val ThumbnailURL: String?,
    val TotalQuantity: Int
)