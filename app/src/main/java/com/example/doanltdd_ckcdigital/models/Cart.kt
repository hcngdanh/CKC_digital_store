package com.example.doanltdd_ckcdigital.models

import com.google.gson.annotations.SerializedName

data class CartItem(
    val CartItemID: Int = 0,
    val ProductID: Int,
    val ProductName: String,
    val Price: Double,
    val ThumbnailURL: String?,
    var quantity: Int
)

data class CartItemResponse(
    val CartItemID: Int,
    val ProductID: Int,
    val Quantity: Int,
    val ProductName: String,
    val Price: Double,
    @SerializedName("ThumbnailURL")
    val ThumbnailURL: String?
)