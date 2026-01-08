package com.example.doanltdd_ckcdigital.screens


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun handleLogin() {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true
        // Giả lập gọi API
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            isLoading = false
            if (email == "admin@gmail.com" && password == "123456") {
                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            } else {
                Toast.makeText(context, "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
            }
        }, 1500)
    }

    Scaffold(
        containerColor = Color.Black // <-- NỀN ĐEN
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo App
            AsyncImage(
                model = "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png",
                contentDescription = "Logo",
                modifier = Modifier.height(100.dp), // Tăng kích thước logo chút cho đẹp
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "ĐĂNG NHẬP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White // <-- CHỮ TRẮNG
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Input Email (Style cho nền đen)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent, // Trong suốt để thấy nền đen
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,   // Chữ khi nhập màu trắng
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color(0xFFFF4D1C), // Viền cam khi focus
                    unfocusedIndicatorColor = Color.Gray,      // Viền xám khi không focus
                    focusedLabelColor = Color(0xFFFF4D1C),     // Label cam
                    unfocusedLabelColor = Color.Gray,          // Label xám
                    cursorColor = Color(0xFFFF4D1C)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.Gray) },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, null, tint = Color.Gray)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color(0xFFFF4D1C),
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color(0xFFFF4D1C),
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color(0xFFFF4D1C)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quên mật khẩu
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Text(
                    text = "Quên mật khẩu?",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Nút Đăng nhập
            Button(
                onClick = { handleLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D1C)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("ĐĂNG NHẬP", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chuyển sang Đăng ký
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Chưa có tài khoản? ", color = Color.Gray)
                Text(
                    text = "Đăng ký ngay",
                    color = Color(0xFFFF4D1C),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}