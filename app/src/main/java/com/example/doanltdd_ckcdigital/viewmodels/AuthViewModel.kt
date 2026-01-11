package com.example.doanltdd_ckcdigital.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd_ckcdigital.models.LoginRequest
import com.example.doanltdd_ckcdigital.models.RegisterRequest
import com.example.doanltdd_ckcdigital.models.UserModel
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import com.example.doanltdd_ckcdigital.utils.CartManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: UserModel) : LoginState()
    data class Error(val message: String) : LoginState()
}

class AuthViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()
    var loginError by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
    var registerError by mutableStateOf("")

    fun login(email: String, pass: String, onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, pass))
                if (response.success && response.data != null) {
                    sessionManager.saveUserSession(response.data)
                    _loginState.value = LoginState.Success(response.data)
                    onLoginSuccess()
                } else {
                    _loginState.value = LoginState.Error(response.message)
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Không thể kết nối server: ${e.message}")
            }
        }
    }

    fun registerUser(request: RegisterRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                registerError = ""
                val response = RetrofitClient.apiService.register(request)
                if (response.success) {
                    onSuccess()
                } else {
                    registerError = response.message
                }
            } catch (e: Exception) {
                registerError = "Lỗi kết nối: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetState() { _loginState.value = LoginState.Idle }
}