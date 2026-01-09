package com.example.doanltdd_ckcdigital.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd_ckcdigital.models.LoginRequest
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Định nghĩa các trạng thái của màn hình Login
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class AuthViewModel : ViewModel() {
    private val apiService = RetrofitClient.apiService

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, pass: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val response = apiService.login(LoginRequest(email, pass))
                if (response.isSuccessful && response.body()?.success == true) {
                    _loginState.value = LoginState.Success
                } else {
                    val msg = response.body()?.message ?: "Đăng nhập thất bại"
                    _loginState.value = LoginState.Error(msg)
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}