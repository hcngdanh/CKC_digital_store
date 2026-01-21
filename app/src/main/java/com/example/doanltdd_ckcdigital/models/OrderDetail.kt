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

    @SerializedName("ReceiverName")
    val ReceiverName: String?,

    @SerializedName("PhoneNumber")
    val PhoneNumber: String?,

    @SerializedName("CancelReason")
    val CancelReason: String?,

    @SerializedName("ShippingCost")
    val ShippingCost: Double = 0.0,
    val Rating: Int?,
    val Comment: String?,
    val ReviewDate: String?
)
data class OrderItemModel(
    val ProductID: Int,
    val ProductName: String,
    @SerializedName("ThumbnailURL") val ThumbnailURL: String?,
    val Quantity: Int,
    val UnitPrice: Double,
    val TotalPrice: Double
)