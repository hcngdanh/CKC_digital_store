package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ShoppingCart
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
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

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

    LaunchedEffect(productId) {
        try {
            isLoading = true
            val response = RetrofitClient.apiService.getProductDetail(productId)
            if (response.success) {
                product = response.data
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
                        IconButton(onClick = onCartClick) {
                            Icon(Icons.Outlined.ShoppingCart, null, tint = Color.White)
                        }
                    }
                )}
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = product!!.ThumbnailURL,
                        contentDescription = product!!.ProductName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Fit
                    )
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
                    SpecRow(label = "Ngàm ống kính", value = product!!.LensMount ?: "Đang cập nhật")
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    SpecRow(label = "Kích thước sensor", value = product!!.SensorType ?: "Đang cập nhật")
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    SpecRow(label = "Độ phân giải", value = product!!.Resolution ?: "Đang cập nhật")
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    SpecRow(label = "Vi xử lý", value = product!!.Processor ?: "Đang cập nhật")
                    HorizontalDivider(color = Color(0xFFEEEEEE))
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
                    Text(
                        text = product!!.FullDescription ?: "Chưa có mô tả chi tiết cho sản phẩm này.",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Justify
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
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