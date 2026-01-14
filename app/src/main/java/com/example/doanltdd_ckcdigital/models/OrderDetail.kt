package com.example.doanltdd_ckcdigital.models
import com.google.gson.annotations.SerializedName

data class OrderDetailResponse(
    val orderInfo: OrderInfoModel,
    val orderItems: List<OrderItemModel>
)

data class OrderInfoModel(
    val OrderID: Int,
    val OrderDate: String,
    val OrderStatus: String,
    val TotalAmount: Double,
    val ShipAddress: String,
    val ShippingMethod: String?,
    val PaymentMethod: String?,

    // --- THÊM 2 TRƯỜNG NÀY ĐỂ HIỂN THỊ LÊN ADMIN ---
    @SerializedName("ReceiverName") // Tên trường phải khớp với SELECT trong Node.js
    val ReceiverName: String?,

    @SerializedName("PhoneNumber")
    val PhoneNumber: String?
)

data class OrderItemModel(
    val ProductID: Int,
    val ProductName: String,
    @SerializedName("ThumbnailURL") val ThumbnailURL: String?,
    val Quantity: Int,
    val UnitPrice: Double,
    val TotalPrice: Double
)