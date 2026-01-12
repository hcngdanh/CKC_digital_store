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
data class RegisterResponse(
    val success: Boolean,
    val message: String
)
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: UserModel?
)

data class UserAddress(
    val AddressID: Int,    // Đảm bảo tên biến khớp chính xác từng chữ cái
    val UserID: Int,
    val ReceiverName: String,
    val PhoneNumber: String,
    val StreetAddress: String,
    val City: String,
    val IsDefault: Int
)

data class SimpleResponse(
    val success: Boolean,
    val message: String
)
