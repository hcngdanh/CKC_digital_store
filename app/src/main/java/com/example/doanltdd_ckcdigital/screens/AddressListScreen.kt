package com.example.doanltdd_ckcdigital.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doanltdd_ckcdigital.models.UserAddress
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressListScreen(
    userId: Int,
    currentSelectedId: Int?,
    onBackClick: () -> Unit,
    onAddressSelected: (UserAddress) -> Unit,
    onEditClick: (Int) -> Unit,
    onAddNewAddressClick: () -> Unit
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val addressList = remember { mutableStateListOf<UserAddress>() }
    var isLoading by remember { mutableStateOf(true) }
    var selectedId by remember { mutableIntStateOf(-1) }

    LaunchedEffect(userId) {
        try {
            isLoading = true
            val dbData = RetrofitClient.apiService.getUserAddresses(userId)

            val sortedDbData = if (currentSelectedId != null) {
                dbData.sortedByDescending { it.AddressID == currentSelectedId }
            } else {
                dbData
            }

            addressList.clear()
            addressList.addAll(sortedDbData)

            selectedId = if (currentSelectedId != null && currentSelectedId != -1) {
                currentSelectedId
            } else {
                dbData.find { it.IsDefault == 1 }?.AddressID ?: -1
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi tải địa chỉ: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace() 
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Địa chỉ của tôi", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNewAddressClick,
                containerColor = Color(0xFF000000),
                contentColor = Color.White,
                shape = RoundedCornerShape(30.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Thêm địa chỉ mới", fontWeight = FontWeight.Bold) }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            Text(
                text = "Địa chỉ nhận hàng",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray,
                fontSize = 14.sp
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF4D1C))
                }
            } else if (addressList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Bạn chưa có địa chỉ nào", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    items(addressList, key = { it.AddressID }) { address ->
                        AddressItem(
                            address = address,
                            isSelected = address.AddressID == selectedId,
                            onSelect = {
                                selectedId = address.AddressID
                                onAddressSelected(address)
                            },
                            onDelete = {
                                scope.launch {
                                    try {
                                        val res = RetrofitClient.apiService.deleteAddress(address.AddressID)
                                        if (res.success) {
                                            addressList.remove(address)
                                            Toast.makeText(context, "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Không thể xóa", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onEdit = { onEditClick(address.AddressID) }
                        )
                        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                    }
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
    onEdit: (Int) -> Unit
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
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF000000))
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = address.ReceiverName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                VerticalDivider(modifier = Modifier.height(14.dp), color = Color.LightGray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(+84) ${address.PhoneNumber.removePrefix("0")}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${address.StreetAddress}, ${address.City}",
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )

            if (address.IsDefault == 1) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    border = BorderStroke(1.dp, Color(0xFFFF4D1C)),
                    shape = RoundedCornerShape(2.dp),
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

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Sửa",
                color = Color(0xFFFF4D1C),
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable { onEdit(address.AddressID) }
                    .padding(bottom = 12.dp)
            )

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