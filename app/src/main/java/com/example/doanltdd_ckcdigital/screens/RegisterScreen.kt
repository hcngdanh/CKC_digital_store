package com.example.doanltdd_ckcdigital.screens


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: ()  -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Cấu hình màu sắc chung cho TextField trên nền đen
    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedIndicatorColor = Color(0xFFFFFFFF),
        unfocusedIndicatorColor = Color.Gray,
        focusedLabelColor = Color(0xFFFFFFFF),
        unfocusedLabelColor = Color.Gray,
        cursorColor = Color(0xFFFFFFFF)
    )

    fun handleRegister() {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(context, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            isLoading = false
            Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
            onRegisterSuccess()
        }, 1500)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
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
            AsyncImage(
                model = "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png",
                contentDescription = "Logo",
                modifier = Modifier.height(80.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("ĐĂNG KÝ TÀI KHOẢN", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(24.dp))

            // Họ tên
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Họ và tên") },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Mật khẩu
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.Gray) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, null, tint = Color.Gray)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Xác nhận mật khẩu
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Xác nhận mật khẩu") },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.Gray) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Nút Đăng ký
            Button(
                onClick = { handleRegister() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("ĐĂNG KÝ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Đã có tài khoản? ", color = Color.Gray)
                Text(
                    text = "Đăng nhập ngay",
                    color = Color(0xFFFFFFFF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}