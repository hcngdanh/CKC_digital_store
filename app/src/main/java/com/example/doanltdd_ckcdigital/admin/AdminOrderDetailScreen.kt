package com.example.doanltdd_ckcdigital.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.models.OrderDetailResponse
import com.example.doanltdd_ckcdigital.services.RetrofitClient
// Import OrderStatus từ file AdminOrderManagerScreen.kt (nếu chung package admin)
import com.example.doanltdd_ckcdigital.admin.OrderStatus

import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailScreen(
    orderId: Int,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var orderDetail by remember { mutableStateOf<OrderDetailResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var selectedStatus by remember { mutableStateOf(OrderStatus.PENDING) }

    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    fun loadOrder() {
        scope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.apiService.getOrderDetail(orderId)
                if (response.success) {
                    orderDetail = response.data
                    val current = OrderStatus.fromString(response.data?.orderInfo?.OrderStatus)
                    selectedStatus = current
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Lỗi tải đơn hàng: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(orderId) {
        loadOrder()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chi tiết đơn hàng #$orderId", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (orderDetail != null) {
                if (selectedStatus != OrderStatus.COMPLETED && selectedStatus != OrderStatus.CANCELLED) {
                    Surface(shadowElevation = 8.dp) {
                        Button(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("CẬP NHẬT TRẠNG THÁI")
                        }
                    }
                } else if (selectedStatus == OrderStatus.COMPLETED) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = "Đơn hàng đã hoàn thành",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (selectedStatus == OrderStatus.CANCELLED) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = "Đơn hàng đã bị hủy",
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (orderDetail != null) {
            val info = orderDetail!!.orderInfo
            val items = orderDetail!!.orderItems
            val currentStatusEnum = OrderStatus.fromString(info.OrderStatus)

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 1. Card Trạng thái
                Card(
                    colors = CardDefaults.cardColors(containerColor = currentStatusEnum.color.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Trạng thái:", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = currentStatusEnum.label,
                            color = currentStatusEnum.color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 2. Thông tin khách hàng
                Text("Thông tin giao hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.padding(top = 8.dp).fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(text = info.ReceiverName ?: "Khách hàng", fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(text = info.PhoneNumber ?: "Không có số điện thoại")
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Place, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(info.ShipAddress, color = Color.DarkGray)
                        }
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray) // Đường kẻ phân cách
                        Spacer(Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, null, tint = Color(0xFF2196F3), modifier = Modifier.size(20.dp)) // Màu xanh dương
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Vận chuyển: ",
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Text(
                                text = info.ShippingMethod ?: "Tiêu chuẩn",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payment, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Thanh toán: ",
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Text(
                                text = info.PaymentMethod ?: "Thanh toán khi nhận hàng (COD)",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 3. Sản phẩm
                Text("Sản phẩm (${items.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                items.forEach { item ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.padding(top = 8.dp).fillMaxWidth()) {
                        Row(Modifier.padding(12.dp)) {
                            AsyncImage(
                                model = item.ThumbnailURL,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp).background(Color.LightGray)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(item.ProductName, maxLines = 2, fontWeight = FontWeight.Medium)
                                Text("x${item.Quantity}", color = Color.Gray)
                                Text(formatter.format(item.UnitPrice), color = Color(0xFFFF5722), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 4. Tổng tiền
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tổng thanh toán", fontWeight = FontWeight.Bold)
                        Text(formatter.format(info.TotalAmount), color = Color(0xFFFF5722), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
                Spacer(Modifier.height(80.dp))
            }
        }
    }

    // --- BOTTOM SHEET CẬP NHẬT TRẠNG THÁI (Đã bỏ lý do hủy) ---
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 30.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Cập nhật trạng thái", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))

                OrderStatus.entries.forEach { status ->
                    val isSelected = selectedStatus == status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStatus = status }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedStatus = status },
                            colors = RadioButtonDefaults.colors(selectedColor = status.color)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(status.label, fontWeight = FontWeight.Bold, color = status.color)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val body = mapOf(
                                    "status" to selectedStatus.apiName,
                                    "cancelReason" to "" // Gửi rỗng vì đã bỏ ô nhập
                                )
                                val res = RetrofitClient.apiService.updateOrderStatus(orderId, body)
                                if (res.success) {
                                    Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                                    loadOrder()
                                    showBottomSheet = false
                                } else {
                                    Toast.makeText(context, "Lỗi: ${res.message}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Lỗi mạng", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("XÁC NHẬN")
                }
            }
        }
    }
}