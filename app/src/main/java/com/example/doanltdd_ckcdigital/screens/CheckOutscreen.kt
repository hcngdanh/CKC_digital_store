package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("COD") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(48.dp)) },
            title = { Text("Đặt hàng thành công!") },
            text = { Text("Cảm ơn bạn đã mua hàng tại CKC Digital. Chúng tôi sẽ liên hệ sớm nhất.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onOrderSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D1C))
                ) {
                    Text("Về trang chủ")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 16.dp, color = Color.White) {
                Button(
                    onClick = {
                        showSuccessDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp)
                        .navigationBarsPadding(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D1C)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("XÁC NHẬN ĐẶT HÀNG", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Thông tin giao hàng", icon = Icons.Default.LocationOn) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Địa chỉ nhận hàng") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SectionCard(title = "Phương thức thanh toán", icon = null) {
                PaymentOption(
                    selected = selectedPaymentMethod == "COD",
                    title = "Thanh toán khi nhận hàng (COD)",
                    onClick = { selectedPaymentMethod = "COD" }
                )
                PaymentOption(
                    selected = selectedPaymentMethod == "BANK",
                    title = "Chuyển khoản ngân hàng (QR Code)",
                    onClick = { selectedPaymentMethod = "BANK" }
                )
            }

            SectionCard(title = "Tóm tắt đơn hàng", icon = null) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tạm tính", color = Color.Gray)
                    Text("54.000.000đ", fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Phí vận chuyển", color = Color.Gray)
                    Text("Miễn phí", color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
                }
                Divider(Modifier.padding(vertical = 12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tổng cộng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("54.000.000đ", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFD32F2F))
                }
            }
        }
    }
}

@Composable
fun SectionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector?, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, null, tint = Color(0xFFFF4D1C), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
            content()
        }
    }
}

@Composable
fun PaymentOption(selected: Boolean, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF4D1C))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 14.sp)
    }
}
