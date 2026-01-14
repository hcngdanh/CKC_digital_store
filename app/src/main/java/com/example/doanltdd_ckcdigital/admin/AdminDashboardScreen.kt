package com.example.doanltdd_ckcdigital.admin

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doanltdd_ckcdigital.models.UserModel
import com.example.doanltdd_ckcdigital.models.DashboardStatsResponse
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class AdminFeature(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

data class DashboardStat(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    user: UserModel,
    onLogout: () -> Unit,
    onNavigateToOrderManager: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var dashboardStats by remember { mutableStateOf<DashboardStatsResponse?>(null) }
    var isLoadingStats by remember { mutableStateOf(true) }
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    LaunchedEffect(Unit) {
        scope.launch {
            isLoadingStats = true
            try {
                val response = RetrofitClient.apiService.getDashboardStats()

                if (response.success) {
                    dashboardStats = response.data
                } else {
                    Log.e("AdminDashboard", "Lỗi lấy thống kê: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("AdminDashboard", "Lỗi mạng: ${e.message}")
            } finally {
                isLoadingStats = false
            }
        }
    }

    val features = listOf(
        AdminFeature(
            "Quản lý Đơn hàng",
            "Cập nhật trạng thái đơn hàng",
            Icons.Default.ShoppingCart,
            Color(0xFF2196F3),
            onNavigateToOrderManager
        )
    )

    // Tạo danh sách thống kê động từ state
    val stats = remember(dashboardStats) {
        dashboardStats?.let {
            listOf(
                DashboardStat(
                    "Doanh thu ngày",
                    formatter.format(it.dailyRevenue),
                    Icons.Default.AttachMoney,
                    Color(0xFF4CAF50)
                ),
                DashboardStat(
                    "Chờ xử lý",
                    it.pendingOrders.toString(),
                    Icons.Default.PendingActions,
                    Color(0xFFFF9800)
                ),
                DashboardStat(
                    "Đã hoàn thành",
                    it.completedOrdersToday.toString(),
                    Icons.Default.CheckCircle,
                    Color(0xFF2196F3)
                )
            )
        } ?: listOf( // Dữ liệu mặc định hoặc khi đang tải
            DashboardStat("Doanh thu ngày", "...", Icons.Default.AttachMoney, Color(0xFF4CAF50)),
            DashboardStat("Chờ xử lý", "...", Icons.Default.PendingActions, Color(0xFFFF9800)),
            DashboardStat("Đã hoàn thành", "...", Icons.Default.CheckCircle, Color(0xFF2196F3))
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Dashboard", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Quản trị viên", fontSize = 12.sp, fontWeight = FontWeight.Normal, color = Color.White.copy(alpha = 0.7f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E2E),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Đăng xuất")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            AdminHeaderCardUpdated(user)
            Spacer(modifier = Modifier.height(20.dp))

            Text("Tổng quan hôm nay", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoadingStats) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    stats.forEach { stat ->
                        StatCard(stat.copy(value = "..."))
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(stats) { stat ->
                        StatCard(stat)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Chức năng quản lý", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(features) { feature ->
                    AdminFeatureCardUpdated(feature)
                }
            }
        }
    }
}

@Composable
fun AdminHeaderCardUpdated(user: UserModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.FullName.take(1).uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.FullName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Text(text = user.Email, fontSize = 12.sp, color = Color.Gray)
            }
            Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                Text("ADMIN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
fun StatCard(stat: DashboardStat) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(150.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(stat.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = stat.icon, contentDescription = null, tint = stat.color, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(stat.title, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(stat.value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun AdminFeatureCardUpdated(feature: AdminFeature) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { feature.onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(feature.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = feature.icon, contentDescription = null, tint = feature.color, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = feature.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                Text(text = feature.subtitle, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.Gray)
            }

            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = Color.LightGray)
        }
    }
}
