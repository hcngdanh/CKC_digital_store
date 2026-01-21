package com.example.doanltdd_ckcdigital.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd_ckcdigital.models.ProductModel
import com.example.doanltdd_ckcdigital.models.Review
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private var allProducts: List<ProductModel> = emptyList()

    private val _displayProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    val displayProducts: StateFlow<List<ProductModel>> = _displayProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategoryKey = MutableStateFlow<String?>(null)
    val selectedCategoryKey = _selectedCategoryKey.asStateFlow()

    private val _selectedSort = MutableStateFlow("Mặc định")
    val selectedSort = _selectedSort.asStateFlow()

    private val _isGridView = MutableStateFlow(true)
    val isGridView = _isGridView.asStateFlow()

    private val _displayTitle = MutableStateFlow("GIAN HÀNG SONY | MÁY ẢNH & PHỤ KIỆN \n PHÂN PHỐI BỞI CKC DIGITAL")
    val displayTitle = _displayTitle.asStateFlow()

    private val _productDetail = MutableStateFlow<ProductModel?>(null)
    val productDetail: StateFlow<ProductModel?> = _productDetail.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _detailLoading = MutableStateFlow(false)
    val detailLoading: StateFlow<Boolean> = _detailLoading.asStateFlow()



    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getProducts()
                if (response.success) {
                    allProducts = response.data
                    applyFilters()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadProductDetail(productId: Int, userId: Int?) {
        viewModelScope.launch {
            _detailLoading.value = true
            _productDetail.value = null
            _reviews.value = emptyList()

            try {
                val productRes = RetrofitClient.apiService.getProductDetail(productId)
                if (productRes.success) {
                    _productDetail.value = productRes.data
                }

                val reviewRes = RetrofitClient.apiService.getProductReviews(productId)
                if (reviewRes.success) {
                    _reviews.value = reviewRes.data
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _detailLoading.value = false
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isNotEmpty()) {
            _selectedCategoryKey.value = null
            updateTitle(null)
        }
        applyFilters()
    }

    fun onCategorySelected(categoryKey: String?) {
        _selectedCategoryKey.value = categoryKey
        _searchQuery.value = ""
        updateTitle(categoryKey)
        applyFilters()
    }

    fun onSortSelected(sortOption: String) {
        _selectedSort.value = sortOption
        applyFilters()
    }

    fun toggleViewMode(isGrid: Boolean) {
        _isGridView.value = isGrid
    }

    fun toggleFavorite(userId: Int, productId: Int) {
        viewModelScope.launch {
            _isFavorite.value = !_isFavorite.value

            try {
                val request = mapOf("userId" to userId, "productId" to productId)
                val response = RetrofitClient.apiService.toggleWishlist(request)

                if (response.success) {
                    _isFavorite.value = response.isFavorite
                }
            } catch (e: Exception) {
                _isFavorite.value = !_isFavorite.value
                e.printStackTrace()
            }
        }
    }

    fun checkFavoriteStatus(userId: Int, productId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.checkFavorite(userId, productId)
                if (response.success) {
                    _isFavorite.value = response.isFavorite
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun applyFilters() {
        var result = allProducts

        _selectedCategoryKey.value?.let { catKey ->
            result = result.filter { product ->
                when (catKey) {
                    "FullFrame" -> product.CategoryID == 6
                    "APS-C" -> product.CategoryID == 7
                    "LensGM" -> product.CategoryID == 8
                    "LensG" -> product.CategoryID == 9
                    "PHỤ KIỆN" -> product.CategoryID == 3
                    else -> true
                }
            }
        }

        val query = _searchQuery.value
        if (query.isNotBlank()) {
            result = result.filter {
                it.ProductName.contains(query.trim(), ignoreCase = true)
            }
        }

        result = when (_selectedSort.value) {
            "Giá tăng dần" -> result.sortedBy { it.Price }
            "Giá giảm dần" -> result.sortedByDescending { it.Price }
            else -> result
        }

        _displayProducts.value = result
    }

    private fun updateTitle(categoryKey: String?) {
        _displayTitle.value = when (categoryKey) {
            "FullFrame" -> "MÁY ẢNH SONY MIRRORLESS FULL FRAME"
            "APS-C" -> "MÁY ẢNH SONY MIRRORLESS APS-C"
            "LensG" -> "ỐNG KÍNH SONY DÒNG G"
            "LensGM" -> "ỐNG KÍNH SONY DÒNG G MASTER"
            "PHỤ KIỆN" -> "PHỤ KIỆN MÁY ẢNH & QUAY PHIM"
            else -> "GIAN HÀNG SONY | MÁY ẢNH & PHỤ KIỆN \n PHÂN PHỐI BỞI CKC DIGITAL"
        }
    }
}