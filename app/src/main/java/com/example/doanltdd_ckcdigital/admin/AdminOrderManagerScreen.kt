package com.example.doanltdd_ckcdigital.admin

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doanltdd_ckcdigital.models.Order
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// --- ENUM TRẠNG THÁI (Đã chuẩn hóa theo Database) ---
enum class OrderStatus(val apiName: String, val label: String, val color: Color) {
    PENDING("pending", "Chờ xử lý", Color(0xFFFF9800)),       // Cam
    WAITING("pickup", "Chờ lấy hàng", Color(0xFFFFC107)),     // Vàng
    SHIPPING("shipping", "Đang giao hàng", Color(0xFF2196F3)),// Xanh dương
    COMPLETED("completed", "Hoàn thành", Color(0xFF4CAF50)),  // Xanh lá
    CANCELLED("cancelled", "Đã hủy", Color(0xFFE91E63));      // Đỏ

    companion object {
        fun fromString(status: String?): OrderStatus {
            return entries.find {
                it.apiName.equals(status, ignoreCase = true) ||
                        it.label.equals(status, ignoreCase = true) ||
                        // Map chính xác chuỗi từ Database để tránh lỗi hiển thị
                        (status == "Chờ giao hàng" && it == WAITING) ||
                        (status == "Đang giao" && it == SHIPPING)
            } ?: PENDING
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderManagerScreen(
    onBackClick: () -> Unit,
    onOrderClick: (Int) -> Unit
) {
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Tất cả") + OrderStatus.entries.map { it.label }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val response = RetrofitClient.apiService.getAllOrders()
            if (response.success) {
                orders = response.data ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    val filteredList = remember(orders, selectedTabIndex) {
        if (selectedTabIndex == 0) {
            orders
        } else {
            val targetStatus = OrderStatus.entries.getOrNull(selectedTabIndex - 1) ?: OrderStatus.PENDING
            orders.filter { OrderStatus.fromString(it.status) == targetStatus }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quản lý Đơn hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFF2196F3),
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) Color(0xFF2196F3) else Color.Gray,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có đơn hàng nào", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { order ->
                        AdminOrderCardItem(
                            order = order,
                            onClick = { onOrderClick(order.orderId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderCardItem(order: Order, onClick: () -> Unit) {
    val status = OrderStatus.fromString(order.status)

    // --- CẬP NHẬT HIỂN THỊ NGÀY GIỜ (dd/MM/yyyy HH:mm) ---
    val dateStr = try {
        if (order.orderDate.isNullOrEmpty()) "N/A"
        else {
            // Định dạng đầu vào từ MySQL: yyyy-MM-dd HH:mm:ss
            val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            // Định dạng đầu ra đẹp hơn: 14/01/2026 16:25
            val output = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN"))

            val date = input.parse(order.orderDate)
            if (date != null) output.format(date) else order.orderDate
        }
    } catch (e: Exception) {
        order.orderDate ?: "N/A"
    }

    val displayName = if (!order.receiverName.isNullOrEmpty()) {
        order.receiverName
    } else {
        order.userFullName ?: "Khách lẻ"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Đơn #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(displayName, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                // Hiển thị ngày giờ đã format
                Text(dateStr, fontSize = 12.sp, color = Color.Gray)
            }

            Surface(
                color = status.color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, status.color)
            ) {
                Text(
                    text = status.label,
                    color = status.color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}