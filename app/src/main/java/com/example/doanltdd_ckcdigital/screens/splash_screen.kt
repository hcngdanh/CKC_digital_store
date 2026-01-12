package com.example.doanltdd_ckcdigital.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import coil.compose.AsyncImage

@Composable
fun SplashScreen(
    isLoading: Boolean,
    onDataReady: () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(100f) }
    var showLoading by remember { mutableStateOf(false) }

    var isTimeOutFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch {
            alpha.animateTo(1f, animationSpec = tween(1000))
        }
        launch {
            offsetY.animateTo(0f, animationSpec = tween(1000))
        }
        delay(1500)
        showLoading = true
        delay(500)
        isTimeOutFinished = true
    }


    LaunchedEffect(isLoading, isTimeOutFinished) {
        if (!isLoading && isTimeOutFinished) {
            onDataReady()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2B2B2B), Color(0xFF000000))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset { IntOffset(0, offsetY.value.toInt()) }
        ) {
            AsyncImage(
                model = "https://res.cloudinary.com/dczhi464d/image/upload/v1767096256/shoplogo_new_fi45zg.png",
                contentDescription = "Logo CKC",
                modifier = Modifier.width(130.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            AnimatedVisibility(
                visible = showLoading,
                enter = fadeIn() + expandVertically()
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}