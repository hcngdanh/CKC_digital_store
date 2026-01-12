package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.NumberFormat
import java.util.Locale

val HistoryBgLight = Color(0xFFF5F5F5)
val HistoryCardLight = Color(0xFFFFFFFF)
val HistoryTextBlack = Color(0xFF212121)
val HistoryTextGrey = Color(0xFF757575)
val HistoryDividerColor = Color(0xFFEEEEEE)
val HistoryBrandOrange = Color(0xFFFF5722)

enum class HistoryStatus(val label: String, val color: Color) {
    ALL("Tất cả", Color.Black),
    PENDING("Chờ xác nhận", Color(0xFFFF9800)),
    SHIPPING("Đang giao", Color(0xFF2196F3)),
    COMPLETED("Hoàn thành", Color(0xFF4CAF50)),
    CANCELLED("Đã hủy", Color(0xFFE91E63))
}

data class OrderHistoryItem(
    val orderId: String,
    val shopName: String,
    val productName: String,
    val productImageUrl: String,
    val quantity: Int,
    val totalPrice: Long,
    val status: HistoryStatus
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    userId: Int,
    onBackClick: () -> Unit = {}
) {
    val sampleOrders = remember {
        listOf(
            OrderHistoryItem("ORD-001", "Sony Official Store", "Sony Alpha A7 IV Body Only", "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png", 1, 59990000, HistoryStatus.PENDING),
            OrderHistoryItem("ORD-002", "CKC Digital", "Fujifilm X-T5 Kit 18-55mm", "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png", 1, 43500000, HistoryStatus.SHIPPING),
            OrderHistoryItem("ORD-003", "Canon Camera", "Canon EOS R6 Mark II", "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png", 1, 67000000, HistoryStatus.COMPLETED),
            OrderHistoryItem("ORD-004", "Phụ Kiện Máy Ảnh", "Tripod Benro SystemGo", "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png", 2, 2500000, HistoryStatus.CANCELLED),
            OrderHistoryItem("ORD-005", "Sony Official Store", "Lens Sony FE 24-70mm GM II", "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png", 1, 48990000, HistoryStatus.COMPLETED)
        )
    }

    var selectedStatus by remember { mutableStateOf(HistoryStatus.ALL) }

    val filteredOrders = remember(selectedStatus, sampleOrders) {
        if (selectedStatus == HistoryStatus.ALL) sampleOrders
        else sampleOrders.filter { it.status == selectedStatus }
    }

    Scaffold(
        containerColor = HistoryBgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Lịch sử mua hàng",
                        color = HistoryTextBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = HistoryTextBlack)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = HistoryCardLight
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = HistoryStatus.values().indexOf(selectedStatus),
                containerColor = HistoryCardLight,
                contentColor = HistoryBrandOrange,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[HistoryStatus.values().indexOf(selectedStatus)]),
                        color = HistoryBrandOrange
                    )
                },
                divider = { HorizontalDivider(color = HistoryDividerColor) }
            ) {
                HistoryStatus.values().forEach { status ->
                    Tab(
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = status },
                        text = {
                            Text(
                                text = status.label,
                                fontSize = 14.sp,
                                fontWeight = if (selectedStatus == status) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedStatus == status) HistoryBrandOrange else HistoryTextGrey
                            )
                        }
                    )
                }
            }

            if (filteredOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có đơn hàng nào", color = HistoryTextGrey)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredOrders) { order ->
                        OrderHistoryCard(order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(order: OrderHistoryItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = HistoryCardLight),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mã đơn hàng: ${order.orderId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = HistoryTextBlack
                )

                Text(
                    text = order.status.label,
                    color = order.status.color,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = HistoryDividerColor)

            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = order.productImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.productName,
                        fontSize = 15.sp,
                        color = HistoryTextBlack,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "x${order.quantity}",
                        fontSize = 13.sp,
                        color = HistoryTextGrey
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        border = BorderStroke(1.dp, HistoryBrandOrange),
                        color = Color.Transparent,
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text(
                            text = "7 ngày trả hàng",
                            color = HistoryBrandOrange,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = HistoryDividerColor)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.quantity} sản phẩm",
                    color = HistoryTextGrey,
                    fontSize = 12.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Thành tiền: ", fontSize = 14.sp, color = HistoryTextBlack)
                    Text(
                        text = formatCurrencyHistory(order.totalPrice),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = HistoryBrandOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                when (order.status) {
                    HistoryStatus.COMPLETED -> {
                        OutlinedButton(
                            onClick = { },
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, HistoryTextGrey),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Đánh giá", color = HistoryTextBlack, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HistoryBrandOrange),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Mua lại", color = Color.White, fontSize = 12.sp)
                        }
                    }
                    HistoryStatus.CANCELLED -> {
                        Button(
                            onClick = { },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HistoryBrandOrange),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Mua lại", color = Color.White, fontSize = 12.sp)
                        }
                    }
                    else -> {
                        OutlinedButton(
                            onClick = { },
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, HistoryTextGrey),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Liên hệ Shop", color = HistoryTextBlack, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

fun formatCurrencyHistory(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(amount)
}

@Preview
@Composable
fun PreviewOrderHistory() {
    OrderHistoryScreen(userId = 1)
}