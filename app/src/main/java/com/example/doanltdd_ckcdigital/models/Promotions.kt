package com.example.doanltdd_ckcdigital.models


import com.google.gson.annotations.SerializedName

data class PromotionModel(
    val PromotionID: Int,

    @SerializedName("Code")
    val Code: String,
    val DiscountType: String,
    val DiscountValue: Double,
    val Description: String,
    val MinOrderValue: Double = 0.0
)