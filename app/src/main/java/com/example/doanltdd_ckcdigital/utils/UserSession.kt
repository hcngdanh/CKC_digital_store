package com.example.doanltdd_ckcdigital.utils

import com.example.doanltdd_ckcdigital.models.User

object UserSession {
    var user: User? = null

    val isLoggedIn: Boolean
        get() = user != null

    var pendingProductId: Int? = null
}