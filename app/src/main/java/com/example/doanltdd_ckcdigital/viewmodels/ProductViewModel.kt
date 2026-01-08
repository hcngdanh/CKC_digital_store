package com.example.doanltdd_ckcdigital.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd_ckcdigital.models.ProductModel
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products: StateFlow<List<ProductModel>> = _products

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getProducts()
                if (response.success) {
                    _products.value = response.data
                } else {
                    _errorMessage.value = "Lấy dữ liệu thất bại"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}