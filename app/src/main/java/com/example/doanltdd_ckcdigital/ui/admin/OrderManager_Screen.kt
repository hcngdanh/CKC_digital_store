package com.example.doanltdd_ckcdigital.ui.admin

import androidx.compose.foundation.BorderStroke
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doanltdd_ckcdigital.models.Order
import com.example.doanltdd_ckcdigital.viewmodels.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Locale

enum class OrderStatus(val apiName: String, val label: String, val color: Color) {
    PENDING("Chờ xử lý", "Chờ xử lý", Color(0xFFFF9800)),
    WAITING("Chờ lấy hàng", "Chờ lấy hàng", Color(0xFFFFC107)),
    SHIPPING("Đang giao hàng", "Đang giao hàng", Color(0xFF2196F3)),
    COMPLETED("Hoàn thành", "Hoàn thành", Color(0xFF4CAF50)),
    CANCELLED("Đã hủy", "Đã hủy", Color(0xFFE91E63));

    companion object {
        fun fromString(status: String?): OrderStatus {
            return entries.find {
                it.apiName.equals(status, ignoreCase = true) ||
                        it.label.equals(status, ignoreCase = true) ||
                        (status.equals("pending", ignoreCase = true) && it == PENDING) ||
                        (status.equals("pickup", ignoreCase = true) && it == WAITING) ||
                        (status.equals("shipping", ignoreCase = true) && it == SHIPPING) ||
                        (status.equals("completed", ignoreCase = true) && it == COMPLETED) ||
                        (status.equals("cancelled", ignoreCase = true) && it == CANCELLED)
            } ?: PENDING
        }
    }
}

object DateFormatter {
    private val inputFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val inputFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN"))

    fun format(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "N/A"
        return try {
            var date = inputFormat1.parse(dateString)
            if (date == null) {
                date = inputFormat2.parse(dateString)
            }
            if (date != null) outputFormat.format(date) else dateString
        } catch (e: Exception) {
            dateString
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderManagerScreen(
    onBackClick: () -> Unit,
    onOrderClick: (Int) -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val filteredList by viewModel.filteredOrders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedTabIndex by viewModel.selectedOrderTabIndex.collectAsState()

    val tabs = remember { listOf("Tất cả") + OrderStatus.entries.map { it.label } }

    LaunchedEffect(Unit) {
        viewModel.fetchAllOrders()
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
                        onClick = { viewModel.setOrderFilterTab(index) },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) Color(0xFF2196F3) else Color.Gray,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            if (isLoading && filteredList.isEmpty()) {
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
                    items(filteredList, key = { it.orderId }) { order ->
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
    val status = remember(order.status) { OrderStatus.fromString(order.status) }
    val dateStr = remember(order.orderDate) { DateFormatter.format(order.orderDate) }
    val displayName = order.receiverName ?: order.userFullName ?: "Khách lẻ"

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