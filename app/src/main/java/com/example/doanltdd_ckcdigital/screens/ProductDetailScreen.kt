package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.models.ProductModel
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import com.example.doanltdd_ckcdigital.utils.CartManager
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.example.doanltdd_ckcdigital.models.Review

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onBuyNowClick: () -> Unit,
    onAddToCart: (ProductModel) -> Unit
) {
    var product by remember { mutableStateOf<ProductModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }

    LaunchedEffect(productId) {
        try {
            isLoading = true
            val response = RetrofitClient.apiService.getProductDetail(productId)
            if (response.success) {
                product = response.data
            }

            val reviewRes = RetrofitClient.apiService.getProductReviews(productId)
            if (reviewRes.success) {
                reviews = reviewRes.data
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.Black)
                    .statusBarsPadding()
            ) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Black
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = { onBackClick() },
                            modifier = Modifier.padding(start = 8.dp).size(40.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(25.dp))
                        }
                    },
                    title = {
                        AsyncImage(
                            model = "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png",
                            contentDescription = "CKC Digital Logo",
                            modifier = Modifier.height(35.dp),
                            contentScale = ContentScale.Fit
                        )
                    },
                    actions = {
                        BadgedBox(
                            badge = {
                                if (CartManager.badgeCartCount > 0) {
                                    Badge(
                                        containerColor = Color.Red,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = CartManager.badgeCartCount.toString())
                                    }
                                }
                            }
                        ) {
                            IconButton(onClick = { onCartClick() }) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Giỏ hàng",
                                    tint = Color.White
                                )
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
                onAddToCart = {
                    if (product != null) {
                        onAddToCart(product!!)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        } else if (product == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy thông tin sản phẩm", color = Color.Gray)
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
                    val list = mutableListOf<String>()
                    // Thêm ảnh thumbnail vào đầu danh sách nếu có
                    product?.ThumbnailURL?.let { list.add(it) }
                    // Thêm toàn bộ danh sách Gallery vào sau
                    product?.Gallery?.let { list.addAll(it) }
                    list
                }

                val pagerState = rememberPagerState(pageCount = { images.size })

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) { page ->
                        AsyncImage(
                            model = images[page],
                            contentDescription = product!!.ProductName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Row(
                        Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(pagerState.pageCount) { iteration ->
                            val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = product!!.ProductName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatter.format(product!!.Price),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFD32F2F)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = "Còn hàng",
                                color = Color(0xFF2E7D32),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chính hãng Sony Việt Nam", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Thông số kỹ thuật",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val categoryId = product!!.CategoryID
                    val isBody = categoryId == 1 || categoryId == 6 || categoryId == 7
                    val isLens = categoryId == 2 || categoryId in 8..10

                    if (isBody || isLens) {
                        SpecRow(label = "Ngàm ống kính", value = product!!.LensMount ?: "Đang cập nhật")
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }

                    if (isBody) {
                        SpecRow(label = "Kích thước sensor", value = product!!.SensorType ?: "Đang cập nhật")
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }

                    val resValue = product!!.Resolution
                    if (!resValue.isNullOrEmpty()) {
                        val label = when {
                            resValue.contains("Bluetooth", true) || resValue.contains("Jack", true) || resValue.contains("USB", true) -> "Kết nối qua"
                            resValue.contains("mAh", true) || resValue.contains("Wh", true) -> "Dung lượng pin"
                            resValue.contains("Hz", true) || resValue.contains("dB", true) -> "Tần số/Độ nhạy"
                            resValue.contains("MP", true) -> "Độ phân giải"
                            resValue.contains("GB", true) || resValue.contains("TB", true) -> "Dung lượng"
                            else -> "Thông số khác"
                        }
                        SpecRow(label = label, value = resValue)
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }

                    if (isBody) {
                        SpecRow(label = "Vi xử lý", value = product!!.Processor ?: "Đang cập nhật")
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }

                    SpecRow(label = "Bảo hành", value = product!!.WarrantyPeriod ?: "Đang cập nhật")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Mô tả sản phẩm",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    val description = if (!product!!.FullDescription.isNullOrEmpty()) {
                        product!!.ShortDescription
                    } else if (!product!!.ShortDescription.isNullOrEmpty()) {
                        product!!.FullDescription
                    } else {
                        "Chưa có mô tả chi tiết cho sản phẩm này."
                    }

                    Text(
                        text = product!!.FullDescription.toString(),
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Justify
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Đánh giá từ khách hàng",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (reviews.isEmpty()) {
                        Text(
                            text = "Sản phẩm này chưa có đánh giá nào.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        reviews.forEach { review ->
                            ReviewItem(review)
                            HorizontalDivider(
                                color = Color(0xFFEEEEEE),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(text = value, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(2f), textAlign = TextAlign.End)
    }
}


@Composable
fun BottomActionBar(
    product: ProductModel?,
    onBuyNowClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Surface(
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onAddToCart,
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color(0xFF050505)),
                modifier = Modifier.height(48.dp),
                enabled = product != null
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color(0xFF000000))
            }

            Button(
                onClick = onBuyNowClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000000)),
                shape = RoundedCornerShape(4.dp),
                enabled = product != null
            ) {
                Text("MUA NGAY", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = review.UserName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )
            // Hiển thị số sao
            Row {
                repeat(5) { index ->
                    Text(
                        text = "★",
                        color = if (index < review.Rating) Color(0xFFFFB400) else Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Text(
            text = review.ReviewDate.split("T")[0],
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = review.Comment,
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 20.sp
        )
    }
}