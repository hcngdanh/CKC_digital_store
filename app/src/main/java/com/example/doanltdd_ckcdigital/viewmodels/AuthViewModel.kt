package com.example.doanltdd_ckcdigital.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd_ckcdigital.models.LoginRequest
import com.example.doanltdd_ckcdigital.models.UserModel
import com.example.doanltdd_ckcdigital.services.RetrofitClient
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

    fun resetState() { _loginState.value = LoginState.Idle }
}