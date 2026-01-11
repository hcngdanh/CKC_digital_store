package com.example.doanltdd_ckcdigital.screens


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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
fun AddAddressScreen(
    userId: Int,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State cho các ô nhập liệu
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thêm địa chỉ mới", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Họ tên người nhận") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Số điện thoại") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = street, onValueChange = { street = it },
                label = { Text("Địa chỉ chi tiết (Số nhà, tên đường)") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = city, onValueChange = { city = it },
                label = { Text("Tỉnh/Thành phố") }, modifier = Modifier.fillMaxWidth()
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
                    if (name.isBlank() || phone.isBlank() || street.isBlank() || city.isBlank()) {
                        Toast.makeText(context, "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        try {
                            // AddressID gửi 0 vì server tự tăng (Auto Increment)
                            val newAddress = UserAddress(
                                0, userId, name, phone, street, city, if (isDefault) 1 else 0
                            )
                            val res = RetrofitClient.apiService.addAddress(newAddress)
                            if (res.success) {
                                Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show()
                                onSaveSuccess()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D1C)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("HOÀN TẤT", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}