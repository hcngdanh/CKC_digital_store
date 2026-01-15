package com.example.doanltdd_ckcdigital.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.models.OrderDetailResponse
import com.example.doanltdd_ckcdigital.models.CancelOrderRequest
// Đã xóa import AddReviewRequest vì không dùng nữa
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

// Hàm helper format date
fun formatDetailDateTime(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000Z", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("vi", "VN"))
        val date = inputFormat.parse(dateString)
        if (date != null) outputFormat.format(date) else dateString
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var orderData by remember { mutableStateOf<OrderDetailResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    // State cho Dialog Hủy đơn
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }

    // --- ĐÃ XÓA: State và Logic Dialog Đánh giá ---

    fun loadOrder() {
        scope.launch {
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
    }

    LaunchedEffect(orderId) {
        loadOrder()
    }

    // --- 1. DIALOG HỦY ĐƠN HÀNG ---
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Hủy đơn hàng", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Bạn có chắc chắn muốn hủy đơn hàng này?")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = { cancelReason = it },
                        label = { Text("Lý do hủy (bắt buộc)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (cancelReason.isBlank()) {
                            Toast.makeText(context, "Vui lòng nhập lý do hủy", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        scope.launch {
                            try {
                                val request = CancelOrderRequest(orderId = orderId, reason = cancelReason)
                                val res = RetrofitClient.apiService.cancelOrder(request)
                                if (res.success) {
                                    Toast.makeText(context, "Đã hủy đơn hàng thành công", Toast.LENGTH_SHORT).show()
                                    showCancelDialog = false
                                    loadOrder()
                                } else {
                                    Toast.makeText(context, res.message ?: "Lỗi hủy đơn", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Xác nhận Hủy")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }

    // --- ĐÃ XÓA: Khối hiển thị Dialog Đánh giá ---

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

                val statusColor = when(info.OrderStatus) {
                    "Chờ xử lý" -> Color(0xFFFF9800)
                    "Chờ lấy hàng" -> Color(0xFFFFC107)
                    "Đang giao hàng" -> Color(0xFF2196F3)
                    "Hoàn thành" -> Color(0xFF4CAF50)
                    "Đã hủy" -> Color.Red
                    else -> Color.Gray
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // --- CARD 1: TRẠNG THÁI ---
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RectangleShape,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("Trạng thái: ", fontSize = 15.sp)
                                Text(info.OrderStatus, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }

                    // --- CARD 2: ĐỊA CHỈ ---
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RectangleShape,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.padding(16.dp)) {
                                Icon(Icons.Default.LocationOn, null, tint = Color(0xFFFF5722))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Địa chỉ nhận hàng", fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(4.dp))
                                    Text(info.ShipAddress, fontSize = 14.sp, color = Color.DarkGray)
                                    if (!info.ReceiverName.isNullOrEmpty()) {
                                        Text("${info.ReceiverName} | ${info.PhoneNumber}", fontSize = 13.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    // --- CARD 3: DANH SÁCH SẢN PHẨM ---
                    item {
                        Text(
                            "Danh sách sản phẩm",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }

                    items(items) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                        ) {
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

                    // --- CARD 4: CHI TIẾT THANH TOÁN ---
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Chi tiết thanh toán", fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))

                                RowInfo("Phương thức thanh toán", info.PaymentMethod ?: "COD")
                                RowInfo("Đơn vị vận chuyển", info.ShippingMethod ?: "Tiêu chuẩn")

                                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                                // --- TÍNH TOÁN ---
                                val productTotal = items.sumOf { it.UnitPrice * it.Quantity }

                                val shippingCost = if (info.ShippingCost > 0) {
                                    info.ShippingCost
                                } else {
                                    when (info.ShippingMethod?.lowercase(Locale.ROOT)) {
                                        "hỏa tốc" -> 120000.0
                                        "nhanh" -> 55000.0
                                        else -> 35000.0
                                    }
                                }

                                val rawDiscount = (productTotal + shippingCost) - info.TotalAmount
                                val voucherDiscount = if (rawDiscount > 100) rawDiscount else 0.0

                                // --- HIỂN THỊ ---
                                Row(
                                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Tổng tiền hàng", color = Color.Gray, fontSize = 14.sp)
                                    Text(formatter.format(productTotal), fontSize = 14.sp, color = Color.Black)
                                }

                                Row(
                                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Phí vận chuyển", color = Color.Gray, fontSize = 14.sp)
                                    Text(formatter.format(shippingCost), fontSize = 14.sp, color = Color.Black)
                                }

                                if (voucherDiscount > 0) {
                                    Row(
                                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Voucher giảm giá", color = Color.Gray, fontSize = 14.sp)
                                        Text(
                                            "-${formatter.format(voucherDiscount)}",
                                            fontSize = 14.sp,
                                            color = Color(0xFF4CAF50)
                                        )
                                    }
                                }

                                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Tổng thanh toán", fontWeight = FontWeight.Bold)
                                    Text(
                                        formatter.format(info.TotalAmount),
                                        color = Color(0xFFD32F2F),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }

                    // --- CARD 5: THÔNG TIN THỜI GIAN ---
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Thời gian đặt hàng", fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.height(8.dp))
                                RowInfo("Mã đơn hàng", "#${info.OrderID}")
                                RowInfo("Ngày đặt hàng", formatDetailDateTime(info.OrderDate))
                            }
                        }
                    }

                    // --- CARD 6: CÁC NÚT THAO TÁC (ĐÃ XÓA NÚT ĐÁNH GIÁ) ---
                    item {
                        Spacer(Modifier.height(16.dp))

                        // Chỉ hiển thị nút HỦY nếu đơn đang chờ xử lý
                        if (info.OrderStatus == "Chờ xử lý" || info.OrderStatus == "Chờ xác nhận") {
                            Button(
                                onClick = {
                                    cancelReason = ""
                                    showCancelDialog = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("HỦY ĐƠN HÀNG", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(30.dp))
                    }
                }
            } else {
                // Màn hình lỗi
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