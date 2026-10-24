package com.example.th_app2.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.th_app2.ui.admin.employee.EmployeeManagementScreen
import com.example.th_app2.ui.admin.salary.SalaryManagementScreen

/**
 * BƯỚC 1: Khung Admin Dashboard chính
 * - TopAppBar: Tiêu đề và nút Đăng xuất (Logout)
 * - PrimaryTabRow: Chuyển đổi giữa 2 tab "Nhân viên" và "Bảng lương"
 * - Content: Hiển thị màn hình tương ứng với tab được chọn
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("👤 Quản lý Nhân viên", "💰 Quản lý Bảng lương")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Quản Trị Hệ Thống",
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    TextButton(onClick = onLogout) {
                        Text(
                            text = "Đăng xuất",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Thanh chuyển đổi Tab
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall
                            ) 
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> EmployeeManagementScreen(modifier = Modifier.fillMaxSize())
                1 -> SalaryManagementScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

