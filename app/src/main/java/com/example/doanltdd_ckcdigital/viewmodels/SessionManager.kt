package com.example.doanltdd_ckcdigital.viewmodels

import android.content.Context
import com.example.doanltdd_ckcdigital.models.UserModel
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("CKC_SESSION", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Lưu thông tin User
    fun saveUserSession(user: UserModel) {
        val userJson = gson.toJson(user)
        prefs.edit().apply {
            putString("user_data", userJson)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    // Lấy thông tin User đã lưu
    fun getUser(): UserModel? {
        val userJson = prefs.getString("user_data", null)
        return if (userJson != null) gson.fromJson(userJson, UserModel::class.java) else null
    }

    // Kiểm tra đã đăng nhập chưa
    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    // Đăng xuất
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}