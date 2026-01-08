package com.example.doanltdd_ckcdigital.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doanltdd_ckcdigital.screens.CartScreen
import com.example.doanltdd_ckcdigital.screens.CheckoutScreen
import com.example.doanltdd_ckcdigital.screens.ProductDetailScreen
import com.example.doanltdd_ckcdigital.screens.ProductListScreen
import com.example.doanltdd_ckcdigital.utils.CartManager

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "product_list"
    ) {
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

        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            val context = LocalContext.current

            ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate("cart") },
                onBuyNowClick = { navController.navigate("checkout") },
                onAddToCart = { product ->
                    CartManager.addProduct(product)

                    Toast.makeText(
                        context,
                        "Đã thêm ${product.ProductName} vào giỏ!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        composable("cart") {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = { navController.navigate("checkout") }
            )
        }

        composable("checkout") {
            CheckoutScreen(
                onBackClick = { navController.popBackStack() },
                onOrderSuccess = {
                    CartManager.clearCart()

                    navController.navigate("product_list") {
                        popUpTo("product_list") { inclusive = true }
                    }
                }
            )
        }
    }
}