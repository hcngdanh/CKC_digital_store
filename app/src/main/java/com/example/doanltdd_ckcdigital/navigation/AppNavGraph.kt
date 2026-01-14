package com.example.doanltdd_ckcdigital.navigation

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doanltdd_ckcdigital.admin.AdminDashboardScreen
import com.example.doanltdd_ckcdigital.admin.AdminOrderManagerScreen
import com.example.doanltdd_ckcdigital.models.UserAddress
import com.example.doanltdd_ckcdigital.screens.*
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import com.example.doanltdd_ckcdigital.utils.CartManager
import com.example.doanltdd_ckcdigital.utils.SessionManager
import com.example.doanltdd_ckcdigital.viewmodels.AuthViewModel
import com.example.doanltdd_ckcdigital.viewmodels.ProductViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }

    val authViewModel: AuthViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(sessionManager) as T
        }
    })

    val productViewModel: ProductViewModel = viewModel()
    val isLoading by productViewModel.isLoading.collectAsState()
    val currentUser = sessionManager.currentUser

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            try {
                val response = RetrofitClient.apiService.getCartItems(currentUser.UserID)
                if (response.success) {
                    CartManager.syncCartFromServer(response.data)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            CartManager.clearCart()
        }
    }

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(isLoading = isLoading, onDataReady = {
                val savedUser = sessionManager.currentUser
                if (savedUser != null && savedUser.RoleID == 1) {
                    navController.navigate("admin_dashboard") { popUpTo("splash") { inclusive = true } }
                } else {
                    navController.navigate("product_list") { popUpTo("splash") { inclusive = true } }
                }
            })
        }

        composable("product_list") {
            ProductListScreen(
                user = sessionManager.currentUser,
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate("login") { popUpTo(navController.graph.startDestinationId) { inclusive = true } }
                },
                onProductClick = { id -> navController.navigate("product_detail/$id") },
                onCartClick = { navController.navigate("cart") },
                onProfileClick = {
                    if (sessionManager.isLoggedIn()) navController.navigate("profile") else navController.navigate("login")
                }
            )
        }

        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId")
            val scope = rememberCoroutineScope()

            if (productId != null) {
                ProductDetailScreen(
                    productId = productId,
                    onBackClick = { navController.popBackStack() },
                    onCartClick = { navController.navigate("cart") },
                    onBuyNowClick = {
                        if (sessionManager.isLoggedIn() && sessionManager.isAdmin() == false) {
                            navController.navigate("checkout?productId=$productId")
                        } else {
                            navController.navigate("login")
                        }
                    },
                    onAddToCart = { product ->
                        val user = sessionManager.currentUser
                        if (user != null) {
                            scope.launch {
                                CartManager.addToCart(user.UserID, product)
                                Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        }
                    }
                )
            }
        }

        composable("cart") {
            CartScreen(onBackClick = { navController.popBackStack() }, onCheckoutClick = {
                if (sessionManager.isLoggedIn()) {
                    navController.navigate("checkout?productId=-1")
                } else {
                    navController.navigate("login")
                }
            })
        }

        composable(
            route = "checkout?productId={productId}",
            arguments = listOf(navArgument("productId") { defaultValue = -1 })
        ) { backStackEntry ->
            val user = sessionManager.currentUser
            val buyNowId = backStackEntry.arguments?.getInt("productId") ?: -1
            val selectedAddressFromList = backStackEntry.savedStateHandle.get<UserAddress>("selected_address")
            if (user != null) {
                CheckoutScreen(
                    user = user,
                    selectedAddress = selectedAddressFromList,
                    onBackClick = { navController.popBackStack() },
                    onAddressClick = { navController.navigate("address_list") },
                    buyNowProductId = buyNowId,
                    onOrderSuccess = {
                        if (buyNowId == -1) CartManager.clearCart()
                        navController.navigate("order_success") { popUpTo("product_list") { inclusive = false } }
                    }
                )
            } else {
                navController.navigate("login")
            }
        }

        composable("order_success") {
            OrderSuccessScreen(onContinueShoppingClick = {
                navController.navigate("product_list") { popUpTo("product_list") { inclusive = true } }
            })
        }

        // --- CẬP NHẬT ROUTE order_history ĐỂ NHẬN THAM SỐ STATUS ---
        composable(
            route = "order_history?status={status}",
            arguments = listOf(navArgument("status") { defaultValue = "ALL" })
        ) { backStackEntry ->
            val statusString = backStackEntry.arguments?.getString("status") ?: "ALL"
            // Chuyển String thành Enum HistoryStatus
            val initialTab = try {
                HistoryStatus.valueOf(statusString)
            } catch (e: Exception) {
                HistoryStatus.ALL
            }

            val user = sessionManager.currentUser
            if (user != null) {
                OrderHistoryScreen(
                    userId = user.UserID,
                    initialTab = initialTab, // Truyền Tab khởi tạo vào màn hình
                    onBackClick = { navController.popBackStack() },
                    onOrderClick = { orderId -> navController.navigate("order_detail/$orderId") }
                )
            } else {
                navController.navigate("login")
            }
        }

        composable(
            route = "order_detail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId")
            if (orderId != null) {
                OrderDetailScreen(orderId = orderId, onBackClick = { navController.popBackStack() })
            }
        }

        composable("address_list") {
            val user = sessionManager.currentUser
            if (user != null) {
                AddressListScreen(
                    userId = user.UserID,
                    currentSelectedId = -1,
                    onBackClick = { navController.popBackStack() },
                    onAddressSelected = { address ->
                        navController.previousBackStackEntry?.savedStateHandle?.set("selected_address", address)
                        navController.popBackStack()
                    },
                    onEditClick = { id -> navController.navigate("edit_address/$id") },
                    onAddNewAddressClick = { navController.navigate("add_address") }
                )
            } else {
                navController.navigate("login")
            }
        }

        composable("admin_dashboard") {
            val user = sessionManager.currentUser
            if (user != null && user.RoleID == 1) {
                AdminDashboardScreen(
                    user = user,
                    onLogout = {
                        sessionManager.clearSession()
                        navController.navigate("login") { popUpTo("admin_dashboard") { inclusive = true } }
                    },
                    onNavigateToOrderManager = { navController.navigate("admin_order_manager") }
                )
            } else {
                navController.navigate("login") { popUpTo("admin_dashboard") { inclusive = true } }
            }
        }

        composable("admin_order_manager") {
            AdminOrderManagerScreen(onBackClick = { navController.popBackStack() })
        }

        composable("profile") {
            val user = sessionManager.currentUser
            if (user != null) {
                ProfileScreen(
                    user = user,
                    onBackClick = { navController.popBackStack() },
                    onLogoutClick = {
                        sessionManager.clearSession()
                        navController.navigate("product_list") { popUpTo("product_list") { inclusive = true } }
                    },
                    onAddressManageClick = { navController.navigate("address_list") },

                    // --- CẬP NHẬT: Gửi Status sang OrderHistory ---
                    onOrderHistoryClick = { status ->
                        navController.navigate("order_history?status=$status")
                    },

                    onEditProfileClick = { navController.navigate("edit_profile") },
                    onPasswordChangeClick = { navController.navigate("change_password") },
                    onFavoriteClick = { navController.navigate("wishlist") }
                )
            } else {
                navController.navigate("login")
            }
        }

        composable("edit_profile") {
            val currentUser = sessionManager.getUser()
            EditProfileScreen(
                user = currentUser,
                sessionManager = sessionManager,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                    Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        composable("change_password") {
            val user = sessionManager.currentUser
            if (user != null) {
                ChangePasswordScreen(
                    userId = user.UserID,
                    onBackClick = { navController.popBackStack() },
                    onSuccess = {
                        sessionManager.clearSession()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                        Toast.makeText(context, "Vui lòng đăng nhập lại", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }

        composable("add_address") {
            val user = sessionManager.currentUser
            if (user != null) AddAddressScreen(
                userId = user.UserID,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(
            "edit_address/{addressId}",
            arguments = listOf(navArgument("addressId") { type = NavType.IntType })
        ) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getInt("addressId") ?: -1
            EditAddressScreen(
                addressId = addressId,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToHome = { navController.navigate("product_list") { popUpTo("login") { inclusive = true } } },
                onNavigateToAdmin = { navController.navigate("admin_dashboard") { popUpTo("login") { inclusive = true } } },
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {},
                onNavigateToLogin = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable("forgot_password") { ForgotPasswordScreen(onBackClick = { navController.popBackStack() }) }

        composable("wishlist") {
            val user = sessionManager.currentUser
            if (user != null) {
                WishlistScreen(
                    userId = user.UserID,
                    onBackClick = { navController.popBackStack() },
                    onProductClick = { productId -> navController.navigate("product_detail/$productId") }
                )
            } else {
                navController.navigate("login")
            }
        }
    }
}