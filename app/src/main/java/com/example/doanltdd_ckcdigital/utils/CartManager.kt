package com.example.doanltdd_ckcdigital.utils

import androidx.compose.runtime.mutableStateListOf
import com.example.doanltdd_ckcdigital.models.ProductModel

// Định nghĩa Model cho từng dòng trong giỏ hàng
data class CartItem(
    val product: ProductModel,
    val ProductID: Int,
    val ProductName: String,
    val Price: Double,
    val ThumbnailURL: String?,
    val quantity: Int
)

object CartManager {
    // Sử dụng mutableStateListOf để Compose tự động cập nhật UI khi giỏ hàng thay đổi
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    // Tổng số lượng sản phẩm (Ví dụ: 2 máy ảnh + 3 ống kính = 5)
    val cartCount: Int get() = _cartItems.sumOf { it.quantity }

    // Số lượng loại sản phẩm hiển thị trên Badge icon giỏ hàng
    val badgeCartCount: Int get() = _cartItems.size

    /**
     * Thêm sản phẩm vào giỏ hàng hoặc tăng số lượng nếu đã tồn tại
     */
    fun addToCart(product: ProductModel) {
        val index = _cartItems.indexOfFirst { it.product.ProductID == product.ProductID }
        if (index != -1) {
            // Nếu sản phẩm đã có trong giỏ, tạo bản sao và tăng quantity
            val item = _cartItems[index]
            _cartItems[index] = item.copy(quantity = item.quantity + 1)
        } else {
            // Nếu là sản phẩm mới, điền đầy đủ 6 tham số để tránh lỗi "No value passed"
            _cartItems.add(
                CartItem(
                    product = product,
                    ProductID = product.ProductID,
                    ProductName = product.ProductName,
                    Price = product.Price,
                    ThumbnailURL = product.ThumbnailURL,
                    quantity = 1
                )
            )
        }
    }

    /**
     * Xử lý cho nút "MUA NGAY" tại ProductDetailScreen
     */
    fun buyNow(product: ProductModel) {
        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        val index = _cartItems.indexOfFirst { it.product.ProductID == product.ProductID }
        if (index == -1) {
            // Nếu chưa có thì thêm mới vào
            addToCart(product)
        }
        // Sau đó logic tại Screen sẽ navigate sang "checkout"
    }

    fun removeProduct(item: CartItem) {
        _cartItems.remove(item)
    }

    fun increaseQuantity(item: CartItem) {
        val index = _cartItems.indexOf(item)
        if (index != -1) {
            _cartItems[index] = item.copy(quantity = item.quantity + 1)
        }
    }

    fun decreaseQuantity(item: CartItem) {
        val index = _cartItems.indexOf(item)
        if (index != -1) {
            if (item.quantity > 1) {
                _cartItems[index] = item.copy(quantity = item.quantity - 1)
            } else {
                _cartItems.removeAt(index)
            }
        }
    }

    /**
     * Tính tổng tiền dựa trên giá và số lượng thực tế
     */
    fun getTotalPrice(): Double {
        return _cartItems.sumOf { it.Price * it.quantity }
    }

    fun clearCart() {
        _cartItems.clear()
    }
}