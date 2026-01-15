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
import com.example.doanltdd_ckcdigital.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.io.IOException

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

    fun login(email: String, pass: String, onLoginSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, pass))

                if (response.success && response.data != null) {
                    val user = response.data
                    sessionManager.saveUserSession(user)
                    _loginState.value = LoginState.Success(user)
                    onLoginSuccess(user.RoleID)
                } else {
                    _loginState.value = LoginState.Error(response.message)
                    loginError = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()

                // --- XỬ LÝ LỖI CHI TIẾT TẠI ĐÂY ---
                val friendlyMessage = when {
                    // Bắt lỗi 401 Unauthorized -> Đổi thành thông báo dễ hiểu
                    e is HttpException && e.code() == 401 -> "Sai email hoặc mật khẩu!"

                    // Lỗi mạng/timeout
                    e is SocketTimeoutException -> "Kết nối quá hạn, vui lòng thử lại."
                    e is IOException -> "Không có kết nối mạng, vui lòng kiểm tra lại."

                    // Lỗi khác
                    else -> "Lỗi hệ thống: ${e.message}"
                }

                _loginState.value = LoginState.Error(message = friendlyMessage)
                loginError = friendlyMessage
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

    fun clearState() {
        _loginState.value = LoginState.Idle // Hoặc trạng thái mặc định ban đầu
        loginError = "" // Xóa dòng thông báo lỗi đỏ
    }
}