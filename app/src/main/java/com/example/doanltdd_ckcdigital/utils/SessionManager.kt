package com.example.doanltdd_ckcdigital.utils

import android.content.Context
import com.example.doanltdd_ckcdigital.models.UserModel
import com.google.gson.Gson

class SessionManager private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("CKC_SESSION", Context.MODE_PRIVATE)
    private val gson = Gson()

    var currentUser: UserModel? = null
        private set

    init {
        currentUser = fetchUserFromPrefs()
    }

    fun saveUserSession(user: UserModel) {
        currentUser = user

        val userJson = gson.toJson(user)
        prefs.edit().apply {
            putString("user_data", userJson)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    private fun fetchUserFromPrefs(): UserModel? {
        val userJson = prefs.getString("user_data", null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, UserModel::class.java)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun getUser(): UserModel? {
        return currentUser
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false) && currentUser != null
    }

    fun isAdmin(): Boolean {
        return isLoggedIn() && currentUser?.RoleID == 1
    }

    fun clearSession() {
        currentUser = null
        prefs.edit().clear().apply()
    }

    companion object {
        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }
}