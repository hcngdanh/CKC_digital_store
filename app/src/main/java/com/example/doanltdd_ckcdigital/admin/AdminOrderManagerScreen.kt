package com.example.doanltdd_ckcdigital.admin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doanltdd_ckcdigital.models.Order
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Enum để quản lý trạng thái đơn hàng một cách nhất quán.
 */
enum class OrderStatus(val apiName: String, val label: String, val color: Color, val description: String) {
    PENDING("pending", "Chờ xác nhận", Color(0xFFFF9800), "Đơn hàng mới tạo, chờ duyệt"),
    SHIPPING("shipping", "Đang giao", Color(0xFF2196F3), "Đang vận chuyển đến khách"),
    COMPLETED("completed", "Hoàn thành", Color(0xFF4CAF50), "Khách đã nhận và thanh toán"),
    CANCELLED("cancelled", "Đã hủy", Color(0xFFE91E63), "Đơn hàng bị hủy bỏ");

    companion object {
        fun fromString(status: String?): OrderStatus {
            return entries.find { it.apiName.equals(status, ignoreCase = true) } ?: PENDING
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderManagerScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val fetchOrders: () -> Unit = {
        scope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.apiService.getAllOrders()
                if (response.success) {
                    orders = response.data.sortedByDescending { it.orderId }
                } else {
                    Log.e("AdminOrder", "Lỗi lấy đơn hàng từ API: ${response.message}")
                    Toast.makeText(context, "Lỗi: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("AdminOrder", "Lỗi mạng: ${e.message}")
                Toast.makeText(context, "Lỗi mạng, không thể tải đơn hàng", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchOrders()
    }

    var selectedFilter by remember { mutableStateOf<OrderStatus?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedOrderForEdit by remember { mutableStateOf<Order?>(null) }

    val filteredList = remember(orders, selectedFilter) {
        if (selectedFilter == null) {
            orders
        } else {
            orders.filter { OrderStatus.fromString(it.status) == selectedFilter }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
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

            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("Tất cả") },
                        leadingIcon = if (selectedFilter == null) { { Icon(Icons.Default.FilterList, null, Modifier.size(16.dp)) } } else null
                    )
                }
                items(OrderStatus.entries.toTypedArray()) { status ->
                    FilterChip(
                        selected = selectedFilter == status,
                        onClick = { selectedFilter = status },
                        label = { Text(status.label) },
                        leadingIcon = if (selectedFilter == status) { { Icon(Icons.Default.FilterList, null, Modifier.size(16.dp)) } } else null
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có đơn hàng nào", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.orderId }) { order ->
                        AdminOrderCard(
                            order = order,
                            onEditStatus = {
                                selectedOrderForEdit = order
                                showBottomSheet = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet && selectedOrderForEdit != null) {
        OrderStatusBottomSheet(
            currentStatus = OrderStatus.fromString(selectedOrderForEdit!!.status),
            onDismiss = { showBottomSheet = false },
            onStatusSelected = { newStatus, cancelReason ->
                scope.launch {

                    val index = orders.indexOfFirst { it.orderId == selectedOrderForEdit!!.orderId }
                    if (index != -1) {
                        orders = orders.toMutableList().apply {
                            this[index] = this[index].copy(status = newStatus.apiName)
                        }
                        val message = if (newStatus == OrderStatus.CANCELLED) "Đã hủy đơn. Lý do: $cancelReason" else "Đã cập nhật: ${newStatus.label}"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
                showBottomSheet = false
            }
        )
    }
}

private fun formatDateString(isoDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(isoDate)
        date?.let { outputFormat.format(it) } ?: isoDate
    } catch (e: Exception) {
        isoDate
    }
}

@Composable
private fun AdminOrderCard(order: Order, onEditStatus: () -> Unit) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }
    val status = OrderStatus.fromString(order.status)
    val itemsSummary = remember(order.items) {
        order.items.joinToString(", ") { "${it.productName} (x${it.quantity})" }.ifEmpty { "Không có sản phẩm" }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable { /* TODO: Có thể thêm hành động xem chi tiết đơn hàng */ }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "ORD-${order.orderId}", fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = formatDateString(order.orderDate), fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Khách hàng: ", fontSize = 13.sp, color = Color.Gray)
                Text(order.userFullName, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = itemsSummary,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Tổng tiền", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = formatter.format(order.totalAmount),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F),
                        fontSize = 16.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = status.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = status.label,
                            color = status.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (status != OrderStatus.COMPLETED && status != OrderStatus.CANCELLED) {
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(
                            onClick = onEditStatus,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFF5F5F5), CircleShape)
                        ) {
                            Icon(Icons.Default.Edit, "Edit", modifier = Modifier.size(16.dp), tint = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderStatusBottomSheet(
    currentStatus: OrderStatus,
    onDismiss: () -> Unit,
    onStatusSelected: (newStatus: OrderStatus, cancelReason: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var tempStatus by remember { mutableStateOf(currentStatus) }
    var cancelReason by remember { mutableStateOf("") }
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Cập nhật trạng thái",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OrderStatus.entries.toTypedArray().forEach { status ->
                val isEnabled = status.ordinal > currentStatus.ordinal || status == OrderStatus.CANCELLED
                if (status == OrderStatus.PENDING && currentStatus != OrderStatus.PENDING) return@forEach

                val isSelected = tempStatus == status

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) status.color.copy(alpha = 0.1f) else Color.Transparent)
                        .clickable(enabled = isEnabled) { tempStatus = status }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { if (isEnabled) tempStatus = status },
                        enabled = isEnabled,
                        colors = RadioButtonDefaults.colors(selectedColor = status.color)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = status.label,
                            fontWeight = FontWeight.Bold,
                            color = if (isEnabled) Color.Black else Color.Gray.copy(alpha = 0.5f)
                        )
                        Text(
                            text = status.description,
                            fontSize = 12.sp,
                            color = if (isEnabled) Color.Gray else Color.LightGray.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            if (tempStatus == OrderStatus.CANCELLED) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Lý do hủy đơn", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = cancelReason,
                    onValueChange = { cancelReason = it },
                    placeholder = { Text("VD: Hết hàng, Khách hủy...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (tempStatus == OrderStatus.CANCELLED && cancelReason.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập lý do hủy", Toast.LENGTH_SHORT).show()
                    } else {
                        onStatusSelected(tempStatus, cancelReason)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("XÁC NHẬN", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
