package com.example.doanltdd_ckcdigital.models

data class UserModel(
    val UserID: Int,
    val FullName: String,
    val Email: String,
    val Phone: String?,
    val AvatarURL: String?,
    val RoleID: Int,
    val IsActive: Int
)

data class LoginRequest(
    val Email: String,
    val Password: String
)

data class RegisterRequest(
    val FullName: String,
    val Email: String,
    val Phone: String,
    val Password: String
)
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: UserModel?
)