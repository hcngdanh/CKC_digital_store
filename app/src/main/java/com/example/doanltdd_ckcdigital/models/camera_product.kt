package com.example.doanltdd_ckcdigital.models

data class ProductResponse(
    val success: Boolean,
    val data: List<ProductApi>,
    val pagination: Pagination?
)

data class ProductApi(
    val ProductID: Int,
    val ProductName: String,
    val CategoryName: String?,
    val Price: Double,
    val ImageUrl: String,
    val SensorType: String?,
    val DiscountPercent: Int,
    val FinalPrice: Double,
    val CategoryID: Int,
    val MountType: String?,
    val Description: String?
)

data class Pagination(
    val total: Int,
    val totalPages: Int
)

