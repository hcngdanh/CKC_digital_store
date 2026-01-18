package com.example.doanltdd_ckcdigital.utils

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.example.doanltdd_ckcdigital.models.UserModel
import com.google.gson.Gson

class SessionManager private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("CKC_SESSION", Context.MODE_PRIVATE)
    private val gson = Gson()

    var currentUser by mutableStateOf<UserModel?>(null)
        private set

    init {
        currentUser = fetchUserFromPrefs()
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

    fun saveUserSession(user: UserModel) {
        currentUser = user
        val userJson = gson.toJson(user)
        prefs.edit {
            putString("user_data", userJson)
            putBoolean("is_logged_in", true)
        }
    }

    fun getUser(): UserModel? = currentUser

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false) && currentUser != null
    }

    fun isAdmin(): Boolean {
        return isLoggedIn() && currentUser?.RoleID == 1
    }

    fun clearSession() {
        prefs.edit(commit = true) {
            clear()
        }
        currentUser = null
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