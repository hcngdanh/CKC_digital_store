package com.example.doanltdd_ckcdigital.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
fun EditAddressScreen(
    addressId: Int,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(addressId) {
        try {
            isLoading = true
            val address = RetrofitClient.apiService.getAddressDetail(addressId)

            name = address.ReceiverName
            phone = address.PhoneNumber
            street = address.StreetAddress
            city = address.City
            isDefault = address.IsDefault == 1
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sửa địa chỉ", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFF4D1C))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Họ tên") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone, onValueChange = { phone = it },
                    label = { Text("Số điện thoại") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = street, onValueChange = { street = it },
                    label = { Text("Địa chỉ chi tiết") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = city, onValueChange = { city = it },
                    label = { Text("Thành phố") }, modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
                    Text("Đặt làm địa chỉ mặc định")
                }

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val updatedAddress = UserAddress(
                                    addressId, 0, name, phone, street, city, if (isDefault) 1 else 0
                                )
                                val res = RetrofitClient.apiService.updateAddress(addressId, updatedAddress)
                                if (res.success) {
                                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                                    onSaveSuccess()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Lỗi cập nhật", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000000)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("LƯU THAY ĐỔI", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}