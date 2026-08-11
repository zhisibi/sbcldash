package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ZashboardApp
import com.example.ui.theme.ZashboardTheme
import com.example.ui.viewmodel.DashboardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZashboardTheme {
                val dashboardViewModel: DashboardViewModel = viewModel()
                ZashboardApp(
                    viewModel = dashboardViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
