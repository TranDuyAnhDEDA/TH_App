package com.example.th_app2.ui.admin.salary

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.th_app2.data.db.DatabaseHelper
import com.example.th_app2.data.db.SalaryReportItem
import java.text.DecimalFormat

@Composable
fun SalaryManagementScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val formatter = remember { DecimalFormat("#,###") }

    var selectedMonth by remember { mutableIntStateOf(10) }
    var selectedYear by remember { mutableIntStateOf(2026) }
    var salaryList by remember { mutableStateOf<List<SalaryReportItem>>(emptyList()) }

    // Tải dữ liệu bảng lương từ CSDL
    fun reloadSalaries() {
        salaryList = dbHelper.getMonthlySalaries(selectedMonth, selectedYear)
    }

    // Tự động tải lại khi đổi tháng hoặc năm
    LaunchedEffect(selectedMonth, selectedYear) {
        reloadSalaries()
    }

    val totalCompanySalary = remember(salaryList) { salaryList.sumOf { it.totalSalary } }
    val totalOvertimeHours = remember(salaryList) { salaryList.sumOf { it.overtimeHours } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Bộ chọn tháng và năm
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    if (selectedMonth > 1) selectedMonth-- else { selectedMonth = 12; selectedYear-- }
                }
            ) {
                Text("◀")
            }

            Text(
                text = "Tháng $selectedMonth / $selectedYear",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                onClick = {
                    if (selectedMonth < 12) selectedMonth++ else { selectedMonth = 1; selectedYear++ }
                }
            ) {
                Text("▶")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Nút kích hoạt tính lương tự động từ CSDL
        Button(
            onClick = {
                val count = dbHelper.calculateMonthlySalaries(selectedMonth, selectedYear)
                Toast.makeText(context, "Đã tính lương tháng $selectedMonth cho $count nhân viên!", Toast.LENGTH_SHORT).show()
                reloadSalaries()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("⚡ Tính lương tháng $selectedMonth", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Thẻ tổng quan quỹ lương
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tổng chi lương:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "${formatter.format(totalCompanySalary)} VNĐ",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Nhân viên: ${salaryList.size} người")
                    Text("Tổng làm thêm: ${totalOvertimeHours}h OT")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chi tiết bảng lương từng nhân viên:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Danh sách bảng lương
        if (salaryList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có nhân viên nào trong CSDL.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(salaryList, key = { it.employeeId }) { item ->
                    SalaryCard(item = item, formatter = formatter)
                }
            }
        }
    }
}

// Thẻ hiển thị lương từng nhân viên
@Composable
fun SalaryCard(
    item: SalaryReportItem,
    formatter: DecimalFormat
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Tên và trạng thái
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.employeeCode,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = item.employeeName, fontWeight = FontWeight.Bold)
                }

                Surface(
                    color = if (item.status == "Đã tính") MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (item.status == "Đã tính") MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chi tiết giờ làm và lương giờ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Lương/giờ: ${formatter.format(item.hourlyRate)} đ", style = MaterialTheme.typography.bodySmall)
                Text("Giờ thường: ${item.regularHours}h", style = MaterialTheme.typography.bodySmall)
                Text("Giờ OT (x1.5): ${item.overtimeHours}h", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            // Tổng lương thực nhận
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lương thực nhận:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "${formatter.format(item.totalSalary)} VNĐ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SalaryManagementScreenPreview() {
    SalaryManagementScreen()
}
