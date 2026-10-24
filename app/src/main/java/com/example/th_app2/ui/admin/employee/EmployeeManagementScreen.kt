package com.example.th_app2.ui.admin.employee

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.th_app2.data.db.DatabaseHelper
import com.example.th_app2.data.model.Employee
import java.text.DecimalFormat

/**
 * GIAO DIỆN QUẢN LÝ NHÂN VIÊN KẾT NỐI DATABASE THỰC TẾ
 * Chức năng:
 * 1. Hiển thị danh sách nhân viên từ CSDL SQLite
 * 2. Tìm kiếm nhân viên thời gian thực theo Tên hoặc Mã
 * 3. Thêm nhân viên mới (tự tạo tài khoản đăng nhập tương ứng)
 * 4. Cập nhật thông tin nhân viên và mức lương theo giờ
 * 5. Xóa nhân viên khỏi CSDL (kèm dialog xác nhận)
 */
@Composable
fun EmployeeManagementScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }

    var employeeList by remember { mutableStateOf<List<Employee>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    // Quản lý trạng thái Dialog Thêm / Sửa
    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedEmployeeForEdit by remember { mutableStateOf<Employee?>(null) }

    // Quản lý trạng thái Dialog Xóa
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedEmployeeForDelete by remember { mutableStateOf<Employee?>(null) }

    // Hàm tải lại dữ liệu từ Database
    fun reloadEmployees() {
        employeeList = if (searchQuery.isBlank()) {
            dbHelper.getAllEmployees()
        } else {
            dbHelper.searchEmployees(searchQuery)
        }
    }

    // Tự động load dữ liệu ban đầu
    LaunchedEffect(Unit) {
        reloadEmployees()
    }

    // Khi người dùng gõ từ khóa tìm kiếm
    LaunchedEffect(searchQuery) {
        reloadEmployees()
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
                text = "Tổng số nhân viên: ${employeeList.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. DANH SÁCH NHÂN VIÊN TỪ CSDL
            if (employeeList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "Chưa có nhân viên nào trong CSDL." else "Không tìm thấy nhân viên phù hợp.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 72.dp)
                ) {
                    items(employeeList, key = { it.id }) { employee ->
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
            onSave = { code, fullname, email, phone, rate ->
                if (selectedEmployeeForEdit == null) {
                    // Thêm mới vào SQLite
                    val result = dbHelper.insertEmployeeWithAccount(
                        code = code,
                        fullname = fullname,
                        email = email,
                        phone = phone,
                        hourlyRate = rate
                    )
                    when (result) {
                        -2L -> Toast.makeText(context, "Lỗi: Mã nhân viên \"$code\" đã tồn tại!", Toast.LENGTH_SHORT).show()
                        -3L -> Toast.makeText(context, "Lỗi: Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show()
                        -1L -> Toast.makeText(context, "Lỗi: Không thể thêm nhân viên!", Toast.LENGTH_SHORT).show()
                        else -> {
                            Toast.makeText(context, "Thêm nhân viên thành công!", Toast.LENGTH_SHORT).show()
                            showAddEditDialog = false
                            reloadEmployees()
                        }
                    }
                } else {
                    // Cập nhật thông tin vào SQLite
                    val success = dbHelper.updateEmployee(
                        id = selectedEmployeeForEdit!!.id,
                        fullname = fullname,
                        email = email,
                        phone = phone,
                        hourlyRate = rate
                    )
                    if (success) {
                        Toast.makeText(context, "Cập nhật nhân viên thành công!", Toast.LENGTH_SHORT).show()
                        showAddEditDialog = false
                        reloadEmployees()
                    } else {
                        Toast.makeText(context, "Lỗi: Cập nhật thất bại!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    // 4. DIALOG XÁC NHẬN XÓA
    if (showDeleteDialog && selectedEmployeeForDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = {
                Text("Bạn có chắc chắn muốn xóa nhân viên \"${selectedEmployeeForDelete?.fullname}\" (Mã: ${selectedEmployeeForDelete?.code})?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = dbHelper.deleteEmployee(selectedEmployeeForDelete!!.id)
                        if (success) {
                            Toast.makeText(context, "Đã xóa nhân viên thành công!", Toast.LENGTH_SHORT).show()
                            showDeleteDialog = false
                            reloadEmployees()
                        } else {
                            Toast.makeText(context, "Lỗi: Xóa nhân viên thất bại!", Toast.LENGTH_SHORT).show()
                        }
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
    employee: Employee,
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
                text = "✉️ ${employee.email ?: "Chưa có email"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "📞 ${employee.phone ?: "Chưa có SĐT"}",
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
    employee: Employee?,
    onDismiss: () -> Unit,
    onSave: (code: String, fullname: String, email: String, phone: String, hourlyRate: Double) -> Unit
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
                    label = { Text("Mã nhân viên (vd: NV003)") },
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
                            onSave(code.trim(), fullname.trim(), email.trim(), phone.trim(), rate)
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
