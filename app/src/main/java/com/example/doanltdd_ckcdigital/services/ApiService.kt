package com.example.doanltdd_ckcdigital.services

import com.example.doanltdd_ckcdigital.models.ApiResponse
import com.example.doanltdd_ckcdigital.models.CategoryModel
import com.example.doanltdd_ckcdigital.models.ProductModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("api/products")
    suspend fun getProducts(): ApiResponse<List<ProductModel>>

    @GET("api/categories")
    suspend fun getCategories(): ApiResponse<List<CategoryModel>>

    @GET("api/products/{id}")
    suspend fun getProductDetail(@Path("id") id: Int): ApiResponse<ProductModel>
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