package com.example.doanltdd_ckcdigital.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doanltdd_ckcdigital.models.UserModel
import com.example.doanltdd_ckcdigital.utils.SessionManager
import com.example.doanltdd_ckcdigital.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    user: UserModel?,
    sessionManager: SessionManager,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

    var fullName by remember { mutableStateOf(user?.FullName ?: "") }
    var phone by remember { mutableStateOf(user?.Phone ?: "") }

    LaunchedEffect(Unit) {
        viewModel.updateEvent.collect { result ->
            when (result) {
                is ProfileViewModel.UpdateResult.Success -> {
                    Toast.makeText(context, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show()
                    onSaveSuccess()
                }
                is ProfileViewModel.UpdateResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chỉnh sửa thông tin", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = user?.Email ?: "",
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = Color.LightGray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Họ và tên") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { if (it.length <= 10) phone = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (fullName.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (phone.length != 10 || !phone.all { it.isDigit() }) {
                        Toast.makeText(context, "Số điện thoại phải đủ 10 chữ số", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    user?.let {
                        viewModel.updateProfile(it.UserID, fullName, phone, sessionManager)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("LƯU THÔNG TIN", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}