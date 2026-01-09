package com.example.doanltdd_ckcdigital.services

import com.example.doanltdd_ckcdigital.models.ApiResponse
import com.example.doanltdd_ckcdigital.models.AuthResponse
import com.example.doanltdd_ckcdigital.models.CategoryModel
import com.example.doanltdd_ckcdigital.models.LoginRequest
import com.example.doanltdd_ckcdigital.models.ProductModel
import com.example.doanltdd_ckcdigital.models.Review
import com.example.doanltdd_ckcdigital.models.ReviewResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    @GET("api/products")
    suspend fun getProducts(): ApiResponse<List<ProductModel>>

    @GET("api/categories")
    suspend fun getCategories(): ApiResponse<List<CategoryModel>>

    @GET("api/products/{id}")
    suspend fun getProductDetail(@Path("id") id: Int): ApiResponse<ProductModel>

    @GET("api/products/{id}/reviews")
    suspend fun getProductReviews(@Path("id") productId: Int): ApiResponse<List<Review>>
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