package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doanltdd_ckcdigital.viewmodels.SessionManager

// Thêm import cho UserModel
import com.example.doanltdd_ckcdigital.models.UserModel

@Composable
fun ProfileScreen(
    user: UserModel?,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Info - Hiển thị dữ liệu từ object user
            if (user != null) {
                // Sử dụng chính xác tên cột trong Database/Model của bạn (FullName, Email)
                Text(
                    text = user.FullName ?: "Người dùng",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.Email ?: "Không có email",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                // Hiển thị thêm số điện thoại nếu cần
                Text(
                    text = "SĐT: ${user.Phone ?: "Chưa cập nhật"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            } else {
                Text("Chưa đăng nhập", fontSize = 18.sp, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Logout Button
            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier.fillMaxWidth().height(50.dp)
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
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("QUAY LẠI", color = Color.Black)
            }
        }
    }
}