package com.example.doanltdd_ckcdigital.ui.order

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.models.OrderHistoryModel
import com.example.doanltdd_ckcdigital.ui.product.WriteReviewDialog
import com.example.doanltdd_ckcdigital.utils.SessionManager
import com.example.doanltdd_ckcdigital.viewmodels.OrderViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

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

fun formatDateTime(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000Z", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN"))
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString.replace("T", " ").substringBefore(".")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    userId: Int,
    initialTab: HistoryStatus = HistoryStatus.ALL,
    onBackClick: () -> Unit,
    onOrderClick: (Int, String) -> Unit,
    viewModel: OrderViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val currentUser = sessionManager.currentUser

    val orderList by viewModel.orderHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf(initialTab) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var selectedOrderForReview by remember { mutableStateOf<OrderHistoryModel?>(null) }

    LaunchedEffect(userId) {
        viewModel.fetchOrderHistory(userId)
    }

    LaunchedEffect(initialTab) {
        selectedTab = initialTab
    }

    LaunchedEffect(Unit) {
        viewModel.reviewEvent.collect { result ->
            when (result) {
                is OrderViewModel.ReviewResult.Success -> {
                    Toast.makeText(context, "Đánh giá thành công!", Toast.LENGTH_SHORT).show()
                    showReviewDialog = false
                    selectedOrderForReview = null
                }
                is OrderViewModel.ReviewResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val filteredOrders = remember(selectedTab, orderList) {
        if (selectedTab == HistoryStatus.ALL) orderList
        else orderList.filter { selectedTab.dbStatusList.contains(it.OrderStatus) }
    }

    if (showReviewDialog && selectedOrderForReview != null) {
        WriteReviewDialog(
            productName = selectedOrderForReview!!.ProductName ?: "Sản phẩm",
            userAvatar = currentUser?.AvatarURL,
            userName = currentUser?.FullName ?: "Bạn",
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment ->
                currentUser?.let { user ->
                    viewModel.submitReview(
                        userId = user.UserID,
                        productId = selectedOrderForReview!!.ProductID,
                        orderId = selectedOrderForReview!!.OrderID,
                        rating = rating,
                        comment = comment
                    )
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Lịch sử mua hàng",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            ScrollableTabRow(
                selectedTabIndex = HistoryStatus.entries.indexOf(selectedTab),
                containerColor = Color.Black,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[HistoryStatus.entries.indexOf(selectedTab)]),
                        color = Color.Red
                    )
                },
                divider = {}
            ) {
                HistoryStatus.entries.forEach { status ->
                    Tab(
                        selected = selectedTab == status,
                        onClick = { selectedTab = status },
                        text = {
                            Text(
                                text = status.label,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == status) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == status) Color.White else Color.LightGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }

            if (isLoading && orderList.isEmpty()) {
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
                            onClick = { onOrderClick(order.OrderID, selectedTab.name) },
                            onRateClick = {
                                selectedOrderForReview = order
                                showReviewDialog = true
                            }
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
    val isCompleted = order.OrderStatus == "Hoàn thành"

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
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("Mã đơn hàng: #${order.OrderID}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Ngày: ${formatDateTime(order.OrderDate)}", color = GrayText, fontSize = 12.sp)
                }
                Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                    Text(order.OrderStatus, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

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
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${order.TotalQuantity} sản phẩm", fontSize = 12.sp, color = GrayText)
                Text("Thành tiền: ${formatter.format(order.TotalAmount)}", color = OrangeColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            if (isCompleted) {
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    val isReviewed = order.IsReviewed == 1

                    Button(
                        onClick = { if (!isReviewed) onRateClick() },
                        enabled = !isReviewed,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isReviewed) Color.Gray else OrangeColor,
                            disabledContainerColor = Color.LightGray
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RateReview,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isReviewed) "Đã đánh giá" else "Đánh giá",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}