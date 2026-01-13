package com.example.doanltdd_ckcdigital.models


import com.google.gson.annotations.SerializedName

data class PromotionModel(
    val PromotionID: Int,

    @SerializedName("Code")
    val Code: String, // Ví dụ: TET2026, CKCNEW

    val DiscountType: String, // "PERCENT" hoặc "FIXED"
    val DiscountValue: Double, // Giá trị giảm (VD: 10 hoặc 200000)
    val Description: String,

    // Nếu trong DB bạn không có cột MinOrderValue thì mình tạm để mặc định là 0
    // Hoặc bạn có thể thêm cột này vào DB nếu muốn quy định đơn tối thiểu
    val MinOrderValue: Double = 0.0
)