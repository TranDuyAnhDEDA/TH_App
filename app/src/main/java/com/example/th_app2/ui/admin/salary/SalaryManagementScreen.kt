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
import java.text.DecimalFormat

// Model hiển thị bảng lương
data class SalaryUiItem(
    val employeeCode: String,
    val employeeName: String,
    val hourlyRate: Double,
    val regularHours: Double,
    val overtimeHours: Double,
    val totalSalary: Double,
    val status: String
)

@Composable
fun SalaryManagementScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formatter = remember { DecimalFormat("#,###") }

    var selectedMonth by remember { mutableIntStateOf(10) }
    var selectedYear by remember { mutableIntStateOf(2026) }

    // Dữ liệu mẫu ban đầu
    var salaryList by remember {
        mutableStateOf(
            listOf(
                SalaryUiItem("NV001", "Nguyen Van An", 50000.0, 16.0, 2.5, 987500.0, "Đã tính"),
                SalaryUiItem("NV002", "Tran Thi Binh", 60000.0, 8.0, 1.0, 570000.0, "Đã tính"),
                SalaryUiItem("NV003", "Le Thi Hoa", 75000.0, 8.0, 0.0, 600000.0, "Đã tính")
            )
        )
    }

    val totalCompanySalary = remember(salaryList) { salaryList.sumOf { it.totalSalary } }
    val totalOvertimeHours = remember(salaryList) { salaryList.sumOf { it.overtimeHours } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chọn tháng và năm
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

        // Nút tính lương
        Button(
            onClick = {
                Toast.makeText(context, "Đã kích hoạt tính lương Tháng $selectedMonth/$selectedYear!", Toast.LENGTH_SHORT).show()
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
                Text("Tổng quỹ lương tháng:", style = MaterialTheme.typography.bodyMedium)
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

        // Danh sách lương từng người
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(salaryList) { item ->
                SalaryCard(item = item, formatter = formatter)
            }
        }
    }
}

// Thẻ bảng lương từng nhân viên
@Composable
fun SalaryCard(
    item: SalaryUiItem,
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
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
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

            // Tổng thực nhận
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
