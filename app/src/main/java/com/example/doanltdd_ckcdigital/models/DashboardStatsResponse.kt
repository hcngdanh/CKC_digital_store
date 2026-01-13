package com.example.doanltdd_ckcdigital.modelsimport

import com.google.gson.annotations.SerializedName


data class DashboardStatsResponse(
    @SerializedName("dailyRevenue")
    val dailyRevenue: Double,

    @SerializedName("pendingOrders")
    val pendingOrders: Int,

    @SerializedName("completedOrdersToday")
    val completedOrdersToday: Int
)
