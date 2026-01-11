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
            SplashScreen(onTimeout = {
                navController.navigate("product_list") {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }
            })
        }
        composable("product_list") {
            ProductListScreen(
                user = sessionManager.getUser(),
                onLogout = {
                    sessionManager.clearSession(); navController.navigate("login") {
                    popUpTo(
                        "product_list"
                    ) { inclusive = true }
                }
                },
                onProductClick = { id -> navController.navigate("product_detail/$id") },
                onCartClick = { navController.navigate("cart") },
                onProfileClick = {
                    if (sessionManager.isLoggedIn()) navController.navigate("profile") else navController.navigate(
                        "login"
                    )
                }
            )
        }
        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId")
            if (productId != null) {
                ProductDetailScreen(
                    productId = productId,
                    onBackClick = { navController.popBackStack() },
                    onCartClick = { navController.navigate("cart") },
                    onBuyNowClick = { p ->
                        if (sessionManager.isLoggedIn()) {
                            // TRUYỀN ID sản phẩm vào route checkout
                            navController.navigate("checkout?productId=${p.ProductID}")
                        } else {
                            navController.navigate("login")
                        }
                    },
                    onAddToCart = { p ->
                        CartManager.addToCart(p)
                        Toast.makeText(context, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
        composable("cart") {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = {
                    if (sessionManager.isLoggedIn()) navController.navigate("checkout") else navController.navigate(
                        "login"
                    )
                })
        }

        // --- SỬA LỖI CHECKOUT SCREEN ---
        composable(
            route = "checkout?productId={productId}",
            arguments = listOf(navArgument("productId") {
                type = NavType.IntType
                defaultValue = -1 // Mặc định là -1 nếu đi từ giỏ hàng
            })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: -1

            CheckoutScreen(
                user = sessionManager.getUser(),
                onBackClick = { navController.popBackStack() },
                onAddressClick = { navController.navigate("address_list") },
                buyNowProductId = productId, // Truyền ID thực tế vào đây
                onOrderSuccess = {
                    // Nếu không phải mua ngay (productId == -1), mới xóa giỏ hàng
                    if (productId == -1) CartManager.clearCart()
                    navController.navigate("product_list") {
                        popUpTo("product_list") { inclusive = true }
                    }
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
                    navController.navigate("product_list") {
                        popUpTo("product_list") {
                            inclusive = true
                        }
                    }
                },
                onAddressManageClick = {
                    navController.navigate("address_list") // Chuyển sang danh sách địa chỉ
                }
            )
        }

        // --- THÊM MÀN HÌNH DANH SÁCH ĐỊA CHỈ (ADDRESS LIST) ---
        composable("address_list") {
            val user = sessionManager.getUser()
            AddressListScreen(
                userId = user?.UserID ?: -1,
                onBackClick = { navController.popBackStack() },
                onAddressSelected = { selected ->
                    // Lưu địa chỉ được chọn vào đâu đó hoặc quay về Checkout
                    navController.popBackStack()
                },
                onEditClick = { addressId ->
                    navController.navigate("edit_address/$addressId")
                },
                onAddNewAddressClick = {
                    // LỆNH CHUYỂN SANG MÀN HÌNH THÊM
                    navController.navigate("add_address")
                }
            )
        }

        composable(
            route = "edit_address/{addressId}",
            arguments = listOf(navArgument("addressId") { type = NavType.IntType })
        ) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getInt("addressId") ?: -1
            EditAddressScreen(
                addressId = addressId,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable("add_address") {
            val user = sessionManager.getUser()
            AddAddressScreen(
                userId = user?.UserID ?: -1,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // ... (Giữ nguyên login, register, forgot_password...)
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate("product_list") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onBack = { navController.popBackStack() })
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    navController.navigate("login") { popUpTo("register") { inclusive = true } }
                },
                onNavigateToLogin = { navController.navigate("login") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("forgot_password") { ForgotPasswordScreen(onBackClick = { navController.popBackStack() }) }
        composable("order_history") { OrderHistoryScreen(onBackClick = { navController.popBackStack() }) }
        composable("order_detail") { OrderDetailScreen() }
    }
}