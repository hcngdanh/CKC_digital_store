package com.example.doanltdd_ckcdigital.services

import com.example.doanltdd_ckcdigital.models.ApiResponse
import com.example.doanltdd_ckcdigital.models.AuthResponse
import com.example.doanltdd_ckcdigital.models.CategoryModel
import com.example.doanltdd_ckcdigital.models.LoginRequest
import com.example.doanltdd_ckcdigital.models.ProductAddToCart
import com.example.doanltdd_ckcdigital.models.ProductModel
import com.example.doanltdd_ckcdigital.models.RegisterRequest
import com.example.doanltdd_ckcdigital.models.Review
import com.example.doanltdd_ckcdigital.models.ReviewResponse
import com.example.doanltdd_ckcdigital.models.SimpleResponse
import com.example.doanltdd_ckcdigital.models.UserAddress
import com.example.doanltdd_ckcdigital.models.UserModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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


    @POST("api/cart/add")
    suspend fun addToRemoteCart(@Body request: Map<String, Int>): SimpleResponse
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    @POST("api/cart/add")
    suspend fun addToCart(@Body request: ProductAddToCart): SimpleResponse

    @GET("api/cart")
    suspend fun getCartItems(): ApiResponse<List<ProductModel>>


}

object RetrofitClient {
    private const val BASE_URL = "https://server-api-doan.onrender.com/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}