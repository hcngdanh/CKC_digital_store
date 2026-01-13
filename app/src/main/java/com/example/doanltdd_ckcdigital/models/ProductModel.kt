package com.example.doanltdd_ckcdigital.models

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val data: T,
    val message: String? = null
)

data class ProductModel(
    val ProductID: Int,
    val ProductName: String,
    val CategoryID: Int,
    val Price: Double,

    @SerializedName("ThumbnailURL")
    val ThumbnailURL: String?,
    val Resolution: String,
    val Processor: String,
    val LensMount: String,
    val Weight: String?,
    val WarrantyPeriod: String,

    val SensorType: String?,
    val ShortDescription: String?,

    val FullDescription: String? = null,

    val Gallery: List<String>? = null
)

data class CategoryModel(
    val CategoryID: Int,
    val CategoryName: String,
    val ParentID: Int?
)

data class ProductAddToCart(
    val userId:Int,
    @SerializedName("productId")
    val productID: Int,
    val quantity: Int
)
