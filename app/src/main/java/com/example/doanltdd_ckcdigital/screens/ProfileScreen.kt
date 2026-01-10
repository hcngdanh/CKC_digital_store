package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doanltdd_ckcdigital.models.UserModel

@Composable
fun ProfileScreen(
    user: UserModel?, // Nhận user từ AppNavGraph
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAddressManageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                Text(
                    text = "HỒ SƠ CÁ NHÂN",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Thông tin User
                if (user != null) {
                    Text(
                        text = user.FullName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = user.Email,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                } else {
                    Text("Chưa đăng nhập", fontSize = 18.sp, color = Color.Red)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Phần Đơn mua
                OrderSection()

                Spacer(modifier = Modifier.height(16.dp))

                // Nút Quản lý địa chỉ
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { onAddressManageClick() } // Gọi hàm chuyển màn hình
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFFFA500),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Quản lý địa chỉ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Các nút bấm dưới đáy
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("ĐĂNG XUẤT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("QUAY LẠI", color = Color.Black)
            }
        }
    }
}

@Composable
fun OrderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Đơn mua",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Xem lịch sử mua hàng",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.List,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp).padding(start = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OrderStatusItem(Icons.Outlined.Assignment, "Chờ xác nhận")
            OrderStatusItem(Icons.Outlined.Inventory2, "Chờ lấy hàng")
            OrderStatusItem(Icons.Outlined.LocalShipping, "Chờ giao hàng", 1)
            OrderStatusItem(Icons.Outlined.StarRate, "Đánh giá", 1)
        }
    }
}

@Composable
fun OrderStatusItem(icon: ImageVector, label: String, badgeCount: Int = 0) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp).clickable { }
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(32.dp), tint = Color(0xFF555555))
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier.offset(x = 6.dp, y = (-6).dp).size(18.dp).clip(CircleShape).background(Color.Red).border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = badgeCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, color = Color.Black, textAlign = TextAlign.Center, lineHeight = 14.sp)
    }
}