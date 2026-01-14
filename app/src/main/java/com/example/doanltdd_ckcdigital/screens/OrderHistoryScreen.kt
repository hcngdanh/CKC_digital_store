package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.models.OrderHistoryModel
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

// Màu sắc
val OrangeColor = Color(0xFFFF3C00)
val GreenColor = Color(0xFF4CAF50)
val BlueColor = Color(0xFF2196F3)
val GrayText = Color(0xFF757575)

enum class HistoryStatus(val label: String, val dbStatusList: List<String>) {
    ALL("Tất cả", emptyList()),
    CONFIRMING("Chờ xác nhận", listOf("Chờ xử lý")),
    PICKUP("Chờ lấy hàng", listOf("Chờ lấy hàng")),
    SHIPPING("Chờ giao hàng", listOf("Đang giao hàng")),
    COMPLETED("Hoàn thành", listOf("Hoàn thành")),
    CANCELLED("Đã hủy", listOf("Đã hủy"))
}

// Hàm format ngày giờ
fun formatDateTime(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""
    return try {
        // Giả sử server trả về format ISO 8601 (yyyy-MM-ddTHH:mm:ss.000Z)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000Z", Locale.getDefault())
        // Nếu server trả về format khác (ví dụ yyyy-MM-dd HH:mm:ss), hãy sửa dòng trên

        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN"))
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        // Fallback: Cắt chuỗi đơn giản nếu parse lỗi
        dateString.replace("T", " ").substringBefore(".")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    userId: Int,
    initialTab: HistoryStatus = HistoryStatus.ALL,
    onBackClick: () -> Unit,
    onOrderClick: (Int) -> Unit,
    onRateClick: (Int) -> Unit = {}
) {
    var orderList by remember { mutableStateOf<List<OrderHistoryModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(initialTab) }

    LaunchedEffect(userId) {
        try {
            isLoading = true
            val response = RetrofitClient.apiService.getUserOrders(userId)
            if (response.success) {
                orderList = response.data
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    val filteredOrders = remember(selectedTab, orderList) {
        if (selectedTab == HistoryStatus.ALL) orderList
        else orderList.filter { selectedTab.dbStatusList.contains(it.OrderStatus) }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lịch sử mua hàng", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            ScrollableTabRow(
                selectedTabIndex = HistoryStatus.values().indexOf(selectedTab),
                containerColor = Color.White,
                contentColor = OrangeColor,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[HistoryStatus.values().indexOf(selectedTab)]),
                        color = Color.Red
                    )
                }
            ) {
                HistoryStatus.values().forEach { status ->
                    Tab(
                        selected = selectedTab == status,
                        onClick = { selectedTab = status },
                        text = {
                            Text(
                                text = status.label,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == status) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == status) OrangeColor else GrayText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangeColor)
                }
            } else if (filteredOrders.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có đơn hàng nào", color = GrayText)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders) { order ->
                        OrderHistoryItemCard(
                            order = order,
                            onClick = { onOrderClick(order.OrderID) },
                            onRateClick = { onRateClick(order.OrderID) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryItemCard(
    order: OrderHistoryModel,
    onClick: () -> Unit,
    onRateClick: () -> Unit
) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    val statusColor = when (order.OrderStatus) {
        "Hoàn thành" -> GreenColor
        "Đã hủy" -> Color.Red
        "Đang giao hàng" -> BlueColor
        "Chờ lấy hàng" -> Color(0xFFFFA000)
        else -> OrangeColor
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(Modifier.padding(12.dp)) {
            // Header: Mã đơn + Ngày đặt + Trạng thái
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top // Căn lề trên cùng
            ) {
                Column {
                    Text("Mã đơn hàng: #${order.OrderID}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    // --- THÊM HIỂN THỊ NGÀY ĐẶT Ở ĐÂY ---
                    Text(
                        text = "Ngày đặt: ${formatDateTime(order.OrderDate.toString())}",
                        color = GrayText,
                        fontSize = 12.sp
                    )
                }

                // Trạng thái (Badge)
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        order.OrderStatus,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

            // Body
            Row {
                AsyncImage(
                    model = order.ThumbnailURL ?: "",
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).background(Color(0xFFFAFAFA)),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(order.ProductName ?: "Sản phẩm", maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 15.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("x${order.TotalQuantity}", color = GrayText, fontSize = 13.sp)

                    if (order.OrderStatus == "Hoàn thành" || order.OrderStatus == "Đang giao hàng") {
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            border = BorderStroke(1.dp, OrangeColor),
                            shape = RoundedCornerShape(2.dp),
                            color = Color.Transparent
                        ) {
                            Text(
                                "7 ngày trả hàng",
                                color = OrangeColor,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

            // Footer
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${order.TotalQuantity} sản phẩm", fontSize = 12.sp, color = GrayText)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Thành tiền: ", fontSize = 14.sp)
                    Text(
                        formatter.format(order.TotalAmount),
                        color = OrangeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // Nút đánh giá (Chỉ hiện khi Hoàn thành)
            if (order.OrderStatus == "Hoàn thành") {
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onRateClick,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeColor),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Outlined.RateReview, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Đánh giá", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}