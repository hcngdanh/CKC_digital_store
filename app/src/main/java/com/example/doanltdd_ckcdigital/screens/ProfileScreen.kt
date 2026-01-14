package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.models.UserModel
import com.example.doanltdd_ckcdigital.services.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserModel?,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAddressManageClick: () -> Unit,
    // --- CẬP NHẬT: onOrderHistoryClick nhận thêm tham số String (Status) ---
    onOrderHistoryClick: (String) -> Unit,
    onEditProfileClick: () -> Unit,
    onPasswordChangeClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    var orderStats by remember { mutableStateOf(mapOf<String, Int>()) }

    LaunchedEffect(user) {
        if (user != null) {
            try {
                val response = RetrofitClient.apiService.getUserOrders(user.UserID)
                if (response.success) {
                    orderStats = response.data.groupingBy { it.OrderStatus }.eachCount()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            Surface(color = Color.Black) {
                CenterAlignedTopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Black
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                        }
                    },
                    title = {
                        AsyncImage(
                            model = "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png",
                            contentDescription = "Logo",
                            modifier = Modifier.height(30.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                )
            }
        },
        containerColor = Color(0xFF686868)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Avatar
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.White)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!user?.AvatarURL.isNullOrEmpty()) {
                        AsyncImage(
                            model = user.AvatarURL,
                            contentDescription = "User Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = Color.Black)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (user != null) {
                    Text(user.FullName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(user.Email, fontSize = 14.sp, color = Color.LightGray)
                }
            }

            // Body Menu
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF8F9FA),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp).fillMaxWidth()) {
                    ProfileMenuItem(Icons.Default.Edit, "Chỉnh sửa thông tin cá nhân", Color.Black) { onEditProfileClick() }
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileMenuItem(Icons.Default.Lock, "Thay đổi mật khẩu", Color.Black) { onPasswordChangeClick() }
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- TRUYỀN HÀM CHUYỂN TRANG ---
                    OrderSection(orderStats = orderStats, onHistoryClick = onOrderHistoryClick)

                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileMenuItem(Icons.Default.LocationOn, "Quản lý địa chỉ", Color(0xFFF44336)) { onAddressManageClick() }
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileMenuItem(Icons.Default.Favorite, "Danh sách yêu thích", Color(0xFFE91E63)) { onFavoriteClick() }
                    Spacer(modifier = Modifier.height(40.dp))
                    Button(
                        onClick = onLogoutClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000000)),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.ExitToApp, null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("ĐĂNG XUẤT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, iconColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).clickable { onClick() }.padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333), modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Outlined.ArrowForwardIos, null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
    }
}

@Composable
fun OrderSection(
    orderStats: Map<String, Int>,
    onHistoryClick: (String) -> Unit // --- CẬP NHẬT THAM SỐ ---
) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onHistoryClick("ALL") }.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Đơn mua", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Lịch sử mua hàng", fontSize = 12.sp, color = Color.Gray)
                Icon(Icons.AutoMirrored.Outlined.List, null, tint = Color.Gray, modifier = Modifier.size(16.dp).padding(start = 4.dp))
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color(0xFFF0F0F0))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            // --- GỌI HÀM VỚI ĐÚNG MÃ TRẠNG THÁI (Khớp với Enum HistoryStatus) ---
            OrderStatusItem(Icons.Outlined.Assignment, "Chờ xác nhận", orderStats["Chờ xử lý"] ?: 0) { onHistoryClick("CONFIRMING") }
            OrderStatusItem(Icons.Outlined.Inventory2, "Chờ lấy hàng", orderStats["Chờ lấy hàng"] ?: 0) { onHistoryClick("PICKUP") }
            OrderStatusItem(Icons.Outlined.LocalShipping, "Chờ giao hàng", orderStats["Đang giao hàng"] ?: 0) { onHistoryClick("SHIPPING") }
            OrderStatusItem(Icons.Outlined.StarRate, "Đánh giá", orderStats["Hoàn thành"] ?: 0) { onHistoryClick("COMPLETED") }
        }
    }
}

@Composable
fun OrderStatusItem(icon: ImageVector, label: String, badgeCount: Int = 0, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(75.dp).clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(icon, null, modifier = Modifier.size(28.dp), tint = Color(0xFF555555))
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier.offset(x = 6.dp, y = (-6).dp).size(18.dp).clip(CircleShape).background(Color.Red).border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (badgeCount > 99) "99+" else badgeCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 13.sp)
    }
}