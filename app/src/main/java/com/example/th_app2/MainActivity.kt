package com.example.th_app2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.th_app2.data.db.AccountDao
import com.example.th_app2.data.db.DatabaseHelper
import com.example.th_app2.data.pref.PrefManager
import com.example.th_app2.ui.admin.AdminDashboardScreen
import com.example.th_app2.ui.auth.LoginScreen
import com.example.th_app2.ui.theme.THAPP2Theme

class MainActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var accountDao: AccountDao
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Khởi tạo các thành phần Core Lead (Thành viên 1)
        dbHelper = DatabaseHelper(this)
        accountDao = AccountDao(dbHelper)
        prefManager = PrefManager(this)

        setContent {
            THAPP2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F172A)
                ) {
                    var currentRole by remember {
                        mutableStateOf(if (prefManager.isLoggedIn()) prefManager.getUserRole() else null)
                    }

                    if (currentRole.equals("ADMIN", ignoreCase = true)) {
                        AdminDashboardScreen(
                            onLogout = {
                                prefManager.clearSession()
                                currentRole = null
                            }
                        )
                    } else {
                        LoginScreen(
                            accountDao = accountDao,
                            prefManager = prefManager,
                            onLoginSuccess = { role ->
                                if (role.equals("ADMIN", ignoreCase = true)) {
                                    currentRole = "ADMIN"
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Đăng nhập thành công vai trò: $role (Chờ module Thành viên 3)",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}