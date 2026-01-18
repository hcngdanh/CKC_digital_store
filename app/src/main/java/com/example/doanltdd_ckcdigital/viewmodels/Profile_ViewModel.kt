package com.example.doanltdd_ckcdigital.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd_ckcdigital.models.ProductModel
import com.example.doanltdd_ckcdigital.models.UserAddress
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import com.example.doanltdd_ckcdigital.utils.SessionManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _orderStats = MutableStateFlow<Map<String, Int>>(emptyMap())
    val orderStats: StateFlow<Map<String, Int>> = _orderStats.asStateFlow()

    private val _wishlist = MutableStateFlow<List<ProductModel>>(emptyList())
    val wishlist: StateFlow<List<ProductModel>> = _wishlist.asStateFlow()

    private val _addresses = MutableStateFlow<List<UserAddress>>(emptyList())
    val addresses: StateFlow<List<UserAddress>> = _addresses.asStateFlow()

    private val _addressDetail = MutableStateFlow<UserAddress?>(null)
    val addressDetail: StateFlow<UserAddress?> = _addressDetail.asStateFlow()

    private val _updateEvent = Channel<UpdateResult>()
    val updateEvent = _updateEvent.receiveAsFlow()

    private val _changePasswordEvent = Channel<ChangePasswordResult>()
    val changePasswordEvent = _changePasswordEvent.receiveAsFlow()

    private val _reviewEvent = Channel<UpdateResult>()
    val reviewEvent = _reviewEvent.receiveAsFlow()

    fun fetchOrderStats(userId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getUserOrders(userId)
                if (response.success) {
                    val stats = response.data.groupingBy { it.OrderStatus }.eachCount()
                    _orderStats.value = stats
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProfile(userId: Int, fullName: String, phone: String, sessionManager: SessionManager) {
        if (fullName.isBlank() || phone.isBlank()) {
            viewModelScope.launch { _updateEvent.send(UpdateResult.Error("Vui lòng điền đủ thông tin")) }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = mapOf("FullName" to fullName, "Phone" to phone)
                val response = RetrofitClient.apiService.updateProfile(userId, request)
                if (response.success) {
                    sessionManager.saveUserSession(response.data)
                    _updateEvent.send(UpdateResult.Success)
                } else {
                    _updateEvent.send(UpdateResult.Error("Cập nhật thất bại"))
                }
            } catch (e: Exception) {
                _updateEvent.send(UpdateResult.Error("Lỗi kết nối: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changePassword(userId: Int, oldPass: String, newPass: String, confirmPass: String) {
        if (oldPass.isBlank() || newPass.isBlank() || confirmPass.isBlank()) {
            sendPassError("Vui lòng nhập đầy đủ thông tin")
            return
        }
        if (newPass != confirmPass) {
            sendPassError("Mật khẩu mới không khớp")
            return
        }
        if (newPass.length < 6) {
            sendPassError("Mật khẩu phải từ 6 ký tự")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = mapOf("oldPassword" to oldPass, "newPassword" to newPass)
                val response = RetrofitClient.apiService.changePassword(userId, request)
                if (response.success) {
                    _changePasswordEvent.send(ChangePasswordResult.Success)
                } else {
                    sendPassError(response.message)
                }
            } catch (e: Exception) {
                sendPassError("Lỗi kết nối server: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchWishlist(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getWishlist(userId)
                if (response.success) {
                    _wishlist.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchUserAddresses(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = RetrofitClient.apiService.getUserAddresses(userId)
                _addresses.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchAddressDetail(addressId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val address = RetrofitClient.apiService.getAddressDetail(addressId)
                _addressDetail.value = address
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addAddress(userId: Int, name: String, phone: String, street: String, city: String, isDefault: Boolean) {
        if (name.isBlank() || phone.isBlank() || street.isBlank() || city.isBlank()) {
            viewModelScope.launch { _updateEvent.send(UpdateResult.Error("Vui lòng điền đủ thông tin")) }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newAddress = UserAddress(
                    AddressID = 0,
                    UserID = userId,
                    ReceiverName = name,
                    PhoneNumber = phone,
                    StreetAddress = street,
                    City = city,
                    IsDefault = if (isDefault) 1 else 0
                )
                val res = RetrofitClient.apiService.addAddress(newAddress)
                if (res.success) {
                    _updateEvent.send(UpdateResult.Success)
                } else {
                    _updateEvent.send(UpdateResult.Error("Thêm địa chỉ thất bại"))
                }
            } catch (e: Exception) {
                _updateEvent.send(UpdateResult.Error("Lỗi kết nối server"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateAddress(addressId: Int, name: String, phone: String, street: String, city: String, isDefault: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updatedAddress = UserAddress(
                    AddressID = addressId,
                    UserID = 0,
                    ReceiverName = name,
                    PhoneNumber = phone,
                    StreetAddress = street,
                    City = city,
                    IsDefault = if (isDefault) 1 else 0
                )
                val res = RetrofitClient.apiService.updateAddress(addressId, updatedAddress)
                if (res.success) {
                    _updateEvent.send(UpdateResult.Success)
                } else {
                    _updateEvent.send(UpdateResult.Error("Cập nhật thất bại"))
                }
            } catch (e: Exception) {
                _updateEvent.send(UpdateResult.Error("Lỗi kết nối"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAddress(addressId: Int) {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.apiService.deleteAddress(addressId)
                if (res.success) {
                    _addresses.value = _addresses.value.filter { it.AddressID != addressId }
                    _updateEvent.send(UpdateResult.Success)
                } else {
                    _updateEvent.send(UpdateResult.Error("Không thể xóa địa chỉ"))
                }
            } catch (e: Exception) {
                _updateEvent.send(UpdateResult.Error("Lỗi kết nối"))
            }
        }
    }

    private fun sendPassError(message: String) {
        viewModelScope.launch {
            _changePasswordEvent.send(ChangePasswordResult.Error(message))
        }
    }

    sealed interface UpdateResult {
        object Success : UpdateResult
        data class Error(val message: String) : UpdateResult
    }

    sealed interface ChangePasswordResult {
        object Success : ChangePasswordResult
        data class Error(val message: String) : ChangePasswordResult
    }
}