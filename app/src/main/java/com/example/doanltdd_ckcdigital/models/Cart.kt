package com.example.doanltdd_ckcdigital.models

import com.google.gson.annotations.SerializedName

data class CartItemResponse(
    val CartItemID: Int,
    val ProductID: Int,
    val Quantity: Int,
    val ProductName: String,
    val Price: Double,

    @SerializedName("ThumbnailURL")
    val ThumbnailURL: String?
)