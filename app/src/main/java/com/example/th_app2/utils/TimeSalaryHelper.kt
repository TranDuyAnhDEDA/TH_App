package com.example.th_app2.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round

object TimeSalaryHelper {
    private const val DATE_FORMAT = "yyyy-MM-dd"
    private const val TIME_FORMAT = "HH:mm"
    const val OVERTIME_MULTIPLIER = 1.5 // Hệ số nhân lương làm thêm giờ (150%)
    const val STANDARD_DAILY_HOURS = 8.0 // Giờ chuẩn mỗi ngày

    fun getCurrentDate(): String {
        return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
    }

    fun getCurrentTime(): String {
        return SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(Date())
    }

    // Kiểm tra giờ Check-out có sau giờ Check-in hay không (Data Validation)
    fun isValidCheckOut(checkInTime: String, checkOutTime: String): Boolean {
        return try {
            val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            val inTime = sdf.parse(checkInTime)
            val outTime = sdf.parse(checkOutTime)
            inTime != null && outTime != null && outTime.after(inTime)
        } catch (e: Exception) {
            false
        }
    }

    // Tự động tính số giờ làm việc (làm tròn 2 chữ số thập phân)
    fun calculateWorkingHours(checkInTime: String, checkOutTime: String): Double {
        return try {
            val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            val inTime = sdf.parse(checkInTime)
            val outTime = sdf.parse(checkOutTime)
            if (inTime != null && outTime != null && outTime.after(inTime)) {
                val diffMillis = outTime.time - inTime.time
                val hours = diffMillis.toDouble() / (1000 * 60 * 60)
                round(hours * 100) / 100.0
            } else {
                0.0
            }
        } catch (e: Exception) {
            0.0
        }
    }

    // Tính tổng lương tháng dựa trên giờ làm bình thường, giờ tăng ca và lương theo giờ
    fun calculateTotalSalary(regularHours: Double, overtimeHours: Double, hourlyRate: Double): Double {
        val regularPay = regularHours * hourlyRate
        val overtimePay = overtimeHours * hourlyRate * OVERTIME_MULTIPLIER
        return round((regularPay + overtimePay) * 100) / 100.0
    }
}
