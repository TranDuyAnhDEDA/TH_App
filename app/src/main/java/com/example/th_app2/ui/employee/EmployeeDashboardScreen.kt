package com.example.th_app2.ui.employee

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.th_app2.data.model.Salary
import com.example.th_app2.data.model.WorkLog
import com.example.th_app2.ui.employee.attendance.AttendanceScreen
import com.example.th_app2.ui.employee.history.WorkLogHistoryScreen
import com.example.th_app2.ui.employee.salary.PersonalSalaryScreen
import kotlinx.coroutines.launch

private enum class EmployeeTab(val title: String, val icon: ImageVector) {
    ATTENDANCE("Chấm công", Icons.Default.AccessTime),
    HISTORY("Lịch sử", Icons.Default.History),
    SALARY("Bảng lương", Icons.Default.Payments)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDashboardScreen(
    employeeName: String = "Nguyen Van An",
    employeeId: Int = 1,
    hourlyRate: Double = 50000.0,
    onLogout: () -> Unit = {},
    // DB callbacks - thay bằng hàm thật từ DatabaseHelper khi tích hợp
    onCheckIn: (Int) -> Boolean = { true },
    onCheckOut: (Int) -> Boolean = { true },
    getTodayWorkLog: (Int) -> WorkLog? = { null },
    getWorkLogs: (Int, Int, Int) -> List<WorkLog> = { _, _, _ -> emptyList() },
    getSalary: (Int, Int, Int) -> Salary? = { _, _, _ -> null }
) {
    val tabs = EmployeeTab.entries
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Xin chào, $employeeName",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Đăng xuất"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // === TAB ROW ===
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(tab.title) },
                        icon = { Icon(tab.icon, contentDescription = tab.title) }
                    )
                }
            }

            // === NỘI DUNG TỪNG TAB ===
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (tabs[page]) {
                    EmployeeTab.ATTENDANCE -> AttendanceScreen(
                        employeeId = employeeId,
                        onCheckIn = onCheckIn,
                        onCheckOut = onCheckOut,
                        getTodayWorkLog = getTodayWorkLog
                    )
                    EmployeeTab.HISTORY -> WorkLogHistoryScreen(
                        employeeId = employeeId,
                        getWorkLogs = getWorkLogs
                    )
                    EmployeeTab.SALARY -> PersonalSalaryScreen(
                        employeeId = employeeId,
                        hourlyRate = hourlyRate,
                        getSalary = getSalary
                    )
                }
            }
        }
    }
}
