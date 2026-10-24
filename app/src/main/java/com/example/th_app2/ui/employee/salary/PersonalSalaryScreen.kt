package com.example.th_app2.ui.employee.salary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.th_app2.data.model.Salary
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalSalaryScreen(
    employeeId: Int = 1,
    hourlyRate: Double = 50000.0,
    getSalary: (Int, Int, Int) -> Salary? = { _, _, _ ->
        Salary(1, 1, 10, 2026, 18.5, 2.5, 987500.0, "Pending")
    }
) {
    val calendar = Calendar.getInstance()
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) }
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var salary by remember { mutableStateOf<Salary?>(null) }

    LaunchedEffect(selectedMonth, selectedYear) {
        salary = getSalary(employeeId, selectedMonth, selectedYear)
    }

    val vndFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bảng lương Cá nhân",
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

        Spacer(modifier = Modifier.height(24.dp))

        if (salary == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Chưa có dữ liệu lương tháng $selectedMonth/$selectedYear",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val s = salary!!
            val normalHours = s.totalHours - s.overtimeHours
            val normalPay = normalHours * hourlyRate
            val overtimePay = s.overtimeHours * hourlyRate * 1.5

            // === THẺ TỔNG LƯƠNG ===
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Tổng lương thực nhận",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        vndFormat.format(s.totalSalary),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val statusColor = when (s.status) {
                        "Paid" -> MaterialTheme.colorScheme.tertiary
                        "Approved" -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outline
                    }
                    val statusText = when (s.status) {
                        "Paid" -> "Đã thanh toán"
                        "Approved" -> "Đã duyệt"
                        else -> "Chờ duyệt"
                    }
                    AssistChip(
                        onClick = {},
                        label = { Text(statusText) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = statusColor.copy(alpha = 0.15f),
                            labelColor = statusColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // === CHI TIẾT ===
            Text(
                "Chi tiết tính lương",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            SalaryDetailRow(
                icon = Icons.Default.AccessTime,
                label = "Giờ làm thường",
                value = "${String.format("%.1f", normalHours)}h"
            )
            SalaryDetailRow(
                icon = Icons.Default.MoreTime,
                label = "Giờ tăng ca (OT)",
                value = "${String.format("%.1f", s.overtimeHours)}h"
            )
            SalaryDetailRow(
                icon = Icons.Default.AttachMoney,
                label = "Mức lương / giờ",
                value = vndFormat.format(hourlyRate)
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            // === BẢNG TÍNH ===
            Text(
                "Công thức tính",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FormulaRow(
                        "Lương thường",
                        "${String.format("%.1f", normalHours)}h × ${vndFormat.format(hourlyRate)}",
                        vndFormat.format(normalPay)
                    )
                    FormulaRow(
                        "Lương OT (×1.5)",
                        "${String.format("%.1f", s.overtimeHours)}h × ${vndFormat.format(hourlyRate)} × 1.5",
                        vndFormat.format(overtimePay)
                    )
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "TỔNG CỘNG",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            vndFormat.format(s.totalSalary),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SalaryDetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun FormulaRow(label: String, formula: String, result: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(result, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            formula,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

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
