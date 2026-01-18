package com.example.doanltdd_ckcdigital.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd_ckcdigital.models.*
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _orderHistory = MutableStateFlow<List<OrderHistoryModel>>(emptyList())
    val orderHistory: StateFlow<List<OrderHistoryModel>> = _orderHistory.asStateFlow()

    private val _orderDetail = MutableStateFlow<OrderDetailResponse?>(null)
    val orderDetail: StateFlow<OrderDetailResponse?> = _orderDetail.asStateFlow()

    private val _checkoutItems = MutableStateFlow<List<CartItem>>(emptyList())
    val checkoutItems: StateFlow<List<CartItem>> = _checkoutItems.asStateFlow()

    private val _shippingMethods = MutableStateFlow<List<ShippingMethod>>(emptyList())
    val shippingMethods: StateFlow<List<ShippingMethod>> = _shippingMethods.asStateFlow()

    private val _promotions = MutableStateFlow<List<PromotionModel>>(emptyList())
    val promotions: StateFlow<List<PromotionModel>> = _promotions.asStateFlow()

    private val _selectedShippingMethod = MutableStateFlow<ShippingMethod?>(null)
    val selectedShippingMethod = _selectedShippingMethod.asStateFlow()

    private val _selectedPromotion = MutableStateFlow<PromotionModel?>(null)
    val selectedPromotion = _selectedPromotion.asStateFlow()

    private val _reviewEvent = Channel<ReviewResult>()
    val reviewEvent = _reviewEvent.receiveAsFlow()

    private val _cancelEvent = Channel<CancelResult>()
    val cancelEvent = _cancelEvent.receiveAsFlow()

    private val _orderSuccessEvent = Channel<Boolean>()
    val orderSuccessEvent = _orderSuccessEvent.receiveAsFlow()

    fun fetchOrderHistory(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getUserOrders(userId)
                if (response.success) {
                    _orderHistory.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
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

    fun submitReview(userId: Int, productId: Int, orderId: Int, rating: Int, comment: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = AddReviewRequest(
                    orderId = orderId,
                    userId = userId,
                    rating = rating,
                    comment = comment
                )
                val response = RetrofitClient.apiService.addReview(request)
                if (response.success) {
                    _orderHistory.value = _orderHistory.value.map { order ->
                        if (order.OrderID == orderId && order.ProductID == productId) {
                            order.copy(IsReviewed = 1)
                        } else {
                            order
                        }
                    }
                    _reviewEvent.send(ReviewResult.Success)
                } else {
                    _reviewEvent.send(ReviewResult.Error(response.message))
                }
            } catch (e: Exception) {
                _reviewEvent.send(ReviewResult.Error("Lỗi kết nối"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelOrder(orderId: Int, reason: String) {
        if (reason.isBlank()) {
            viewModelScope.launch { _cancelEvent.send(CancelResult.Error("Vui lòng nhập lý do")) }
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = CancelOrderRequest(orderId = orderId, reason = reason)
                val response = RetrofitClient.apiService.cancelOrder(request)
                if (response.success) {
                    _cancelEvent.send(CancelResult.Success)
                    fetchOrderDetail(orderId)
                } else {
                    _cancelEvent.send(CancelResult.Error(response.message))
                }
            } catch (e: Exception) {
                _cancelEvent.send(CancelResult.Error("Lỗi kết nối"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun initCheckout(buyNowProductId: Int, cartItems: List<CartItem>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val shipJob = launch {
                    val res = RetrofitClient.apiService.getShippingMethods()
                    if (res.success) {
                        _shippingMethods.value = res.data
                        if (res.data.isNotEmpty()) _selectedShippingMethod.value = res.data[0]
                    }
                }
                val promoJob = launch {
                    val res = RetrofitClient.apiService.getPromotions()
                    if (res.success) _promotions.value = res.data
                }
                if (buyNowProductId != -1) {
                    val productRes = RetrofitClient.apiService.getProductDetail(buyNowProductId)
                    if (productRes.success) {
                        val p = productRes.data
                        _checkoutItems.value = listOf(
                            CartItem(0, p.ProductID, p.ProductName, p.Price, p.ThumbnailURL, 1)
                        )
                    }
                } else {
                    _checkoutItems.value = cartItems
                }
                shipJob.join()
                promoJob.join()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectShippingMethod(method: ShippingMethod) {
        _selectedShippingMethod.value = method
    }

    fun selectPromotion(promo: PromotionModel?) {
        _selectedPromotion.value = promo
    }

    fun placeOrder(
        user: UserModel,
        address: UserAddress,
        paymentMethodKey: String,
        finalPrice: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val paymentId = when (paymentMethodKey) {
                    "COD" -> 1
                    "BANK" -> 2
                    "MOMO" -> 3
                    else -> 1
                }
                val orderDetails = _checkoutItems.value.map { item ->
                    OrderDetailRequest(item.ProductID, item.quantity, item.Price)
                }
                val request = OrderRequest(
                    userId = user.UserID,
                    totalAmount = finalPrice,
                    shipAddress = "${address.StreetAddress}, ${address.City}",
                    receiverName = address.ReceiverName,
                    phoneNumber = address.PhoneNumber,
                    paymentMethodId = paymentId,
                    shippingMethodId = _selectedShippingMethod.value!!.ShippingMethodID,
                    items = orderDetails
                )
                val response = RetrofitClient.apiService.createOrder(request)
                if (response.success) {
                    _orderSuccessEvent.send(true)
                } else {
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isLoading.value = false
            }
        }
    }

    sealed interface ReviewResult {
        object Success : ReviewResult
        data class Error(val message: String) : ReviewResult
    }

    sealed interface CancelResult {
        object Success : CancelResult
        data class Error(val message: String) : CancelResult
    }
}