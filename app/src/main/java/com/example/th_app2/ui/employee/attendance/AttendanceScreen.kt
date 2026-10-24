package com.example.th_app2.ui.employee.attendance

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.th_app2.data.model.WorkLog
import com.example.th_app2.utils.TimeSalaryHelper
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    employeeId: Int = 1,
    onCheckIn: (Int) -> Boolean = { true },
    onCheckOut: (Int) -> Boolean = { true },
    getTodayWorkLog: (Int) -> WorkLog? = { null }
) {
    // Đồng hồ thời gian thực
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance()
            delay(1000L)
        }
    }

    // Trạng thái chấm công hôm nay
    var todayLog by remember { mutableStateOf(getTodayWorkLog(employeeId)) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    val attendanceStatus = when {
        todayLog == null -> AttendanceStatus.NOT_STARTED
        todayLog?.status == "In Progress" -> AttendanceStatus.WORKING
        todayLog?.status == "Completed" -> AttendanceStatus.COMPLETED
        else -> AttendanceStatus.NOT_STARTED
    }

    val statusColor by animateColorAsState(
        targetValue = when (attendanceStatus) {
            AttendanceStatus.NOT_STARTED -> MaterialTheme.colorScheme.surfaceVariant
            AttendanceStatus.WORKING -> MaterialTheme.colorScheme.primaryContainer
            AttendanceStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
        },
        label = "statusColor"
    )

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // === NGÀY HIỆN TẠI ===
            Text(
                text = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("vi")).format(currentTime.time),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // === ĐỒNG HỒ THỜI GIAN THỰC ===
            Text(
                text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(currentTime.time),
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // === THẺ TRẠNG THÁI ===
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = statusColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (attendanceStatus) {
                            AttendanceStatus.NOT_STARTED -> "Chưa vào ca"
                            AttendanceStatus.WORKING -> "Đang làm việc"
                            AttendanceStatus.COMPLETED -> "Đã hoàn thành ca"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (todayLog != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Giờ vào",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    todayLog?.checkInTime?.substring(0, 5) ?: "--:--",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Giờ ra",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    todayLog?.checkOutTime?.substring(0, 5) ?: "--:--",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (todayLog?.totalHours != null && todayLog?.totalHours != 0.0) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "Tổng giờ",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        String.format("%.1fh", todayLog?.totalHours),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // === NÚT CHECK-IN ===
            Button(
                onClick = {
                    if (attendanceStatus != AttendanceStatus.NOT_STARTED) {
                        snackbarMessage = "Bạn đã check-in hôm nay rồi!"
                        showSnackbar = true
                        return@Button
                    }
                    val success = onCheckIn(employeeId)
                    if (success) {
                        val now = TimeSalaryHelper.getCurrentTime()
                        val today = TimeSalaryHelper.getCurrentDate()
                        todayLog = WorkLog(
                            employeeId = employeeId,
                            workDate = today,
                            checkInTime = now,
                            checkOutTime = null,
                            status = "In Progress"
                        )
                        snackbarMessage = "Check-in thành công lúc $now"
                    } else {
                        snackbarMessage = "Check-in thất bại!"
                    }
                    showSnackbar = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = attendanceStatus == AttendanceStatus.NOT_STARTED,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Login, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("CHECK-IN", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // === NÚT CHECK-OUT ===
            Button(
                onClick = {
                    if (attendanceStatus != AttendanceStatus.WORKING) {
                        snackbarMessage = if (attendanceStatus == AttendanceStatus.NOT_STARTED)
                            "Bạn chưa check-in hôm nay!"
                        else
                            "Bạn đã check-out rồi!"
                        showSnackbar = true
                        return@Button
                    }
                    val success = onCheckOut(employeeId)
                    if (success) {
                        val now = TimeSalaryHelper.getCurrentTime()
                        val checkIn = todayLog?.checkInTime ?: return@Button
                        val totalH = TimeSalaryHelper.calculateWorkingHours(checkIn, now)
                        val today = TimeSalaryHelper.getCurrentDate()
                        todayLog = WorkLog(
                            employeeId = employeeId,
                            workDate = today,
                            checkInTime = checkIn,
                            checkOutTime = now,
                            totalHours = totalH,
                            status = "Completed"
                        )
                        snackbarMessage = "Check-out thành công! Tổng: ${String.format("%.1f", totalH)}h"
                    } else {
                        snackbarMessage = "Check-out thất bại!"
                    }
                    showSnackbar = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = attendanceStatus == AttendanceStatus.WORKING,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("CHECK-OUT", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // === GHI CHÚ ===
            if (attendanceStatus == AttendanceStatus.COMPLETED) {
                Text(
                    text = "Bạn đã hoàn thành ca làm việc hôm nay.\nHẹn gặp lại ngày mai!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private enum class AttendanceStatus {
    NOT_STARTED, WORKING, COMPLETED
}
