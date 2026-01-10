package com.example.doanltdd_ckcdigital.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doanltdd_ckcdigital.screens.* // Import hết các màn hình
import com.example.doanltdd_ckcdigital.utils.CartManager
import com.example.doanltdd_ckcdigital.viewmodels.AuthViewModel
import com.example.doanltdd_ckcdigital.viewmodels.SessionManager

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(sessionManager) as T
            }
        }
    )

    NavHost(navController = navController, startDestination = "splash") {

        // ... (Giữ nguyên splash, product_list, product_detail, cart...)
        composable("splash") {
            SplashScreen(onTimeout = { navController.navigate("product_list") { popUpTo("splash") { inclusive = true } } })
        }
        composable("product_list") {
            ProductListScreen(
                user = sessionManager.getUser(),
                onLogout = { sessionManager.clearSession(); navController.navigate("login") { popUpTo("product_list") { inclusive = true } } },
                onProductClick = { id -> navController.navigate("product_detail/$id") },
                onCartClick = { navController.navigate("cart") },
                onProfileClick = { if (sessionManager.isLoggedIn()) navController.navigate("profile") else navController.navigate("login") }
            )
        }
        composable(route = "product_detail/{productId}", arguments = listOf(navArgument("productId") { type = NavType.IntType })) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId")
            if (productId != null) {
                ProductDetailScreen(
                    productId = productId,
                    onBackClick = { navController.popBackStack() },
                    onCartClick = { navController.navigate("cart") },
                    onBuyNowClick = { if (sessionManager.isLoggedIn()) navController.navigate("checkout") else navController.navigate("login") },
                    onAddToCart = { p -> CartManager.addToCart(p); Toast.makeText(context, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show() }
                )
            }
        }
        composable("cart") {
            CartScreen(onBackClick = { navController.popBackStack() }, onCheckoutClick = { if (sessionManager.isLoggedIn()) navController.navigate("checkout") else navController.navigate("login") })
        }

        // --- SỬA LỖI CHECKOUT SCREEN ---
        composable("checkout") {
            CheckoutScreen(
                user = sessionManager.getUser(),
                onBackClick = { navController.popBackStack() },
                // Thêm tham số còn thiếu:
                onAddressClick = { navController.navigate("address_list") }, // Chuyển sang danh sách địa chỉ
                buyNowProductId = -1, // Mặc định -1 nếu thanh toán giỏ hàng
                onOrderSuccess = {
                    CartManager.clearCart()
                    navController.navigate("product_list") { popUpTo("product_list") { inclusive = true } }
                }
            )
        }

        // --- SỬA LỖI PROFILE SCREEN ---
        composable(route = "profile") {
            ProfileScreen(
                user = sessionManager.getUser(), // Truyền user vào
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    sessionManager.clearSession()
                    navController.navigate("product_list") { popUpTo("product_list") { inclusive = true } }
                },
                onAddressManageClick = {
                    navController.navigate("address_list") // Chuyển sang danh sách địa chỉ
                }
            )
        }

        // --- THÊM MÀN HÌNH DANH SÁCH ĐỊA CHỈ (ADDRESS LIST) ---
        composable(route = "address_list") {
            AddressListScreen(
                onBackClick = { navController.popBackStack() },
                onAddressSelected = { selectedAddress ->
                    // Xử lý khi chọn địa chỉ (ví dụ: lưu vào session rồi quay lại)
                    // Hiện tại chỉ quay lại màn hình trước
                    navController.popBackStack()
                }
            )
        }

        // ... (Giữ nguyên login, register, forgot_password...)
        composable("login") { LoginScreen(viewModel = authViewModel, onNavigateToHome = { navController.navigate("product_list") { popUpTo("login") { inclusive = true } } }, onNavigateToRegister = { navController.navigate("register") }, onNavigateToForgotPassword = { navController.navigate("forgot_password") }, onBack = { navController.popBackStack() }) }
        composable("register") { RegisterScreen(onRegisterSuccess = { navController.navigate("login") { popUpTo("register") { inclusive = true } } }, onNavigateToLogin = { navController.navigate("login") { popUpTo("register") { inclusive = true } } }, onBack = { navController.popBackStack(); navController.popBackStack() }) }
        composable("forgot_password") { ForgotPasswordScreen(onBackClick = { navController.popBackStack() }) }
        composable("order_history") { OrderHistoryScreen(onBackClick = { navController.popBackStack() }) }
        composable("order_detail") { OrderDetailScreen() }
    }
}