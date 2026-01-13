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
import com.example.doanltdd_ckcdigital.utils.SessionManager
import com.example.doanltdd_ckcdigital.viewmodels.AuthViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val sessionManager = remember { SessionManager.getInstance(context) }

    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(sessionManager) as T
            }
        }
    )

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(
                isLoading = false,
                onDataReady = {
                    val savedUser = sessionManager.currentUser

                    if (savedUser != null && savedUser.RoleID == 1) {
                        navController.navigate("admin_dashboard") {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else {
                        navController.navigate("product_list") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("product_list") {
            ProductListScreen(
                user = sessionManager.currentUser,
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate("login") {
                        popUpTo("product_list") { inclusive = true }
                    }
                },
                onProductClick = { id -> navController.navigate("product_detail/$id") },
                onCartClick = { navController.navigate("cart") },
                onProfileClick = {
                    if (sessionManager.isLoggedIn()) navController.navigate("profile")
                    else navController.navigate("login")
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
                    onBuyNowClick = {
                        if (sessionManager.isLoggedIn()&& sessionManager.isAdmin()==false) {
                            navController.navigate("checkout?productId=$productId")
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
                    if (sessionManager.isLoggedIn()) {
                        navController.navigate("checkout?productId=-1")
                    } else {
                        navController.navigate("login")
                    }
                }
            )
        }

        composable(
            route = "checkout?productId={productId}",
            arguments = listOf(navArgument("productId") { defaultValue = -1 })
        ) { backStackEntry ->
            val user = sessionManager.currentUser
            val buyNowId = backStackEntry.arguments?.getInt("productId") ?: -1

            if (user != null) {
                CheckoutScreen(
                    user = user,
                    selectedAddress = null,
                    onBackClick = { navController.popBackStack() },
                    onAddressClick = { navController.navigate("address_list") },
                    buyNowProductId = buyNowId,
                    onOrderSuccess = {
                        if (buyNowId == -1) {
                            CartManager.clearCart()
                        }
                        navController.navigate("product_list") {
                            popUpTo("product_list") { inclusive = true }
                        }
                    }
                )
            } else {
                navController.navigate("login")
            }
        }

        composable("profile") {
            val user = sessionManager.currentUser
            if (user != null) {
                ProfileScreen(
                    user = user,
                    onBackClick = { navController.popBackStack() },
                    onLogoutClick = {
                        sessionManager.clearSession()
                        navController.navigate("product_list") {
                            popUpTo("product_list") { inclusive = true }
                        }
                    },
                    onAddressManageClick = { navController.navigate("address_list") },
                    onOrderHistoryClick = { navController.navigate("order_history") },
                    onEditProfileClick = {
                        Toast.makeText(context, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
                    },
                    onPasswordChangeClick = {
                        navController.navigate("change_password")
                    }
                )
            } else {
                navController.navigate("login")
            }
        }

        composable("change_password") {
            val user = sessionManager.currentUser
            if (user != null) {
                ChangePasswordScreen(
                    userId = user.UserID,
                    onBackClick = { navController.popBackStack() },
                    onSuccess = {
                        // Khi đổi mật khẩu thành công, thường sẽ bắt đăng nhập lại
                        sessionManager.clearSession()
                        Toast.makeText(context, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("product_list") { inclusive = true }
                        }
                    }
                )
            } else {
                // Nếu không tìm thấy user (lỗi session), quay về login
                navController.navigate("login")
            }
        }

        composable("address_list") {
            val user = sessionManager.currentUser
            if (user != null) {
                AddressListScreen(
                    userId = user.UserID,
                    currentSelectedId = -1,
                    onBackClick = { navController.popBackStack() },
                    onAddressSelected = {
                        navController.popBackStack()
                    },
                    onEditClick = { addressId ->
                        Toast.makeText(context, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
                    },
                    onAddNewAddressClick = {
                        Toast.makeText(context, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                navController.navigate("login")
            }
        }

        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate("product_list") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToAdmin = {
                    navController.navigate("admin_dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(onBackClick = { navController.popBackStack() })
        }

        composable("order_history") {
            val user = sessionManager.currentUser
            if (user != null) {
                OrderHistoryScreen(
                    userId = user.UserID,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                navController.navigate("login")
            }
        }

        composable("order_detail") {
            OrderDetailScreen()
        }

        composable("admin_dashboard") {
            val user = sessionManager.currentUser
            if (user != null && user.RoleID == 1) {
                AdminDashboardScreen(
                    user = user,
                    onLogout = {
                        sessionManager.clearSession()
                        navController.navigate("login") {
                            popUpTo("admin_dashboard") { inclusive = true }
                        }
                    },
                    onNavigateToOrderManager = {
                        Toast.makeText(context, "Quản lý đơn hàng", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                navController.navigate("login") {
                    popUpTo("admin_dashboard") { inclusive = true }
                }
            }
        }
    }
}