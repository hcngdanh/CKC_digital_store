package com.example.doanltdd_ckcdigital.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
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
    val sessionManager = SessionManager.getInstance(context)
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(sessionManager) as T
            }
        }
    )

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("product_list") {
                    popUpTo("splash") { inclusive = true }
                }
            })
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
                    onBuyNowClick = { p ->
                        if (sessionManager.isLoggedIn()) {
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
                    if (sessionManager.isLoggedIn()) navController.navigate("checkout")
                    else navController.navigate("login")
                }
            )
        }

        composable(
            route = "checkout?productId={productId}",
            arguments = listOf(navArgument("productId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: -1

            CheckoutScreen(
                user = sessionManager.currentUser,
                onBackClick = { navController.popBackStack() },
                onAddressClick = { navController.navigate("address_list") },
                buyNowProductId = productId,
                onOrderSuccess = {
                    if (productId == -1) CartManager.clearCart()
                    navController.navigate("product_list") {
                        popUpTo("product_list") { inclusive = true }
                    }
                }
            )
        }

        composable(route = "profile") {
            ProfileScreen(
                user = sessionManager.currentUser,
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    sessionManager.clearSession()
                    navController.navigate("product_list") {
                        popUpTo("product_list") { inclusive = true }
                    }
                },
                onAddressManageClick = {
                    navController.navigate("address_list")
                }
            )
        }

        composable("address_list") {
            val userId = sessionManager.currentUser?.UserID ?: -1

            AddressListScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() },
                onAddressSelected = { selected ->
                    navController.popBackStack()
                },
                onEditClick = { addressId ->
                    navController.navigate("edit_address/$addressId")
                },
                onAddNewAddressClick = {
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
            val userId = sessionManager.currentUser?.UserID ?: -1

            AddAddressScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate("product_list") {
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