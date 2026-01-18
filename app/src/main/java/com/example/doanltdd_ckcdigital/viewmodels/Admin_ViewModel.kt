package com.example.doanltdd_ckcdigital.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd_ckcdigital.models.DashboardStatsResponse
import com.example.doanltdd_ckcdigital.models.Order
import com.example.doanltdd_ckcdigital.models.OrderDetailResponse
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import com.example.doanltdd_ckcdigital.ui.admin.OrderStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _dashboardStats = MutableStateFlow<DashboardStatsResponse?>(null)
    val dashboardStats: StateFlow<DashboardStatsResponse?> = _dashboardStats.asStateFlow()

    private val _allOrders = MutableStateFlow<List<Order>>(emptyList())
    private val _selectedOrderTabIndex = MutableStateFlow(0)
    val selectedOrderTabIndex: StateFlow<Int> = _selectedOrderTabIndex.asStateFlow()

    private val _filteredOrders = MutableStateFlow<List<Order>>(emptyList())
    val filteredOrders: StateFlow<List<Order>> = _filteredOrders.asStateFlow()

    private val _orderDetail = MutableStateFlow<OrderDetailResponse?>(null)
    val orderDetail: StateFlow<OrderDetailResponse?> = _orderDetail.asStateFlow()

    private val _updateStatusEvent = Channel<Boolean>()
    val updateStatusEvent = _updateStatusEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(_allOrders, _selectedOrderTabIndex) { orders, index ->
                if (index == 0) orders
                else {
                    val targetStatus = OrderStatus.entries.getOrNull(index - 1) ?: OrderStatus.PENDING
                    orders.filter { OrderStatus.fromString(it.status) == targetStatus }
                }
            }.collect { _filteredOrders.value = it }
        }
    }

    fun fetchDashboardStats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = RetrofitClient.apiService.getDashboardStats()
                if (res.success) _dashboardStats.value = res.data
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchAllOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = RetrofitClient.apiService.getAllOrders()
                if (res.success) {
                    _allOrders.value = res.data.sortedByDescending { it.orderId }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setOrderFilterTab(index: Int) {
        _selectedOrderTabIndex.value = index
    }

    fun fetchOrderDetail(orderId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _orderDetail.value = null
            try {
                val response = RetrofitClient.apiService.getOrderDetail(orderId)
                if (response.success) {
                    _orderDetail.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateOrderStatus(orderId: Int, statusApiName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val body = mapOf("status" to statusApiName, "cancelReason" to "")
                val res = RetrofitClient.apiService.updateOrderStatus(orderId, body)
                if (res.success) {
                    _updateStatusEvent.send(true)
                    fetchOrderDetail(orderId)
                } else {
                    _updateStatusEvent.send(false)
                }
            } catch (e: Exception) {
                _updateStatusEvent.send(false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}