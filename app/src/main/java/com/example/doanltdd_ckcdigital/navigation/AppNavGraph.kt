package com.example.doanltdd_ckcdigital.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.doanltdd_ckcdigital.admin.AdminDashboardScreen
import com.example.doanltdd_ckcdigital.screens.*
import com.example.doanltdd_ckcdigital.utils.CartManager
import com.example.doanltdd_ckcdigital.utils.SessionManager
import com.example.doanltdd_ckcdigital.viewmodels.AuthViewModel
import com.example.doanltdd_ckcdigital.viewmodels.ProductViewModel

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
    val isLoading by productViewModel.isLoading.collectAsState() // Quan sát trạng thái load

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(
                isLoading = isLoading, onDataReady = {
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
                })
        }
        composable("product_list") {
            ProductListScreen(
                user = sessionManager.currentUser,
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onProductClick = { id -> navController.navigate("product_detail/$id") },
                onCartClick = { navController.navigate("cart") },
                onProfileClick = {
                    if (sessionManager.isLoggedIn()) navController.navigate("profile")
                    else navController.navigate("login")
                })
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
                        if (sessionManager.isLoggedIn() && sessionManager.isAdmin() == false) {
                            navController.navigate("checkout?productId=$productId")
                        } else {
                            navController.navigate("login")
                        }
                    },
                    onAddToCart = { p ->
                        CartManager.addToCart(p)
                        Toast.makeText(context, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show()
                    })
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

            if (user != null) {
                CheckoutScreen(
                    user = user,
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
                    })
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
                    onEditProfileClick = { navController.navigate("edit_profile") },
                    onPasswordChangeClick = { navController.navigate("change_password") })
            } else {
                navController.navigate("login")
            }
        }

        composable("edit_profile") {
            // Lấy thông tin user hiện tại từ session để điền sẵn vào form
            val currentUser = sessionManager.getUser()

            EditProfileScreen(user = currentUser, sessionManager = sessionManager, onBackClick = {
                navController.popBackStack()
            }, onSaveSuccess = {
                // Sau khi lưu thành công, quay lại trang trước đó (thường là Profile)
                navController.popBackStack()
                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            })
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
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                        Toast.makeText(context, "Vui lòng đăng nhập lại", Toast.LENGTH_LONG).show()
                    })
            }
        }

        composable("add_address") {
            val user = sessionManager.currentUser
            if (user != null) {
                AddAddressScreen(
                    userId = user.UserID,
                    onBackClick = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }
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

        composable("address_list") {
            val user = sessionManager.currentUser
            if (user != null) {
                AddressListScreen(
                    userId = user.UserID,
                    onBackClick = { navController.popBackStack() },
                    onAddressSelected = {
                        navController.popBackStack()
                    },
                    onEditClick = { addressId ->
                        navController.navigate("edit_address/$addressId") // Chuyển sang màn hình sửa
                    },
                    onAddNewAddressClick = {
                        navController.navigate("add_address") // Chuyển sang màn hình thêm mới
                    })
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
                onBack = { navController.popBackStack() })
        }

        composable("register") {
            RegisterScreen(viewModel = authViewModel, onRegisterSuccess = {}, onNavigateToLogin = {
                navController.popBackStack()
            }, onBack = { navController.popBackStack() })
        }

        composable("forgot_password") {
            ForgotPasswordScreen(onBackClick = { navController.popBackStack() })
        }

        composable("order_history") {
            val user = sessionManager.currentUser
            if (user != null) {
                OrderHistoryScreen(
                    userId = user.UserID, onBackClick = { navController.popBackStack() })
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
                AdminDashboardScreen(user = user, onLogout = {
                    sessionManager.clearSession()
                    navController.navigate("login") {
                        popUpTo("admin_dashboard") { inclusive = true }
                    }
                }, onNavigateToProductManager = {
                    Toast.makeText(context, "Quản lý sản phẩm", Toast.LENGTH_SHORT).show()
                }, onNavigateToOrderManager = {
                    Toast.makeText(context, "Quản lý đơn hàng", Toast.LENGTH_SHORT).show()
                }, onNavigateToUserManager = {
                    Toast.makeText(context, "Quản lý người dùng", Toast.LENGTH_SHORT).show()
                })
            } else {
                navController.navigate("login") {
                    popUpTo("admin_dashboard") { inclusive = true }
                }
            }
        }
    }
}