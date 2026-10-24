package com.example.th_app2.data.model

data class WorkLog(
    val id: Int = 0,
    val employeeId: Int,
    val workDate: String,            // Định dạng: yyyy-MM-dd
    val checkInTime: String,         // Định dạng: HH:mm
    val checkOutTime: String? = null,// Định dạng: HH:mm (null nếu đang trong ca)
    val totalHours: Double = 0.0,    // Số giờ làm tính toán tự động
    val status: String               // "In Progress", "Completed"
)
