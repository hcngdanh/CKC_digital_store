package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doanltdd_ckcdigital.models.UserAddress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressListScreen(
    onBackClick: () -> Unit,
    onAddressSelected: (UserAddress) -> Unit,

) {
    val addressList = remember {
        mutableStateListOf(
            UserAddress(1, "Hồ Công Danh", "0866 551 849", "81/1 Đường số 18D, Gò Xoài, Phường Bình Hưng Hòa A, Quận Bình Tân, TP. Hồ Chí Minh", true),
            UserAddress(2, "Nguyễn Thị Thuỳ Trang", "0346 183 945", "Tổ 2, ấp Thanh Tân, xã Thanh Lương, Thị Xã Bình Long, tỉnh Bình Phước"),
            UserAddress(3, "Minh Chánh", "0869 532 615", "Số 608 Lô T, Đoàn Văn Bơ, Phường 9, Quận 4, TP. Hồ Chí Minh"),
            UserAddress(4, "Triệu Vy", "0347 993 070", "Ấp Bình Thới, Xã An Thạnh, Huyện Mỏ Cày Nam, Bến Tre"),
            UserAddress(5, "Bùi Phú Duy", "0563 795 999", "Ấp Đồng Tâm, xã Lộc Thịnh, Huyện Lộc Ninh, Bình Phước")
        )
    }

    var selectedId by remember { mutableIntStateOf(1) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chọn địa chỉ nhận hàng", fontSize = 18.sp) },
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
        ) {
            Text(
                text = "Địa chỉ của tôi",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray,
                fontSize = 14.sp
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                items(addressList, key = { it.id }) { address ->
                    AddressItem(
                        address = address,
                        isSelected = address.id == selectedId,
                        onSelect = {
                            selectedId = address.id
                            onAddressSelected(address)
                        },
                        onDelete = {
                            // Thực hiện xóa khỏi danh sách
                            addressList.remove(address)
                        }
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun AddressItem(
    address: UserAddress,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF4D1C))
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = address.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                VerticalDivider(modifier = Modifier.height(14.dp), color = Color.LightGray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(+84) ${address.phone.removePrefix("0")}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = address.details,
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )

            if (address.isDefault) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4D1C)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp),
                    color = Color.White
                ) {
                    Text(
                        text = "Mặc định",
                        color = Color(0xFFFF4D1C),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Cột chức năng: Sửa và Xóa
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Sửa",
                color = Color(0xFFFF4D1C),
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable {  } // Xử lý sự kiện khi ấn vào chữ Sửa
                    .padding(bottom = 8.dp)
            )

            // Nút Xóa địa chỉ
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}