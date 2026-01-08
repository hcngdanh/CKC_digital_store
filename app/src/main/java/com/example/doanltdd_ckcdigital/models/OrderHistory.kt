package com.example.doanltdd_ckcdigital.models

import com.example.doanltdd_ckcdigital.screens.HistoryStatus

data class OrderHistoryItem(
    val orderId: String,
    val shopName: String,
    val productName: String,
    val productImageUrl: String,
    val quantity: Int,
    val totalPrice: Long,
    val status: HistoryStatus
)