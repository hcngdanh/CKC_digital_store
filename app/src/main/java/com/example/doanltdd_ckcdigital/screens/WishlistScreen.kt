package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.models.ProductModel
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    userId: Int,
    onBackClick: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    var wishlist by remember { mutableStateOf<List<ProductModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Hàm load dữ liệu
    fun loadData() {
        isLoading = true // set loading để UI cập nhật
        /* Dùng CoroutineScope hoặc LaunchedEffect để gọi API */
    }

    LaunchedEffect(userId) {
        try {
            val response = RetrofitClient.apiService.getWishlist(userId)
            if (response.success) {
                wishlist = response.data
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Danh sách yêu thích", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFF5722))
            }
        } else if (wishlist.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chưa có sản phẩm yêu thích nào")
            }
        } else {
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(wishlist) { product ->
                    WishlistItem(product, onClick = { onProductClick(product.ProductID) })
                }
            }
        }
    }
}

@Composable
fun WishlistItem(product: ProductModel, onClick: () -> Unit) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = product.ThumbnailURL,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.ProductName, fontWeight = FontWeight.Bold, maxLines = 2)
                Text(formatter.format(product.Price), color = Color(0xFFFF5722), fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red)
        }
    }
}