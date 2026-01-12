package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val ShopeeRed = Color(0xFFEE4D2D)
val BackgroundGray = Color(0xFFF5F5F5)
val TextGray = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tài khoản & Bảo mật", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ShopeeRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(BackgroundGray)
        ) {
            SectionHeader(text = "Tài Khoản")

            SettingsItem(title = "Hồ sơ của tôi")

            SettingsItem(title = "Tên người dùng", valueText = "trantuancuongdev", showArrow = false)

            SettingsItem(title = "Điện thoại", valueText = "*****56")
            SettingsItem(title = "Email nhận hóa đơn", valueText = "e*********5@gmail.com")
            SettingsItem(title = "Tài khoản mạng xã hội")
            SettingsItem(title = "Đổi mật khẩu")

            SettingsItem(
                title = "Passkey",
                valueText = "Thiết lập ngay bây giờ",
                valueColor = ShopeeRed
            )

            var fingerprintEnabled by remember { mutableStateOf(true) }
            SettingsSwitchItem(
                title = "Xác Thực Bằng Vân Tay",
                subtitle = "Hình ảnh Dấu Vân Tay có trên thiết bị của bạn, Shopee không lưu trữ thông tin đó",
                checked = fingerprintEnabled,
                onCheckedChange = { fingerprintEnabled = it }
            )

            var quickLoginEnabled by remember { mutableStateOf(false) }
            SettingsSwitchItem(
                title = "Đăng nhập nhanh",
                subtitle = "Cho phép đăng nhập nhanh trên thiết bị này: LGE",
                checked = quickLoginEnabled,
                onCheckedChange = { quickLoginEnabled = it }
            )

            SectionHeader(text = "Bảo Mật")

            SettingsItem(
                title = "Kiểm tra hoạt động của tài khoản",
                subtitle = "Kiểm tra những lần đăng nhập và thay đổi tài khoản trong 30 ngày gần nhất"
            )

            SettingsItem(
                title = "Quản lý thiết bị đăng nhập",
                subtitle = "Quản lý các thiết bị đã đăng nhập vào tài khoản Shopee",
                showRedDot = true
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
        color = TextGray,
        fontSize = 14.sp
    )
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    valueText: String? = null,
    valueColor: Color = Color.Black,
    showArrow: Boolean = true,
    showRedDot: Boolean = false
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .clickable {  }
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, color = Color.Black)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (valueText != null) {
                    Text(
                        text = valueText,
                        fontSize = 14.sp,
                        color = valueColor,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                if (showRedDot) {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(8.dp)
                            .background(ShopeeRed, shape = CircleShape)
                    )
                }

                if (showArrow) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }

        if (subtitle != null) {
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp, end = 24.dp),
                lineHeight = 16.sp
            )
        }
    }
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, color = Color.Black)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF26A69A),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp, end = 40.dp),
                lineHeight = 16.sp
            )
        }
    }
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
}