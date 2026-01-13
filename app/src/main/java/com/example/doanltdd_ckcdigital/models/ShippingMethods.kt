package com.example.doanltdd_ckcdigital.models

data class ShippingMethod(
    val ShippingMethodID: Int,
    val MethodName: String,
    val Cost: Double,
    val EstimatedDelivery: String
)