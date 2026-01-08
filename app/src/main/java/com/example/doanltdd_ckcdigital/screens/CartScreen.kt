package com.example.doanltdd_ckcdigital.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.utils.CartItem
import com.example.doanltdd_ckcdigital.utils.CartManager
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Giỏ hàng (${CartManager.cartItems.size})",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        bottomBar = {
            if (CartManager.cartItems.isNotEmpty()) {
                Surface(shadowElevation = 16.dp, color = Color.White) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tổng thanh toán (${CartManager.cartCount} sản phẩm)",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = formatter.format(CartManager.getTotalPrice()),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                        }
                        Button(
                            onClick = onCheckoutClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("Mua hàng", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (CartManager.cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Giỏ hàng đang trống", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F5F5)),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    items = CartManager.cartItems,
                    key = { it.product.ProductID }
                ) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { CartManager.increaseQuantity(item) },
                        onDecrease = { CartManager.decreaseQuantity(item) },
                        onDelete = { CartManager.removeProduct(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.product.ThumbnailURL ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.LightGray, RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.ProductName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Text(
                    text = formatter.format(item.product.Price),
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(30.dp)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onDecrease() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Remove, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    }

                    Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFFE0E0E0)))

                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .fillMaxHeight()
                            .background(Color(0xFFFAFAFA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${item.quantity}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFFE0E0E0)))

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onIncrease() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = Color.Gray)
            }
        }
    }
}