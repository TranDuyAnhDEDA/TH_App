package com.example.th_app2.data.model

data class Salary(
    val id: Int = 0,
    val employeeId: Int,
    val month: Int,
    val year: Int,
    val totalHours: Double,
    val overtimeHours: Double,
    val totalSalary: Double,
    val status: String // "Pending", "Approved", "Paid"
)
