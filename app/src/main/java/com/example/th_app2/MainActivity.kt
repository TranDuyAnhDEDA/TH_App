package com.example.th_app2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.th_app2.data.db.AccountDao
import com.example.th_app2.data.db.DatabaseHelper
import com.example.th_app2.data.model.UserSession
import com.example.th_app2.data.pref.PrefManager
import com.example.th_app2.ui.admin.AdminDashboardScreen
import com.example.th_app2.ui.auth.LoginScreen
import com.example.th_app2.ui.employee.EmployeeDashboardScreen
import com.example.th_app2.ui.theme.THAPP2Theme

enum class AppDestination {
    LOGIN,
    ADMIN_DASHBOARD,
    EMPLOYEE_DASHBOARD
}

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
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRootNavigation(
                        dbHelper = dbHelper,
                        accountDao = accountDao,
                        prefManager = prefManager
                    )
                }
            }
        }
    }
}

@Composable
fun AppRootNavigation(
    dbHelper: DatabaseHelper,
    accountDao: AccountDao,
    prefManager: PrefManager
) {
    val initialDestination = remember {
        if (prefManager.isLoggedIn()) {
            val role = prefManager.getUserRole()
            if (role.equals("ADMIN", ignoreCase = true)) {
                AppDestination.ADMIN_DASHBOARD
            } else {
                AppDestination.EMPLOYEE_DASHBOARD
            }
        } else {
            AppDestination.LOGIN
        }
    }

    var currentScreen by remember { mutableStateOf(initialDestination) }
    var currentSession by remember { mutableStateOf<UserSession?>(prefManager.getSession()) }

    when (currentScreen) {
        AppDestination.LOGIN -> {
            LoginScreen(
                accountDao = accountDao,
                prefManager = prefManager,
                onLoginSuccess = { role ->
                    currentSession = prefManager.getSession()
                    currentScreen = if (role.equals("ADMIN", ignoreCase = true)) {
                        AppDestination.ADMIN_DASHBOARD
                    } else {
                        AppDestination.EMPLOYEE_DASHBOARD
                    }
                }
            )
        }

        AppDestination.ADMIN_DASHBOARD -> {
            AdminDashboardScreen(
                onLogout = {
                    prefManager.clearSession()
                    currentSession = null
                    currentScreen = AppDestination.LOGIN
                }
            )
        }

        AppDestination.EMPLOYEE_DASHBOARD -> {
            EmployeeDashboardScreen(
                session = currentSession,
                onLogout = {
                    prefManager.clearSession()
                    currentSession = null
                    currentScreen = AppDestination.LOGIN
                },
                onCheckIn = { empId -> dbHelper.doCheckIn(empId) },
                onCheckOut = { empId -> dbHelper.doCheckOut(empId) },
                getTodayWorkLog = { empId -> dbHelper.getTodayWorkLog(empId) },
                getWorkLogs = { empId, m, y -> dbHelper.getWorkLogsByEmployee(empId, m, y) },
                getSalary = { empId, m, y -> dbHelper.getSalaryByEmployee(empId, m, y) }
            )
        }
    }
}
