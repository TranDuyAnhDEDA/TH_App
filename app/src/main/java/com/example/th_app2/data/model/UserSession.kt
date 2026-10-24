package com.example.th_app2.data.model

data class UserSession(
    val userId: Int,
    val username: String,
    val role: String,                 // "ADMIN" hoặc "EMPLOYEE"
    val employeeId: Int = -1,         // ID trong bảng Employee (-1 nếu là Admin)
    val employeeCode: String = "",
    val employeeName: String = "",
    val hourlyRate: Double = 0.0
)
