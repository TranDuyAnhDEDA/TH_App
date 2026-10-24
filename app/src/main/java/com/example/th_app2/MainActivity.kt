package com.example.th_app2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.th_app2.ui.admin.AdminDashboardScreen
import com.example.th_app2.ui.theme.THAPP2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            THAPP2Theme {
                AdminDashboardScreen(
                    onLogout = {

                    }
                )
            }
        }
    }
}