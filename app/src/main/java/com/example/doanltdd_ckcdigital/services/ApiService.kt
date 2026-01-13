package com.example.doanltdd_ckcdigital.services

import com.example.doanltdd_ckcdigital.models.ApiResponse
import com.example.doanltdd_ckcdigital.models.AuthResponse
import com.example.doanltdd_ckcdigital.models.CartItemResponse
import com.example.doanltdd_ckcdigital.models.CategoryModel
import com.example.doanltdd_ckcdigital.models.LoginRequest
import com.example.doanltdd_ckcdigital.models.Order
import com.example.doanltdd_ckcdigital.models.OrderDetailResponse
import com.example.doanltdd_ckcdigital.models.OrderHistoryModel
import com.example.doanltdd_ckcdigital.models.OrderRequest
import com.example.doanltdd_ckcdigital.models.ProductAddToCart
import com.example.doanltdd_ckcdigital.models.ProductModel
import com.example.doanltdd_ckcdigital.models.PromotionModel
import com.example.doanltdd_ckcdigital.models.RegisterRequest
import com.example.doanltdd_ckcdigital.models.Review
import com.example.doanltdd_ckcdigital.models.ShippingMethod
import com.example.doanltdd_ckcdigital.models.SimpleResponse
import com.example.doanltdd_ckcdigital.models.UserAddress
import com.example.doanltdd_ckcdigital.models.UserModel
import com.example.doanltdd_ckcdigital.models.Voucher
import com.example.doanltdd_ckcdigital.modelsimport.DashboardStatsResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("api/products")
    suspend fun getProducts(): ApiResponse<List<ProductModel>>

    @GET("api/categories")
    suspend fun getCategories(): ApiResponse<List<CategoryModel>>

    @GET("api/products/{id}")
    suspend fun getProductDetail(@Path("id") id: Int): ApiResponse<ProductModel>

    @GET("api/products/{id}/reviews")
    suspend fun getProductReviews(@Path("id") productId: Int): ApiResponse<List<Review>>

    @GET("api/addresses/{userId}")
    suspend fun getUserAddresses(@Path("userId") userId: Int): List<UserAddress>

    @DELETE("api/addresses/{addressId}")
    suspend fun deleteAddress(@Path("addressId") addressId: Int): SimpleResponse

    @GET("api/addresses/detail/{addressId}")
    suspend fun getAddressDetail(@Path("addressId") addressId: Int): UserAddress

    @PUT("api/addresses/{addressId}")
    suspend fun updateAddress(@Path("addressId") addressId: Int, @Body address: UserAddress): SimpleResponse

    @POST("api/addresses")
    suspend fun addAddress(@Body address: UserAddress): SimpleResponse


    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    @POST("api/cart/add")
    suspend fun addToCart(@Body request: ProductAddToCart): SimpleResponse

    @GET("api/cart/{userId}")
    suspend fun getCartItems(@Path("userId") userId: Int): ApiResponse<List<CartItemResponse>>

    @PUT("api/cart/update")
    suspend fun updateCartQuantity(@Body request: Map<String, Int>): SimpleResponse

    // Hàm xóa sản phẩm theo CartItemID
    @DELETE("api/cart/remove/{cartItemId}")
    suspend fun removeCartItem(@Path("cartItemId") cartItemId: Int): SimpleResponse


    @GET("api/vouchers")
    suspend fun getVouchers(): ApiResponse<List<Voucher>>

    @PUT("api/auth/update-profile/{userId}")
    suspend fun updateProfile(
        @Path("userId") userId: Int,
        @Body request: Map<String, String>
    ): ApiResponse<UserModel>

    @PUT("api/auth/change-password/{userId}")
    suspend fun changePassword(
        @Path("userId") userId: Int,
        @Body request: Map<String, String>
    ): SimpleResponse

    @GET("api/promotions")
    suspend fun getPromotions(): ApiResponse<List<PromotionModel>>

    @GET("api/shipping-methods")
    suspend fun getShippingMethods(): ApiResponse<List<ShippingMethod>>

    @POST("api/orders")
    suspend fun createOrder(@Body request: OrderRequest): SimpleResponse

    @GET("api/orders/user/{userId}")
    suspend fun getUserOrders(@Path("userId") userId: Int): ApiResponse<List<OrderHistoryModel>>

    @GET("api/orders/detail/{orderId}")
    suspend fun getOrderDetail(@Path("orderId") orderId: Int): ApiResponse<OrderDetailResponse>

    @GET("api/admin/orders")
    suspend fun getAllOrders(): ApiResponse<List<Order>>

    @GET("api/admin/dashboard-stats")
    suspend fun getDashboardStats(): ApiResponse<DashboardStatsResponse>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}