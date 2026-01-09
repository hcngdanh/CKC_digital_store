package com.example.doanltdd_ckcdigital.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doanltdd_ckcdigital.models.ProductModel
import com.example.doanltdd_ckcdigital.models.UserModel
import com.example.doanltdd_ckcdigital.services.RetrofitClient
import com.example.doanltdd_ckcdigital.utils.CartManager
import com.example.doanltdd_ckcdigital.utils.CartManager.cartCount
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: ()-> Unit,
    user: UserModel?, // Thêm tham số này
    onLogout: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("Mặc định") }
    var isGridView by remember { mutableStateOf(true) }

    var products by remember { mutableStateOf<List<ProductModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedCategoryKey by remember { mutableStateOf<String?>(null) }

    val displayTitle = remember(selectedCategoryKey) {
        when (selectedCategoryKey) {
            "FullFrame" -> "MÁY ẢNH SONY MIRRORLESS FULL FRAME"
            "APS-C" -> "MÁY ẢNH SONY MIRRORLESS APS-C"
            "LensG" -> "ỐNG KÍNH SONY DÒNG G"
            "LensGM" -> "ỐNG KÍNH SONY DÒNG G MASTER"
            "PHỤ KIỆN" -> "PHỤ KIỆN MÁY ẢNH & QUAY PHIM"
            else -> "GIAN HÀNG SONY | MÁY ẢNH & PHỤ KIỆN \n PHÂN PHỐI BỞI CKC DIGITAL"
        }
    }

    val displayProducts = remember(products, searchQuery, selectedCategoryKey, selectedSort) {
        var result = products

        if (selectedCategoryKey != null) {
            result = result.filter { product ->
                when (selectedCategoryKey) {
                    "FullFrame" -> product.CategoryID == 6
                    "APS-C" -> product.CategoryID == 7
                    "LensGM" -> product.CategoryID == 8
                    "LensG" -> product.CategoryID == 9
                    "PHỤ KIỆN" -> product.CategoryID == 3
                    else -> true
                }
            }
        }

        if (searchQuery.isNotBlank()) {
            result = result.filter {
                it.ProductName.contains(searchQuery.trim(), ignoreCase = true)
            }
        }

        when (selectedSort) {
            "Giá tăng dần" -> result.sortedBy { it.Price }
            "Giá giảm dần" -> result.sortedByDescending { it.Price }
            else -> result
        }
    }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val response = RetrofitClient.apiService.getProducts()
            if (response.success) {
                products = response.data
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Lỗi kết nối: ${e.message}") // Xem lỗi ở Logcat
        } finally {
            isLoading = false
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideMenuContent(onItemClick = { category ->
                selectedCategoryKey = category
                searchQuery = ""
                scope.launch { drawerState.close() }
            })
        }
    ) {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .background(Color.Black)
                        .statusBarsPadding()
                ) {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Black
                        ),
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.padding(start = 8.dp).size(40.dp)
                            ) {
                                Icon(Icons.Default.Menu, null, tint = Color.White, modifier = Modifier.size(25.dp))
                            }
                        },
                        title = {
                            AsyncImage(
                                model = "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png",
                                contentDescription = "CKC Digital Logo",
                                modifier = Modifier.height(35.dp),
                                contentScale = ContentScale.Fit
                            )
                        },
                        actions = {
                            IconButton(onClick = { onProfileClick() }) { Icon(Icons.Outlined.Person, null, tint = Color.White) }

                            BadgedBox(
                                badge = {
                                    if (CartManager.badgeCartCount > 0) {
                                        Badge(
                                            containerColor = Color.Red,
                                            contentColor = Color.White
                                        ) {
                                            Text(text = CartManager.badgeCartCount.toString())
                                        }
                                    }
                                }
                            ) {
                                IconButton(onClick = { onCartClick() }) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Giỏ hàng",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    )
                    SearchBarSection(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            if (it.isNotEmpty()) selectedCategoryKey = null
                        }
                    )
                }
            }
        ) { padding ->
            if (isLoading) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFFF5F5F5))
                ) {
                    item {
                        AsyncImage(
                            model = "https://res.cloudinary.com/dczhi464d/image/upload/v1767280023/banner_shop_cwoias.png",
                            contentDescription = "Banner",
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    stickyHeader {
                        Column(modifier = Modifier.background(Color.White)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = displayTitle,
                                    color = Color.Black,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                            FilterSection(
                                expandedSort = expanded,
                                onExpandedSortChange = { expanded = it },
                                selectedSort = selectedSort,
                                onSortSelected = { selectedSort = it },
                                isGridView = isGridView,
                                onToggleView = { isGridView = it }
                            )
                            Spacer(modifier = Modifier.height(8.dp).fillMaxWidth().background(Color(0xFFF5F5F5)))
                        }
                    }

                    if (displayProducts.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillParentMaxHeight(0.5f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("Không tìm thấy sản phẩm", color = Color.Gray)
                            }
                        }
                    } else {
                        if (isGridView) {
                            val chunkedProducts = displayProducts.chunked(2)
                            items(chunkedProducts.size) { rowIndex ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    for (product in chunkedProducts[rowIndex]) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            ProductItemCard(product, onClick = { onProductClick(it) })
                                        }
                                    }
                                    if (chunkedProducts[rowIndex].size == 1) Box(modifier = Modifier.weight(1f))
                                }
                            }
                        } else {
                            items(displayProducts.size) { index ->
                                ProductItemListCard(
                                    displayProducts[index],
                                    onBuyNowClick = { onProductClick(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItemCard(product: ProductModel, onClick: (Int) -> Unit) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(product.ProductID) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = product.ThumbnailURL ?: "https://via.placeholder.com/300",
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.ProductName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    minLines = 2,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatter.format(product.Price),
                    fontSize = 15.sp,
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.SensorType ?: "",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ProductItemListCard(product: ProductModel, onBuyNowClick: (Int) -> Unit) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp)
            .clickable { onBuyNowClick(product.ProductID) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(0.5.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.ThumbnailURL ?: "https://via.placeholder.com/300",
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.ProductName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                if (!product.SensorType.isNullOrEmpty()) {
                    Text(
                        text = product.SensorType,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatter.format(product.Price),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFD32F2F)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onBuyNowClick(product.ProductID) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D1C)),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("XEM NGAY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SearchBarSection(value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f).height(49.dp),
            placeholder = { Text("Tìm sản phẩm ...", fontSize = 14.sp) },
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(Icons.Rounded.Close, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
            singleLine = true
        )
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFCC3300), shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Search, null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun FilterSection(
    expandedSort: Boolean,
    onExpandedSortChange: (Boolean) -> Unit,
    selectedSort: String,
    onSortSelected: (String) -> Unit,
    isGridView: Boolean,
    onToggleView: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F1F1))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Sắp xếp:", fontSize = 12.sp, modifier = Modifier.padding(bottom = 2.dp))
            DropdownSelector(
                expanded = expandedSort,
                onExpandedChange = onExpandedSortChange,
                selectedOption = selectedSort,
                options = listOf("Mặc định", "Giá tăng dần", "Giá giảm dần"),
                onOptionSelected = onSortSelected
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Hiển thị:", fontSize = 12.sp, modifier = Modifier.padding(bottom = 2.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .background(Color.White, shape = RoundedCornerShape(4.dp))
                    .border(1.dp, Color.LightGray, shape = RoundedCornerShape(4.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f).fillMaxHeight()
                        .background(if (isGridView) Color(0xFFD1D1D1) else Color.Transparent)
                        .clickable { onToggleView(true) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.GridView, null, modifier = Modifier.size(16.dp), tint = if (isGridView) Color.Gray else Color.LightGray)
                }
                VerticalDivider(color = Color.LightGray, modifier = Modifier.fillMaxHeight().width(1.dp))
                Box(
                    modifier = Modifier
                        .weight(1f).fillMaxHeight()
                        .background(if (!isGridView) Color(0xFFD1D1D1) else Color.Transparent)
                        .clickable { onToggleView(false) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, null, modifier = Modifier.size(18.dp), tint = if (!isGridView) Color.Gray else Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun DropdownSelector(expanded: Boolean, onExpandedChange: (Boolean) -> Unit, selectedOption: String, options: List<String>, onOptionSelected: (String) -> Unit) {
    Box {
        Surface(
            onClick = { onExpandedChange(true) },
            modifier = Modifier.fillMaxWidth().height(35.dp),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = selectedOption, fontSize = 13.sp, maxLines = 1)
                Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, modifier = Modifier.size(18.dp))
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }, modifier = Modifier.background(Color.White)) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 14.sp) },
                    onClick = { onOptionSelected(option); onExpandedChange(false) }
                )
            }
        }
    }
}

@Composable
fun SideMenuContent(onItemClick: (String) -> Unit) {
    var isCameraExpanded by remember { mutableStateOf(false) }
    var isLensExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Text("SẢN PHẨM", modifier = Modifier.fillMaxWidth().background(Color(0xFFF1F1F1)).padding(16.dp), style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp))

        ExpandableMenuItem(
            title = "MÁY ẢNH",
            isExpanded = isCameraExpanded,
            onExpandClick = { isCameraExpanded = !isCameraExpanded },
            subMenuItems = listOf(
                "Máy ảnh Sony mirrorless Full Frame" to "FullFrame",
                "Máy ảnh Sony mirrorless APS-C" to "APS-C"
            ),
            onItemClick = onItemClick
        )
        HorizontalDivider(color = Color(0xFFEEEEEE))

        ExpandableMenuItem(
            title = "ỐNG KÍNH",
            isExpanded = isLensExpanded,
            onExpandClick = { isLensExpanded = !isLensExpanded },
            subMenuItems = listOf(
                "Ống kính G" to "LensG",
                "Ống kính G Master" to "LensGM"
            ),
            onItemClick = onItemClick
        )
        HorizontalDivider(color = Color(0xFFEEEEEE))

        Text("PHỤ KIỆN", modifier = Modifier.fillMaxWidth().clickable { onItemClick("PHỤ KIỆN") }.padding(16.dp), fontSize = 14.sp)
        HorizontalDivider(color = Color(0xFFEEEEEE))
    }
}

@Composable
fun ExpandableMenuItem(title: String, isExpanded: Boolean, onExpandClick: () -> Unit, subMenuItems: List<Pair<String, String>>, onItemClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().clickable { onExpandClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Icon(if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, tint = Color.Gray)
        }
        if (isExpanded) {
            Column(modifier = Modifier.background(Color(0xFFFAFAFA))) {
                subMenuItems.forEach { (label, value) ->
                    Text(label, modifier = Modifier.fillMaxWidth().clickable { onItemClick(value) }.padding(start = 32.dp, top = 12.dp, bottom = 12.dp), fontSize = 13.sp, color = Color.DarkGray)
                }
            }
        }
    }
}