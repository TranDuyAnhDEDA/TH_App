package com.example.th_app2.ui.employee.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.th_app2.data.model.WorkLog
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkLogHistoryScreen(
    employeeId: Int = 1,
    getWorkLogs: (Int, Int, Int) -> List<WorkLog> = { _, _, _ ->
        listOf(
            WorkLog(1, 1, "2026-10-22", "08:00", "17:30", 9.5, "Completed"),
            WorkLog(2, 1, "2026-10-23", "08:30", "17:30", 9.0, "Completed"),
            WorkLog(3, 1, "2026-10-24", "08:30", null, 0.0, "In Progress")
        )
    }
) {
    val calendar = Calendar.getInstance()
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) }
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var workLogs by remember { mutableStateOf<List<WorkLog>>(emptyList()) }

    LaunchedEffect(selectedMonth, selectedYear) {
        workLogs = getWorkLogs(employeeId, selectedMonth, selectedYear)
    }

    val totalDays = workLogs.size
    val totalHours = workLogs.sumOf { it.totalHours }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lịch sử Điểm danh",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // === BỘ LỌC THÁNG / NĂM ===
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MonthDropdown(
                selected = selectedMonth,
                onSelected = { selectedMonth = it },
                modifier = Modifier.weight(1f)
            )
            YearDropdown(
                selected = selectedYear,
                onSelected = { selectedYear = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // === THẺ TÓM TẮT ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Tổng ngày",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "$totalDays ngày",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Tổng giờ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "${String.format("%.1f", totalHours)}h",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // === DANH SÁCH WORKLOG ===
        if (workLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Không có dữ liệu tháng $selectedMonth/$selectedYear",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(workLogs) { log ->
                    WorkLogItem(log)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

// === CARD MỖI NGÀY ===
@Composable
private fun WorkLogItem(workLog: WorkLog) {
    val isCompleted = workLog.status == "Completed"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatDate(workLog.workDate),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (isCompleted) "Hoàn thành" else "Đang làm",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    leadingIcon = {
                        Icon(
                            if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isCompleted)
                            MaterialTheme.colorScheme.tertiaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Giờ vào",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        workLog.checkInTime,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Giờ ra",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        workLog.checkOutTime ?: "--:--",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Tổng giờ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        if (isCompleted) "${String.format("%.1f", workLog.totalHours)}h" else "--",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// === DROPDOWN THÁNG ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthDropdown(
    selected: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "Tháng $selected",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            (1..12).forEach { month ->
                DropdownMenuItem(
                    text = { Text("Tháng $month") },
                    onClick = {
                        onSelected(month)
                        expanded = false
                    }
                )
            }
        }
    }
}

// === DROPDOWN NĂM ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YearDropdown(
    selected: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "Năm $selected",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            (currentYear - 2..currentYear).forEach { year ->
                DropdownMenuItem(
                    text = { Text("Năm $year") },
                    onClick = {
                        onSelected(year)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun formatDate(dateStr: String): String {
    val parts = dateStr.split("-")
    if (parts.size != 3) return dateStr
    return "${parts[2]}/${parts[1]}/${parts[0]}"
}
