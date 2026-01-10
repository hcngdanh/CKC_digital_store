package com.example.doanltdd_ckcdigital.utils

import com.example.doanltdd_ckcdigital.models.UserModel

object UserSession {
    // Biến lưu thông tin user đang đăng nhập
    // Tôi tạo sẵn dữ liệu giả (Fake) để bạn test giao diện luôn
    var user: UserModel? = UserModel(
        UserID = 1,
        FullName = "Trần Tuấn Cường",
        Email = "cuong@example.com",
        Phone = "0901234567",
        AvatarURL = null,
        RoleID = 1,
        IsActive = 1
    )
}