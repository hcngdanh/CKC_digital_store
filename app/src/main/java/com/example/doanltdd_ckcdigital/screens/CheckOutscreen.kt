package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.models.ProductModel
import com.example.doanltdd_ckcdigital.models.UserAddress
import com.example.doanltdd_ckcdigital.models.UserModel
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import com.example.doanltdd_ckcdigital.utils.CartItem
import com.example.doanltdd_ckcdigital.utils.CartManager
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    user: UserModel?,
    onBackClick: () -> Unit,
    selectedAddress: UserAddress?,
    onAddressClick: () -> Unit,
    buyNowProductId: Int,
    onOrderSuccess: () -> Unit
) {
    var buyNowProduct by remember { mutableStateOf<ProductModel?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf("COD") }
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }
    var selectedVoucher by remember { mutableStateOf<String?>(null) }
    var discountAmount by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(buyNowProductId) {
        if (buyNowProductId != -1) {
            try {
                val response = RetrofitClient.apiService.getProductDetail(buyNowProductId)
                if (response.success) {
                    buyNowProduct = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val displayItems = if (buyNowProductId != -1) {
        buyNowProduct?.let { product ->
            listOf(
                CartItem(
                    CartItemID = 0,
                    ProductID = product.ProductID,
                    ProductName = product.ProductName,
                    Price = product.Price,
                    ThumbnailURL = product.ThumbnailURL,
                    quantity = 1
                )
            )
        } ?: emptyList()
    } else {
        CartManager.cartItems
    }

    val totalPrice = displayItems.sumOf { it.Price * it.quantity }
    val finalPrice = totalPrice - discountAmount

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thanh toán", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                onClick = onAddressClick,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFFF4D1C),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        val displayName =
                            selectedAddress?.ReceiverName ?: user?.FullName ?: "Chọn địa chỉ"
                        val displayPhone = selectedAddress?.PhoneNumber ?: user?.Phone ?: ""
                        val displayAddressText = if (selectedAddress != null) {
                            "${selectedAddress.StreetAddress}, ${selectedAddress.City}"
                        } else {
                            "Vui lòng chọn địa chỉ nhận hàng"
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (displayPhone.isNotEmpty()) {
                                Text(
                                    text = "(+84) ${displayPhone.removePrefix("0")}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = displayAddressText,
                            fontSize = 14.sp,
                            color = if (selectedAddress != null) Color.DarkGray else ShopeeRed,
                            lineHeight = 20.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Sản phẩm đã chọn", fontWeight = FontWeight.Bold)
                    }
                    displayItems.forEach { item ->
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = item.ThumbnailURL,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp)
                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Fit
                            )
                            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(
                                    item.ProductName,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${formatter.format(item.Price)} x ${item.quantity}",
                                    color = Color.Gray, fontSize = 13.sp
                                )
                            }
                        }
                        HorizontalDivider(color = Color(0xFFF1F1F1))
                    }
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.clickable {
                    if (selectedVoucher == null) {
                        selectedVoucher = "GIAM50K"
                        discountAmount = 50000.0
                    } else {
                        selectedVoucher = null
                        discountAmount = 0.0
                    }
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = Color(0xFFFF4D1C),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Shopee Voucher", fontWeight = FontWeight.Bold)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (selectedVoucher != null) {
                            Text(
                                text = "- ${formatter.format(discountAmount)}",
                                color = Color(0xFFFF4D1C),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "Chọn hoặc nhập mã",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Payment,
                            null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Phương thức thanh toán", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    val paymentMethods = listOf(
                        "COD" to "Thanh toán khi nhận hàng (COD)",
                        "BANK" to "Chuyển khoản ngân hàng (QR Code)",
                        "MOMO" to "Ví MoMo",
                        "CARD" to "Visa / Mastercard"
                    )
                    paymentMethods.forEach { (id, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = id }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedPaymentMethod == id),
                                onClick = { selectedPaymentMethod = id },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF4D1C))
                            )
                            Text(label, fontSize = 14.sp)
                        }
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Tóm tắt đơn hàng", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tạm tính", color = Color.Gray, fontSize = 14.sp)
                        Text(formatter.format(totalPrice))
                    }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Phí vận chuyển", color = Color.Gray, fontSize = 14.sp)
                        Text("Miễn phí", color = Color(0xFF4CAF50))
                    }

                    if (discountAmount > 0) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Voucher giảm giá", color = Color.Gray, fontSize = 14.sp)
                            Text("-${formatter.format(discountAmount)}", color = Color(0xFFFF4D1C))
                        }
                    }

                    HorizontalDivider(Modifier.padding(vertical = 12.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tổng cộng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = formatter.format(finalPrice),
                            color = Color(0xFFFF4D1C),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }

            Button(
                onClick = onOrderSuccess,
                modifier = Modifier.fillMaxWidth().height(54.dp).padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D1C)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("XÁC NHẬN ĐẶT HÀNG", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}