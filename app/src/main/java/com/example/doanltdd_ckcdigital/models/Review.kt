package com.example.doanltdd_ckcdigital.models

data class Review(
    val ReviewID: Int,
    val UserName: String,
    val AvatarURL: String?,
    val Rating: Int,
    val Comment: String,
    val ReviewDate: String
)

data class ReviewResponse(
    val success: Boolean,
    val data: List<Review>
)

