package com.example.doanltdd_ckcdigital.screensimport

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

val BgDark = Color(0xFF1E1E1E)
val CardDark = Color(0xFF2C2C2C)
val TextWhite = Color(0xFFE0E0E0)
val TextGrey = Color(0xFFB0B0B0)
val SelectionBlue = Color(0xFF448AFF)

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    val logoUrl = "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        AsyncImage(
            model = logoUrl,
            contentDescription = "Shop Logo",
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TẠO TÀI KHOẢN",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Text(
            text = "Đăng ký thành viên CKC Digital",
            fontSize = 14.sp,
            color = TextGrey
        )

        Spacer(modifier = Modifier.height(32.dp))

        CustomRegisterInput(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Họ và tên",
            icon = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomRegisterInput(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomRegisterInput(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = "Số điện thoại",
            icon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomPasswordInput(
            value = password,
            onValueChange = { password = it },
            label = "Mật khẩu",
            isVisible = isPasswordVisible,
            onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomPasswordInput(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Xác nhận mật khẩu",
            isVisible = isConfirmPasswordVisible,
            onVisibilityChange = { isConfirmPasswordVisible = !isConfirmPasswordVisible }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                } else if (password != confirmPassword) {
                    Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                } else {
                    onRegisterSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SelectionBlue,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("ĐĂNG KÝ NGAY", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Đã có tài khoản? ", color = TextGrey)
            Text(
                text = "Đăng nhập",
                color = SelectionBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    // Cập nhật lệnh gọi hàm ở đây
                    onNavigateToLogin()
                }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRegisterInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = TextGrey) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CardDark,
            unfocusedContainerColor = CardDark,
            disabledContainerColor = CardDark,
            focusedBorderColor = SelectionBlue,
            unfocusedBorderColor = Color.Transparent,
            focusedLabelColor = SelectionBlue,
            unfocusedLabelColor = TextGrey,
            cursorColor = SelectionBlue,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onVisibilityChange: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextGrey) },
        trailingIcon = {
            IconButton(onClick = onVisibilityChange) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Password",
                    tint = TextGrey
                )
            }
        },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CardDark,
            unfocusedContainerColor = CardDark,
            focusedBorderColor = SelectionBlue,
            unfocusedBorderColor = Color.Transparent,
            focusedLabelColor = SelectionBlue,
            unfocusedLabelColor = TextGrey,
            cursorColor = SelectionBlue,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        )
    )
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    // Cập nhật lệnh gọi trong Preview
    RegisterScreen(onRegisterSuccess = {}, onNavigateToLogin = {})
}
