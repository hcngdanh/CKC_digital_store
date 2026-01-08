package com.example.doanltdd_ckcdigital.models

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: UserData? = null
)

data class UserData(
    val UserID: Int,
    val FullName: String,
    val Email: String,
    val Role: String,
    val Phone: String? = null
)

data class RegisterRequest(
    val FullName: String,
    val Email: String,
    val Phone: String,
    val Password: String
)

data class LoginRequest(
    val Email: String,
    val Password: String
)