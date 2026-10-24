package com.example.th_app2.ui.admin.employee

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat

/**
 * Model tạm thời hiển thị trên UI Quản lý Nhân viên
 * (Dễ dàng map với Model Employee từ Database của Thành viên 1)
 */
data class EmployeeUiItem(
    val id: Int = 0,
    val code: String = "",
    val fullname: String = "",
    val email: String = "",
    val phone: String = "",
    val hourlyRate: Double = 0.0
)

/**
 * GIAO DIỆN QUẢN LÝ NHÂN VIÊN (BƯỚC 2)
 * Bao gồm:
 * 1. Thanh tìm kiếm thời gian thực (Search Bar)
 * 2. Danh sách nhân viên dạng thẻ Card (Mã NV, Tên, Email, SĐT, Lương/giờ)
 * 3. Nút nổi FloatingActionButton (+) Thêm mới nhân viên
 * 4. Hộp thoại Dialog Thêm / Sửa nhân viên (kèm validation)
 * 5. Hộp thoại Dialog Xác nhận Xóa nhân viên
 */
@Composable
fun EmployeeManagementScreen(
    modifier: Modifier = Modifier
) {
    // Danh sách nhân viên mẫu ban đầu để demo và kiểm tra giao diện
    var employeeList by remember {
        mutableStateOf(
            listOf(
                EmployeeUiItem(1, "NV001", "Trần Duy Anh", "duyanh@gmail.com", "0912345678", 60000.0),
                EmployeeUiItem(2, "NV002", "Nguyễn Văn Nam", "nam@gmail.com", "0987654321", 50000.0),
                EmployeeUiItem(3, "NV003", "Lê Thị Hoa", "hoa@gmail.com", "0901234567", 75000.0)
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    
    // Quản lý trạng thái Dialog Thêm / Sửa
    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedEmployeeForEdit by remember { mutableStateOf<EmployeeUiItem?>(null) }

    // Quản lý trạng thái Dialog Xóa
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedEmployeeForDelete by remember { mutableStateOf<EmployeeUiItem?>(null) }

    // Lọc danh sách theo từ khóa tìm kiếm (Mã NV hoặc Tên NV)
    val filteredList = remember(searchQuery, employeeList) {
        if (searchQuery.isBlank()) {
            employeeList
        } else {
            employeeList.filter {
                it.fullname.contains(searchQuery, ignoreCase = true) ||
                it.code.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    selectedEmployeeForEdit = null // Chế độ Thêm mới
                    showAddEditDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(text = "➕ Thêm Nhân Viên", fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. THANH TÌM KIẾM
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Tìm kiếm theo tên hoặc mã NV...") },
                leadingIcon = { Text("🔍") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Text("✕", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tiêu đề số lượng nhân viên
            Text(
                text = "Tổng số nhân viên: ${filteredList.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. DANH SÁCH NHÂN VIÊN
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "Chưa có nhân viên nào." else "Không tìm thấy nhân viên phù hợp.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 72.dp) // Tránh che khuất bởi FAB
                ) {
                    items(filteredList, key = { it.id }) { employee ->
                        EmployeeCard(
                            employee = employee,
                            onEdit = {
                                selectedEmployeeForEdit = employee
                                showAddEditDialog = true
                            },
                            onDelete = {
                                selectedEmployeeForDelete = employee
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // 3. DIALOG THÊM / SỬA NHÂN VIÊN
    if (showAddEditDialog) {
        EmployeeFormDialog(
            employee = selectedEmployeeForEdit,
            onDismiss = { showAddEditDialog = false },
            onSave = { updatedOrNewEmp ->
                if (selectedEmployeeForEdit == null) {
                    // Thêm mới
                    employeeList = employeeList + updatedOrNewEmp.copy(
                        id = (employeeList.maxOfOrNull { it.id } ?: 0) + 1
                    )
                } else {
                    // Cập nhật
                    employeeList = employeeList.map {
                        if (it.id == updatedOrNewEmp.id) updatedOrNewEmp else it
                    }
                }
                showAddEditDialog = false
            }
        )
    }

    // 4. DIALOG XÁC NHẬN XÓA
    if (showDeleteDialog && selectedEmployeeForDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = {
                Text("Bạn có chắc chắn muốn xóa nhân viên \"${selectedEmployeeForDelete?.fullname}\" (${selectedEmployeeForDelete?.code})?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        employeeList = employeeList.filter { it.id != selectedEmployeeForDelete?.id }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xóa", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

/**
 * Thẻ hiển thị thông tin từng nhân viên
 */
@Composable
fun EmployeeCard(
    employee: EmployeeUiItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val formatter = remember { DecimalFormat("#,###") }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Hàng 1: Mã NV + Các nút hành động (Sửa, Xóa)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = employee.code,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Nút Sửa
                FilledTonalButton(
                    onClick = onEdit,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("✏️ Sửa", style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Nút Xóa
                OutlinedButton(
                    onClick = onDelete,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("🗑️ Xóa", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hàng 2: Họ và tên
            Text(
                text = employee.fullname,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Hàng 3: Email và SĐT
            Text(
                text = "✉️ ${employee.email}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "📞 ${employee.phone}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            // Hàng 4: Lương theo giờ (hourlyRate)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mức lương theo giờ:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${formatter.format(employee.hourlyRate)} đ/h",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Hộp thoại Nhập liệu Thêm / Sửa Nhân viên
 */
@Composable
fun EmployeeFormDialog(
    employee: EmployeeUiItem?,
    onDismiss: () -> Unit,
    onSave: (EmployeeUiItem) -> Unit
) {
    val isEditMode = employee != null

    var code by remember { mutableStateOf(employee?.code ?: "") }
    var fullname by remember { mutableStateOf(employee?.fullname ?: "") }
    var email by remember { mutableStateOf(employee?.email ?: "") }
    var phone by remember { mutableStateOf(employee?.phone ?: "") }
    var hourlyRateStr by remember {
        mutableStateOf(if (employee != null) employee.hourlyRate.toInt().toString() else "")
    }

    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isEditMode) "Chỉnh sửa nhân viên" else "Thêm nhân viên mới")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Mã NV
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Mã nhân viên (vd: NV001)") },
                    enabled = !isEditMode, // Không cho sửa mã khi edit
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Họ tên
                OutlinedTextField(
                    value = fullname,
                    onValueChange = { fullname = it },
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // SĐT
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số điện thoại") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Mức lương theo giờ
                OutlinedTextField(
                    value = hourlyRateStr,
                    onValueChange = { hourlyRateStr = it },
                    label = { Text("Lương theo giờ (VNĐ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rate = hourlyRateStr.toDoubleOrNull()
                    when {
                        code.isBlank() -> errorMessage = "Mã nhân viên không được để trống"
                        fullname.isBlank() -> errorMessage = "Họ tên không được để trống"
                        email.isBlank() -> errorMessage = "Email không được để trống"
                        phone.isBlank() -> errorMessage = "Số điện thoại không được để trống"
                        rate == null || rate <= 0 -> errorMessage = "Mức lương phải là số lớn hơn 0"
                        else -> {
                            onSave(
                                EmployeeUiItem(
                                    id = employee?.id ?: 0,
                                    code = code.trim(),
                                    fullname = fullname.trim(),
                                    email = email.trim(),
                                    phone = phone.trim(),
                                    hourlyRate = rate
                                )
                            )
                        }
                    }
                }
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EmployeeManagementScreenPreview() {
    EmployeeManagementScreen()
}
