package com.example.doanltdd_ckcdigital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.doanltdd_ckcdigital.navigation.AppNavGraph
import com.example.doanltdd_ckcdigital.ui.theme.DoAnLTDD_CKCDigitalTheme // Tên theme dự án của bạn

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoAnLTDD_CKCDigitalTheme {
                AppNavGraph()
            }
        }
    }
}