package com.example.th_app2.data.model

// TODO: Xóa file này khi Thành viên 1 cung cấp model chính thức

data class WorkLog(
    val id: Int = 0,
    val employeeId: Int,
    val date: String,           // "YYYY-MM-DD"
    val checkInTime: String?,   // "HH:mm:ss"
    val checkOutTime: String?,  // "HH:mm:ss"
    val totalHours: Double?,
    val status: String          // "CHECKED_IN", "COMPLETED"
)

data class Salary(
    val id: Int = 0,
    val employeeId: Int,
    val month: Int,
    val year: Int,
    val totalHours: Double,
    val overtimeHours: Double,
    val hourlyRate: Double,
    val totalSalary: Double
)

data class Employee(
    val id: Int = 0,
    val employeeCode: String,
    val name: String,
    val email: String,
    val phone: String,
    val hourlyRate: Double,
    val accountId: Int
)
