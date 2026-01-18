package com.example.doanltdd_ckcdigital.ui.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.models.OrderInfoModel
import com.example.doanltdd_ckcdigital.models.OrderItemModel
import com.example.doanltdd_ckcdigital.viewmodels.AdminViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailScreen(
    orderId: Int,
    onBackClick: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current

    val orderDetail by viewModel.orderDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(OrderStatus.PENDING) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(orderId) {
        viewModel.fetchOrderDetail(orderId)
    }

    LaunchedEffect(orderDetail) {
        orderDetail?.orderInfo?.OrderStatus?.let {
            selectedStatus = OrderStatus.fromString(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.updateStatusEvent.collect { success ->
            if (success) {
                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                showBottomSheet = false
            } else {
                Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
            }
        }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (orderDetail != null) {
                val currentStatus = OrderStatus.fromString(orderDetail!!.orderInfo.OrderStatus)
                BottomActionArea(
                    currentStatus = currentStatus,
                    onUpdateClick = { showBottomSheet = true }
                )
            }
        }
    ) { padding ->
        if (isLoading && orderDetail == null) {
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatusInfoCard(currentStatusEnum)

                if (currentStatusEnum == OrderStatus.CANCELLED && !info.CancelReason.isNullOrEmpty()) {
                    CancelReasonCard(info.CancelReason)
                }

                OrderGeneralInfoCard(info)
                ProductListCard(items)
                PaymentSummaryCard(info, items)

                Spacer(Modifier.height(40.dp))
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            StatusUpdateContent(
                selectedStatus = selectedStatus,
                onStatusSelected = { selectedStatus = it },
                onConfirm = { viewModel.updateOrderStatus(orderId, selectedStatus.apiName) }
            )
        }
    }
}

@Composable
private fun BottomActionArea(
    currentStatus: OrderStatus,
    onUpdateClick: () -> Unit
) {
    if (currentStatus != OrderStatus.COMPLETED && currentStatus != OrderStatus.CANCELLED) {
        Surface(shadowElevation = 8.dp) {
            Button(
                onClick = onUpdateClick,
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
    } else {
        val (bgColor, textColor, text) = if (currentStatus == OrderStatus.COMPLETED) {
            Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Đơn hàng đã hoàn thành")
        } else {
            Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Đơn hàng đã bị hủy")
        }
        Surface(color = bgColor, modifier = Modifier.fillMaxWidth(), shadowElevation = 4.dp) {
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatusInfoCard(status: OrderStatus) {
    Card(
        colors = CardDefaults.cardColors(containerColor = status.color.copy(alpha = 0.1f)),
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
                text = status.label,
                color = status.color,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun CancelReasonCard(reason: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color(0xFFFFCDD2))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Lý do hủy đơn:", fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
            Spacer(Modifier.height(4.dp))
            Text(text = reason, fontStyle = FontStyle.Italic)
        }
    }
}

@Composable
private fun OrderGeneralInfoCard(info: OrderInfoModel) {
    Text("Thông tin đơn hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            InfoRow(Icons.Default.Person, info.ReceiverName ?: "Khách hàng")
            Spacer(Modifier.height(8.dp))
            InfoRow(Icons.Default.Phone, info.PhoneNumber ?: "Không có số điện thoại")
            Spacer(Modifier.height(8.dp))
            InfoRow(Icons.Default.Place, info.ShipAddress)

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            Spacer(Modifier.height(12.dp))

            InfoRow(Icons.Default.LocalShipping, "Vận chuyển: ${info.ShippingMethod ?: "Tiêu chuẩn"}", iconTint = Color(0xFF2196F3))
            Spacer(Modifier.height(8.dp))
            InfoRow(Icons.Default.Payment, "Thanh toán: ${info.PaymentMethod ?: "COD"}", iconTint = Color(0xFF4CAF50))
        }
    }
}

@Composable
private fun ProductListCard(items: List<OrderItemModel>) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }
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
}

@Composable
private fun PaymentSummaryCard(info: OrderInfoModel, items: List<OrderItemModel>) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }
    val productTotal = items.sumOf { it.UnitPrice * it.Quantity }
    val shippingCost = info.ShippingCost
    val rawDiscount = (productTotal + shippingCost) - info.TotalAmount
    val voucherDiscount = if (rawDiscount > 100) rawDiscount else 0.0

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Chi tiết thanh toán", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))

            SummaryRow("Tổng tiền hàng", formatter.format(productTotal))
            SummaryRow("Phí vận chuyển", formatter.format(shippingCost))

            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Voucher giảm giá", color = Color.Gray, fontSize = 14.sp)
                if (voucherDiscount > 0) {
                    Text("-${formatter.format(voucherDiscount)}", fontSize = 14.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                } else {
                    Text("0 đ", fontSize = 14.sp, color = Color.Black)
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color.LightGray)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tổng thanh toán", fontWeight = FontWeight.Bold)
                Text(formatter.format(info.TotalAmount), color = Color(0xFFFF5722), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun StatusUpdateContent(
    selectedStatus: OrderStatus,
    onStatusSelected: (OrderStatus) -> Unit,
    onConfirm: () -> Unit
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
                    .clickable { onStatusSelected(status) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onStatusSelected(status) },
                    colors = RadioButtonDefaults.colors(selectedColor = status.color)
                )
                Spacer(Modifier.width(8.dp))
                Text(status.label, fontWeight = FontWeight.Bold, color = status.color)
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("XÁC NHẬN")
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String, iconTint: Color = Color.Gray) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, fontWeight = if(iconTint != Color.Gray) FontWeight.Bold else FontWeight.Normal, color = Color.DarkGray)
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontSize = 14.sp, color = Color.Black)
    }
}