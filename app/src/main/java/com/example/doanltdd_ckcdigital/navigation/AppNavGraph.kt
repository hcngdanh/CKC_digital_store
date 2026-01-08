package com.example.doanltdd_ckcdigital.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doanltdd_ckcdigital.screens.CartScreen
import com.example.doanltdd_ckcdigital.screens.CheckoutScreen
import com.example.doanltdd_ckcdigital.screens.ForgotPasswordScreen
import com.example.doanltdd_ckcdigital.screens.LoginScreen
import com.example.doanltdd_ckcdigital.screens.OrderDetailScreen
import com.example.doanltdd_ckcdigital.screens.OrderHistoryScreen
import com.example.doanltdd_ckcdigital.screens.ProductDetailScreen
import com.example.doanltdd_ckcdigital.screens.ProductListScreen
import com.example.doanltdd_ckcdigital.screens.RegisterScreen
import com.example.doanltdd_ckcdigital.screens.SplashScreen
import com.example.doanltdd_ckcdigital.utils.CartManager
import com.example.doanltdd_ckcdigital.utils.UserSession
import com.example.doanltdd_ckcdigital.viewmodels.AuthViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // 1. Màn hình chờ (Splash)
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("product_list") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // 2. Danh sách sản phẩm (Trang chủ)
        composable("product_list") {
            ProductListScreen(
                onProductClick = { productId ->
                    navController.navigate("product_detail/$productId")
                },
                onCartClick = {
                    navController.navigate("cart")
                }
            )
        }

        // 3. Chi tiết sản phẩm
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
                        if (UserSession.isLoggedIn) {
                            navController.navigate("checkout")
                        } else {
                            Toast.makeText(context, "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        }
                    },
                    onAddToCart = { product ->
                        CartManager.addToCart(product)
                        Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // 4. Giỏ hàng
        composable("cart") {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = {
                    if (UserSession.isLoggedIn) {
                        navController.navigate("checkout")
                    } else {
                        Toast.makeText(context, "Vui lòng đăng nhập để thanh toán", Toast.LENGTH_SHORT).show()
                        navController.navigate("login")
                    }
                }
            )
        }

        // 5. Thanh toán
        composable("checkout") {
            CheckoutScreen(
                onBackClick = { navController.popBackStack() },
                onOrderSuccess = {
                    CartManager.clearCart() // Xóa giỏ hàng sau khi đặt thành công (nếu cần)
                    navController.navigate("product_list") {
                        popUpTo("product_list") { inclusive = true }
                    }
                }
            )
        }

        // 6. Đăng nhập
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate("product_list") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // 7. Đăng ký
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                    navController.popBackStack()
                }
            )
        }

        // 8. Quên mật khẩu
        composable("forgot_password") {
            ForgotPasswordScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // 9. Lịch sử đơn hàng
        composable("order_history") {
            OrderHistoryScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // 10. Chi tiết đơn hàng (Mẫu)
        composable("order_detail") {
            OrderDetailScreen()
        }
    }
}