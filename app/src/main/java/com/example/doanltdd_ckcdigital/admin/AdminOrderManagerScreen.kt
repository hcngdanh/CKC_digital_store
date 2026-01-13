package com.example.doanltdd_ckcdigital.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

enum class OrderStatus(val label: String, val color: Color, val description: String) {
    ALL("Tất cả", Color.Black, ""),
    PENDING("Chờ xác nhận", Color(0xFFFF9800), "Đơn hàng mới tạo, chờ duyệt"),
    SHIPPING("Đang giao", Color(0xFF2196F3), "Đang vận chuyển đến khách"),
    COMPLETED("Hoàn thành", Color(0xFF4CAF50), "Khách đã nhận và thanh toán"),
    CANCELLED("Đã hủy", Color(0xFFE91E63), "Đơn hàng bị hủy bỏ")
}

data class AdminOrder(
    val id: String,
    val customerName: String,
    val date: String,
    val total: Long,
    var status: OrderStatus,
    val itemsSummary: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderManagerScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val orders = remember {
        mutableStateListOf(
            AdminOrder("ORD-009", "Nguyễn Văn A", "2024-01-25", 15000000, OrderStatus.PENDING, "Sony A6400..."),
            AdminOrder("ORD-010", "Trần Thị B", "2024-01-24", 2500000, OrderStatus.SHIPPING, "Tripod Benro..."),
            AdminOrder("ORD-011", "Lê Văn C", "2024-01-23", 45000000, OrderStatus.COMPLETED, "Fujifilm X-T5..."),
            AdminOrder("ORD-012", "Phạm D", "2024-01-25", 890000, OrderStatus.PENDING, "Thẻ nhớ Sandisk..."),
            AdminOrder("ORD-013", "Hoàng E", "2024-01-20", 1200000, OrderStatus.CANCELLED, "Túi máy ảnh...")
        )
    }

    var selectedFilter by remember { mutableStateOf(OrderStatus.ALL) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedOrderForEdit by remember { mutableStateOf<AdminOrder?>(null) }

    val filteredList = if (selectedFilter == OrderStatus.ALL) {
        orders
    } else {
        orders.filter { it.status == selectedFilter }
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
                items(OrderStatus.values()) { status ->
                    FilterChip(
                        selected = selectedFilter == status,
                        onClick = { selectedFilter = status },
                        label = { Text(status.label) },
                        leadingIcon = if (selectedFilter == status) {
                            { Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE3F2FD),
                            selectedLabelColor = Color(0xFF1565C0)
                        )
                    )
                }
            }

            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có đơn hàng nào", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { order ->
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
            currentStatus = selectedOrderForEdit!!.status,
            onDismiss = { showBottomSheet = false },
            onStatusSelected = { newStatus, cancelReason ->
                val index = orders.indexOfFirst { it.id == selectedOrderForEdit!!.id }
                if (index != -1) {
                    orders[index] = orders[index].copy(status = newStatus)
                    if (newStatus == OrderStatus.CANCELLED) {
                        Toast.makeText(context, "Đã hủy đơn. Lý do: $cancelReason", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Đã cập nhật: ${newStatus.label}", Toast.LENGTH_SHORT).show()
                    }
                }
                showBottomSheet = false
            }
        )
    }
}

@Composable
fun AdminOrderCard(order: AdminOrder, onEditStatus: () -> Unit) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = order.id, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = order.date, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Khách hàng: ", fontSize = 13.sp, color = Color.Gray)
                Text(order.customerName, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = order.itemsSummary,
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
                        text = formatter.format(order.total),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F),
                        fontSize = 16.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = order.status.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = order.status.label,
                            color = order.status.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (order.status != OrderStatus.COMPLETED && order.status != OrderStatus.CANCELLED) {
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
fun OrderStatusBottomSheet(
    currentStatus: OrderStatus,
    onDismiss: () -> Unit,
    onStatusSelected: (OrderStatus, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
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
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Cập nhật trạng thái",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OrderStatus.values().filter { it != OrderStatus.ALL }.forEach { status ->
                val isEnabled = status.ordinal > currentStatus.ordinal || status == OrderStatus.CANCELLED
                val isSelected = tempStatus == status

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
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
                            color = if (isEnabled) Color.Black else Color.Gray
                        )
                        Text(
                            text = status.description,
                            fontSize = 12.sp,
                            color = if (isEnabled) Color.Gray else Color.LightGray
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
                    placeholder = { Text("Nhập lý do (VD: Hết hàng, Khách hủy...)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFAFAFA),
                        unfocusedContainerColor = Color(0xFFFAFAFA)
                    )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Xác nhận cập nhật", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}