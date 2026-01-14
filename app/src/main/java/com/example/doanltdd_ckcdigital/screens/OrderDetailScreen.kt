package com.example.doanltdd_ckcdigital.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.models.OrderDetailResponse
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

// Hàm helper format date (copy từ OrderHistoryScreen hoặc tách ra file Utils dùng chung)
fun formatDetailDateTime(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000Z", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("vi", "VN"))
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString.replace("T", " ").substringBefore(".")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Int,
    onBackClick: () -> Unit
) {
    var orderData by remember { mutableStateOf<OrderDetailResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    LaunchedEffect(orderId) {
        try {
            isLoading = true
            hasError = false
            val response = RetrofitClient.apiService.getOrderDetail(orderId)
            if (response.success && response.data != null) {
                orderData = response.data
            } else {
                hasError = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            hasError = true
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thông tin đơn hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color(0xFFFF5722),
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (orderData != null && !hasError) {
                val info = orderData!!.orderInfo
                val items = orderData!!.orderItems

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Trạng thái đơn hàng
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("Trạng thái: ", fontSize = 15.sp)
                                Text(info.OrderStatus, color = Color(0xFFFF5722), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }

                    // 2. Địa chỉ nhận hàng
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Row(Modifier.padding(16.dp)) {
                                Icon(Icons.Default.LocationOn, null, tint = Color(0xFFFF5722))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Địa chỉ nhận hàng", fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(4.dp))
                                    Text(info.ShipAddress, fontSize = 14.sp, color = Color.DarkGray)
                                }
                            }
                        }
                    }

                    // 3. Danh sách sản phẩm
                    item { Text("Danh sách sản phẩm", fontWeight = FontWeight.Bold) }

                    items(items) { item ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Row(Modifier.padding(12.dp)) {
                                AsyncImage(
                                    model = item.ThumbnailURL,
                                    contentDescription = null,
                                    modifier = Modifier.size(70.dp).background(Color(0xFFFAFAFA)),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(item.ProductName, fontWeight = FontWeight.Medium, maxLines = 2, fontSize = 14.sp)
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("x${item.Quantity}", color = Color.Gray, fontSize = 13.sp)
                                        Text(formatter.format(item.UnitPrice), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }

                    // 4. Chi tiết thanh toán
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Chi tiết thanh toán", fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                RowInfo("Phương thức thanh toán", info.PaymentMethod ?: "COD")
                                RowInfo("Đơn vị vận chuyển", info.ShippingMethod ?: "Tiêu chuẩn")
                                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Thành tiền", fontWeight = FontWeight.Bold)
                                    Text(formatter.format(info.TotalAmount), color = Color(0xFFFF5722), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                            }
                        }
                    }

                    // 5. THÔNG TIN MÃ ĐƠN & THỜI GIAN (Thêm mới)
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Thời gian đặt hàng", fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.height(8.dp))
                                RowInfo("Mã đơn hàng", "#${info.OrderID}")
                                // Hiển thị ngày giờ đặt hàng
                                RowInfo("Ngày đặt hàng", formatDetailDateTime(info.OrderDate.toString()))
                            }
                        }
                        Spacer(Modifier.height(24.dp)) // Khoảng trống cuối cùng
                    }
                }
            } else {
                // Error UI
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Warning, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                    Text(text = "Không tìm thấy thông tin đơn hàng!", color = Color.Gray)
                    Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))) {
                        Text("Quay lại")
                    }
                }
            }
        }
    }
}

@Composable
fun RowInfo(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontSize = 14.sp, color = Color.Black)
    }
}