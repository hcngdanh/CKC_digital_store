package com.example.doanltdd_ckcdigital.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


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
@Parcelize
data class UserAddress(
    val AddressID: Int,
    val UserID: Int,
    val ReceiverName: String,
    val PhoneNumber: String,
    val StreetAddress: String,
    val City: String,
    val IsDefault: Int
) : Parcelable

data class SimpleResponse(
    val success: Boolean,
    val message: String
)

data class Voucher(
    val VoucherID: Int,
    val VoucherCode: String,
    val Description: String,
    val DiscountAmount: Double,
    val MinOrderValue: Double,
    val Quantity: Int,
    val ExpirationDate: String?
)
