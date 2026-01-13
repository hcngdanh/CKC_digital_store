package com.example.doanltdd_ckcdigital.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.models.*
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import com.example.doanltdd_ckcdigital.utils.CartItem
import com.example.doanltdd_ckcdigital.utils.CartManager
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// Định nghĩa màu sắc thương hiệu CKC Digital
val CKCRed = Color(0xFFD32F2F)
val CKCBlack = Color.Black
val CKCBackground = Color(0xFFF5F5F5)

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    // --- STATES QUẢN LÝ DỮ LIỆU ---
    var buyNowProduct by remember { mutableStateOf<ProductModel?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf("COD") }
    var isProcessing by remember { mutableStateOf(false) }

    // Khuyến mãi
    var selectedPromotion by remember { mutableStateOf<PromotionModel?>(null) }
    var showVoucherSheet by remember { mutableStateOf(false) }
    val promotionList = remember { mutableStateListOf<PromotionModel>() }

    // Vận chuyển
    var selectedShippingMethod by remember { mutableStateOf<ShippingMethod?>(null) }
    val shippingMethods = remember { mutableStateListOf<ShippingMethod>() }

    // --- LOAD DỮ LIỆU TỪ API ---
    LaunchedEffect(Unit) {
        try {
            // 1. Lấy danh sách Voucher
            val promoRes = RetrofitClient.apiService.getPromotions()
            if (promoRes.success) promotionList.addAll(promoRes.data)

            // 2. Lấy danh sách Vận chuyển
            val shipRes = RetrofitClient.apiService.getShippingMethods()
            if (shipRes.success) {
                shippingMethods.addAll(shipRes.data)
                // Mặc định chọn phương thức đầu tiên (thường là rẻ nhất)
                if (shippingMethods.isNotEmpty()) selectedShippingMethod = shippingMethods[0]
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Load sản phẩm nếu là "Mua ngay"
    LaunchedEffect(buyNowProductId) {
        if (buyNowProductId != -1) {
            val res = RetrofitClient.apiService.getProductDetail(buyNowProductId)
            if (res.success) buyNowProduct = res.data
        }
    }

    // --- TÍNH TOÁN TIỀN ---
    val displayItems = if (buyNowProductId != -1) {
        buyNowProduct?.let { p ->
            listOf(CartItem(0, p.ProductID, p.ProductName, p.Price, p.ThumbnailURL, 1))
        } ?: emptyList()
    } else {
        CartManager.cartItems
    }

    val itemTotal = displayItems.sumOf { it.Price * it.quantity }
    val shippingCost = selectedShippingMethod?.Cost ?: 0.0

    // Tính giảm giá
    val discountAmount = if (selectedPromotion != null) {
        if (itemTotal >= selectedPromotion!!.MinOrderValue) {
            if (selectedPromotion!!.DiscountType == "PERCENT") {
                itemTotal * (selectedPromotion!!.DiscountValue / 100)
            } else {
                selectedPromotion!!.DiscountValue
            }
        } else 0.0
    } else 0.0

    val finalPrice = (itemTotal + shippingCost - discountAmount).coerceAtLeast(0.0)

    // --- XỬ LÝ ĐẶT HÀNG ---
    fun handlePlaceOrder() {
        if (selectedAddress == null) {
            Toast.makeText(context, "Vui lòng chọn địa chỉ nhận hàng", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedShippingMethod == null) {
            Toast.makeText(context, "Vui lòng chọn đơn vị vận chuyển", Toast.LENGTH_SHORT).show()
            return
        }
        if (user == null) return

        isProcessing = true
        scope.launch {
            try {
                // Map Payment Method
                val paymentId = when (selectedPaymentMethod) {
                    "COD" -> 1
                    "BANK" -> 2
                    "MOMO" -> 3
                    else -> 1
                }

                // Map Items
                val orderDetails = displayItems.map { item ->
                    OrderDetailRequest(item.ProductID, item.quantity, item.Price)
                }

                // Tạo Request
                val request = OrderRequest(
                    userId = user.UserID,
                    totalAmount = finalPrice,
                    shipAddress = "${selectedAddress.StreetAddress}, ${selectedAddress.City}",
                    paymentMethodId = paymentId,
                    shippingMethodId = selectedShippingMethod!!.ShippingMethodID,
                    items = orderDetails
                )

                // Gọi API
                val response = RetrofitClient.apiService.createOrder(request)
                if (response.success) {
                    Toast.makeText(context, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show()
                    if (buyNowProductId == -1) CartManager.clearCart()
                    onOrderSuccess()
                } else {
                    Toast.makeText(context, "Lỗi: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isProcessing = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CKCBlack)
            )
        },
        bottomBar = {
            // Thanh thanh toán cố định ở dưới
            Surface(
                shadowElevation = 16.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tổng thanh toán", fontSize = 14.sp, color = Color.Gray)
                        Text(
                            text = formatter.format(finalPrice),
                            color = CKCRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Button(
                        onClick = { handlePlaceOrder() },
                        enabled = !isProcessing,
                        colors = ButtonDefaults.buttonColors(containerColor = CKCRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .padding(start = 16.dp)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("ĐẶT HÀNG", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CKCBackground)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. THẺ ĐỊA CHỈ
            SectionCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAddressClick() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = CKCRed, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        if (selectedAddress != null) {
                            Text(
                                text = "${selectedAddress.ReceiverName} | ${selectedAddress.PhoneNumber}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${selectedAddress.StreetAddress}, ${selectedAddress.City}",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text("Vui lòng chọn địa chỉ nhận hàng", color = CKCRed, fontWeight = FontWeight.Medium)
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray)
                }
            }

            // 2. DANH SÁCH SẢN PHẨM
            SectionCard {
                Column(Modifier.padding(16.dp)) {
                    Text("Sản phẩm đã chọn", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))
                    displayItems.forEach { item ->
                        Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = item.ThumbnailURL,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(item.ProductName, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Medium)
                                Spacer(Modifier.height(4.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(formatter.format(item.Price), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("x${item.quantity}", fontSize = 14.sp, color = Color.Gray)
                                }
                            }
                        }
                        if (item != displayItems.last()) {
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                        }
                    }
                }
            }

            // 3. PHƯƠNG THỨC VẬN CHUYỂN (MỚI)
            SectionCard {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalShipping, null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Phương thức vận chuyển", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(12.dp))

                    if (shippingMethods.isEmpty()) {
                        Text("Đang tải phương thức vận chuyển...", color = Color.Gray, fontSize = 13.sp)
                    } else {
                        shippingMethods.forEach { method ->
                            val isSelected = selectedShippingMethod?.ShippingMethodID == method.ShippingMethodID
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedShippingMethod = method }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedShippingMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = CKCRed)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(method.MethodName, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                                    Text(
                                        "Nhận hàng: ${method.EstimatedDelivery}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    formatter.format(method.Cost),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if(isSelected) CKCRed else Color.Black
                                )
                            }
                        }
                    }
                }
            }

            // 4. VOUCHER (SHOPEE VOUCHER STYLE)
            SectionCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showVoucherSheet = true }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ConfirmationNumber, null, tint = CKCRed, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("CKC Voucher", fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (selectedPromotion != null) "-${formatter.format(discountAmount)}" else "Chọn mã",
                            color = CKCRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray)
                    }
                }
            }

            // 5. PHƯƠNG THỨC THANH TOÁN
            SectionCard {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Phương thức thanh toán", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    listOf("COD" to "Thanh toán khi nhận hàng", "BANK" to "Chuyển khoản ngân hàng", "MOMO" to "Ví MoMo").forEach { (id, label) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = id }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedPaymentMethod == id),
                                onClick = { selectedPaymentMethod = id },
                                colors = RadioButtonDefaults.colors(selectedColor = CKCRed)
                            )
                            Text(label, fontSize = 14.sp)
                        }
                    }
                }
            }

            // 6. CHI TIẾT THANH TOÁN
            SectionCard {
                Column(Modifier.padding(16.dp)) {
                    Text("Chi tiết thanh toán", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))
                    RowSummary("Tổng tiền hàng", itemTotal, formatter)
                    RowSummary("Phí vận chuyển", shippingCost, formatter)
                    if (discountAmount > 0) {
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tổng giảm giá", fontSize = 14.sp, color = Color.Gray)
                            Text("-${formatter.format(discountAmount)}", fontSize = 14.sp, color = CKCRed)
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tổng thanh toán", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(formatter.format(finalPrice), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CKCRed)
                    }
                }
            }
        }

        // --- BOTTOM SHEET VOUCHER ---
        if (showVoucherSheet) {
            ModalBottomSheet(
                onDismissRequest = { showVoucherSheet = false },
                containerColor = Color.White
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text("Chọn Mã Giảm Giá", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(Modifier.height(16.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
                        items(promotionList) { promo ->
                            val isSelected = selectedPromotion?.PromotionID == promo.PromotionID
                            val discountText = if (promo.DiscountType == "PERCENT") "${promo.DiscountValue.toInt()}%" else formatter.format(promo.DiscountValue)

                            Card(
                                onClick = { selectedPromotion = if (isSelected) null else promo; showVoucherSheet = false },
                                colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFFFF0E6) else Color(0xFFF5F5F5)),
                                border = if (isSelected) BorderStroke(1.dp, CKCRed) else null
                            ) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(promo.Code, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text("Giảm $discountText", color = CKCRed, fontWeight = FontWeight.Medium)
                                        Text(promo.Description, fontSize = 12.sp, color = Color.Gray)
                                    }
                                    if (isSelected) Icon(Icons.Default.ConfirmationNumber, null, tint = CKCRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- HELPER COMPOSABLES ---

@Composable
fun SectionCard(content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp), // Phẳng, hiện đại
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        content()
    }
}

@Composable
fun RowSummary(label: String, value: Double, formatter: NumberFormat) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(formatter.format(value), fontSize = 14.sp, color = CKCBlack)
    }
}