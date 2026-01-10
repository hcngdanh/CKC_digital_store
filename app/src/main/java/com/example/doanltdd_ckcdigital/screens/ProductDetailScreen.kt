package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.models.*
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import com.example.doanltdd_ckcdigital.utils.CartManager
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onBuyNowClick: (ProductModel) -> Unit,
    onAddToCart: (ProductModel) -> Unit
) {
    var product by remember { mutableStateOf<ProductModel?>(null) }
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    LaunchedEffect(productId) {
        try {
            isLoading = true
            val productRes = RetrofitClient.apiService.getProductDetail(productId)
            if (productRes.success) product = productRes.data

            val reviewRes = RetrofitClient.apiService.getProductReviews(productId)
            if (reviewRes.success) reviews = reviewRes.data
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.Black).statusBarsPadding()) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black),
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                    },
                    title = {
                        AsyncImage(
                            model = "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png",
                            contentDescription = "Logo",
                            modifier = Modifier.height(35.dp),
                            contentScale = ContentScale.Fit
                        )
                    },
                    actions = {
                        BadgedBox(badge = {
                            if (CartManager.badgeCartCount > 0) {
                                Badge(containerColor = Color.Red) { Text(CartManager.badgeCartCount.toString()) }
                            }
                        }) {
                            IconButton(onClick = onCartClick) {
                                Icon(Icons.Default.ShoppingCart, "Cart", tint = Color.White)
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            BottomActionBar(
                product = product,
                onBuyNowClick = onBuyNowClick,
                onAddToCart = { product?.let { onAddToCart(it) } }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                CircularProgressIndicator(color = Color.Black)
            }
        } else if (product == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                Text("Không tìm thấy sản phẩm", color = Color.Gray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF5F5F5))
                    .verticalScroll(rememberScrollState())
            ) {
                val images = remember(product) {
                    mutableListOf<String>().apply {
                        product?.ThumbnailURL?.let { add(it) }
                        product?.Gallery?.let { addAll(it) }
                    }
                }
                val pagerState = rememberPagerState(pageCount = { images.size })

                Box(Modifier.fillMaxWidth().background(Color.White).padding(vertical = 16.dp)) {
                    HorizontalPager(state = pagerState, modifier = Modifier.height(300.dp)) { page ->
                        AsyncImage(
                            model = images[page],
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Row(Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)) {
                        repeat(pagerState.pageCount) { i ->
                            val color = if (pagerState.currentPage == i) Color.DarkGray else Color.LightGray
                            Box(Modifier.padding(2.dp).clip(CircleShape).background(color).size(8.dp))
                        }
                    }
                }

                Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
                    Text(product!!.ProductName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(formatter.format(product!!.Price), fontSize = 24.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.ExtraBold)

                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                            Text("Còn hàng", color = Color(0xFF2E7D32), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Chính hãng Sony Việt Nam", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(Modifier.height(8.dp))
                Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
                    Text("Thông số kỹ thuật", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))

                    val categoryId = product!!.CategoryID
                    if (categoryId in listOf(1, 6, 7, 2, 8, 9, 10)) {
                        SpecRow("Ngàm ống kính", product!!.LensMount ?: "N/A")
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }
                    if (categoryId in listOf(1, 6, 7)) {
                        SpecRow("Cảm biến", product!!.SensorType ?: "N/A")
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }
                    product!!.Resolution?.let { SpecRow("Chi tiết", it) }
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    SpecRow("Bảo hành", product!!.WarrantyPeriod ?: "N/A")
                }

                Spacer(Modifier.height(8.dp))
                Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
                    Text("Mô tả sản phẩm", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = product!!.FullDescription ?: "Chưa có mô tả",
                        fontSize = 14.sp, color = Color.DarkGray, lineHeight = 22.sp
                    )
                }

                Spacer(Modifier.height(8.dp))
                Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
                    Text("Đánh giá khách hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (reviews.isEmpty()) {
                        Text("Chưa có đánh giá nào", color = Color.Gray, modifier = Modifier.padding(vertical = 12.dp))
                    } else {
                        reviews.forEach { ReviewItem(it) }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.Medium, textAlign = TextAlign.End, modifier = Modifier.weight(2f))
    }
}

@Composable
fun BottomActionBar(product: ProductModel?, onBuyNowClick: (ProductModel) -> Unit, onAddToCart: () -> Unit) {
    Surface(shadowElevation = 8.dp, color = Color.White) {
        Row(Modifier.fillMaxWidth().padding(12.dp).navigationBarsPadding(), Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onAddToCart,
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(4.dp),
                enabled = product != null
            ) { Icon(Icons.Default.ShoppingCart, null, tint = Color.Black) }

            Button(
                onClick = { product?.let { onBuyNowClick(it) } },
                modifier = Modifier.weight(1f).height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(4.dp),
                enabled = product != null
            ) { Text("MUA NGAY", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text(review.UserName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row {
                repeat(5) { i ->
                    Text("★", color = if (i < review.Rating) Color(0xFFFFB400) else Color.LightGray)
                }
            }
        }
        Text(review.ReviewDate.split("T")[0], fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Text(review.Comment, fontSize = 14.sp, color = Color.DarkGray)
        HorizontalDivider(Modifier.padding(top = 8.dp), color = Color(0xFFEEEEEE))
    }
}