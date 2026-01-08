package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.NumberFormat
import java.util.Locale

val OrangeColor = Color(0xFFEF6C00)
val CardBackgroundColor = Color(0xFFF5F5F5)

data class CameraItem(
    val name: String,
    val specs: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String
)

data class OrderDetail(
    val id: String,
    val status: String,
    val date: String,
    val items: List<CameraItem>,
    val shippingFee: Double,
    val total: Double
)

@Composable
fun OrderDetailScreen() {
    val sampleOrder = OrderDetail(
        id = "#ORD-2025-8899",
        status = "Đang vận chuyển",
        date = "02/01/2026",
        items = listOf(
            CameraItem(
                "Sony Alpha A7 IV",
                "Body Only - 33MP",
                59990000.0,
                1,
                "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png"
            ),
            CameraItem(
                "Lens FE 24-70mm GM",
                "f/2.8 GM II",
                48990000.0,
                1,
                "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png"
            )
        ),
        shippingFee = 50000.0,
        total = 109030000.0
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            OrderDetailTopBar()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OrderStatusSection(sampleOrder)
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Danh sách sản phẩm",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                sampleOrder.items.forEach { item ->
                    ProductItemRow(item)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
                ShippingInfoSection()
                Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
                PriceSummarySection(sampleOrder)

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { /* */ },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Mua lại đơn này", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailTopBar() {
    TopAppBar(
        title = { Text("Chi tiết đơn hàng", color = Color.Black, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = { /* Back */ }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun OrderStatusSection(order: OrderDetail) {
    Column {
        Text(text = "Mã đơn: ${order.id}", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = order.status, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "• ${order.date}", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun ProductItemRow(item: CameraItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, color = Color.Black, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(text = item.specs, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "x${item.quantity}", color = Color.Black, fontSize = 12.sp)
        }

        Text(
            text = formatCurrency(item.price),
            color = OrangeColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ShippingInfoSection() {
    Column {
        Text("Địa chỉ nhận hàng", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Trần Tuấn Cường", color = Color.Black, fontWeight = FontWeight.Medium)
                Text("0909 xxx xxx", color = Color.Gray, fontSize = 14.sp)
                Text("123 Đường ABC, Quận 1, TP.HCM", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun PriceSummarySection(order: OrderDetail) {
    Column {
        PriceRow("Tạm tính", order.items.sumOf { it.price * it.quantity })
        PriceRow("Phí vận chuyển", order.shippingFee)
        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tổng cộng", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                formatCurrency(order.total),
                color = OrangeColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun PriceRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(formatCurrency(amount), color = Color.Black, fontSize = 14.sp)
    }
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(amount)
}

@Preview
@Composable
fun PreviewOrderDetail() {
    OrderDetailScreen()
}