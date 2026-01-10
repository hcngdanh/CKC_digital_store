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
import com.example.doanltdd_ckcdigital.screens.*
import com.example.doanltdd_ckcdigital.utils.CartManager
import com.example.doanltdd_ckcdigital.viewmodels.AuthViewModel
import com.example.doanltdd_ckcdigital.viewmodels.SessionManager

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Khởi tạo AuthViewModel thông qua Factory để truyền sessionManager
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(sessionManager) as T
        }
    )

    NavHost(navController = navController, startDestination = "splash") {

        // 1. Màn hình chào: Chờ và chuyển hướng vào trang chủ
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("product_list") { popUpTo("splash") { inclusive = true } }
            })
        }

        // 2. Trang chủ: Danh sách sản phẩm và quản lý trạng thái đăng nhập
        composable("product_list") {
            ProductListScreen(
                user = sessionManager.getUser(),
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate("login") { popUpTo("product_list") { inclusive = true } }
                },
                onProductClick = { id -> navController.navigate("product_detail/$id") },
                onCartClick = { navController.navigate("cart") },
                onProfileClick = {
                    if (sessionManager.isLoggedIn()) navController.navigate("profile")
                    else navController.navigate("login")
                }
            )
        }

        // 3. Chi tiết sản phẩm: Xử lý Mua ngay (truyền ID) hoặc Thêm vào giỏ
        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { entry ->
            entry.arguments?.getInt("productId")?.let { id ->
                ProductDetailScreen(
                    productId = id,
                    onBackClick = { navController.popBackStack() },
                    onCartClick = { navController.navigate("cart") },
                    onBuyNowClick = { product ->
                        // Điều hướng thẳng tới thanh toán với ID sản phẩm riêng biệt
                        navController.navigate("checkout?productId=${product.ProductID}")
                    },
                    onAddToCart = { product ->
                        CartManager.addToCart(product)
                        Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // 4. Giỏ hàng: Xem danh sách đã thêm và chuyển sang thanh toán
        composable("cart") {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = {
                    if (sessionManager.isLoggedIn()) navController.navigate("checkout")
                    else navController.navigate("login")
                }
            )
        }

        // 5. Thanh toán: Hỗ trợ cả Mua ngay (productId) và Thanh toán giỏ hàng chung
        composable(
            route = "checkout?productId={productId}",
            arguments = listOf(navArgument("productId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: -1

            CheckoutScreen(
                user = sessionManager.getUser(),
                buyNowProductId = productId,
                onBackClick = { navController.popBackStack() },
                onAddressClick = {
                    // Điều hướng sang trang quản lý địa chỉ của user
                    navController.navigate("address_list")
                },
                onOrderSuccess = {
                    if (productId == -1) CartManager.clearCart()
                    navController.navigate("product_list") {
                        popUpTo("product_list") { inclusive = true }
                    }
                }
            )
        }

        // 6. Auth: Đăng nhập, Đăng ký và Quên mật khẩu
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate("product_list") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("login") { popUpTo("register") { inclusive = true } } },
                onNavigateToLogin = { navController.navigate("login") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(onBackClick = { navController.popBackStack() })
        }

        // 7. Người dùng: Hồ sơ cá nhân và Lịch sử đơn hàng
        composable("profile") {
            ProfileScreen(
                user = sessionManager.getUser(),
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    sessionManager.clearSession()
                    navController.navigate("product_list") { popUpTo(0) }
                }
            )
        }

        composable("order_history") {
            OrderHistoryScreen(onBackClick = { navController.popBackStack() })
        }

        composable("address_list") {
            AddressListScreen(
                onBackClick = { navController.popBackStack() },
                onAddressSelected = { selectedAddress ->
                    // Lưu địa chỉ vào một biến state hoặc ViewModel nếu cần
                    navController.popBackStack()
                }
            )
        }
    }
}