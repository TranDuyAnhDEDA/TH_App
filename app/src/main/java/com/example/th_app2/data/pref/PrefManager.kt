package com.example.th_app2.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.example.th_app2.data.model.UserSession

class PrefManager(context: Context) {

    private val pref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "EmployeeAppSessionPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLE = "role"
        private const val KEY_EMPLOYEE_ID = "employeeId"
        private const val KEY_EMPLOYEE_CODE = "employeeCode"
        private const val KEY_EMPLOYEE_NAME = "employeeName"
        private const val KEY_HOURLY_RATE = "hourlyRate"
    }

    /**
     * Lưu phiên đăng nhập từ đối tượng UserSession
     */
    fun saveSession(session: UserSession) {
        pref.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putInt(KEY_USER_ID, session.userId)
            putString(KEY_USERNAME, session.username)
            putString(KEY_ROLE, session.role.uppercase())
            putInt(KEY_EMPLOYEE_ID, session.employeeId)
            putString(KEY_EMPLOYEE_CODE, session.employeeCode)
            putString(KEY_EMPLOYEE_NAME, session.employeeName)
            putFloat(KEY_HOURLY_RATE, session.hourlyRate.toFloat())
            apply()
        }
    }

    /**
     * Lưu phiên đăng nhập với các tham số riêng lẻ (tương thích code cũ)
     */
    fun saveLoginSession(
        userId: Int,
        username: String,
        role: String,
        employeeId: Int = -1,
        employeeCode: String = "",
        employeeName: String = "",
        hourlyRate: Double = 0.0
    ) {
        saveSession(
            UserSession(
                userId = userId,
                username = username,
                role = role,
                employeeId = employeeId,
                employeeCode = employeeCode,
                employeeName = employeeName,
                hourlyRate = hourlyRate
            )
        )
    }

    /**
     * Lấy toàn bộ thông tin phiên làm việc hiện tại
     */
    fun getSession(): UserSession? {
        if (!isLoggedIn()) return null
        return UserSession(
            userId = pref.getInt(KEY_USER_ID, -1),
            username = pref.getString(KEY_USERNAME, "") ?: "",
            role = pref.getString(KEY_ROLE, "") ?: "",
            employeeId = pref.getInt(KEY_EMPLOYEE_ID, -1),
            employeeCode = pref.getString(KEY_EMPLOYEE_CODE, "") ?: "",
            employeeName = pref.getString(KEY_EMPLOYEE_NAME, "") ?: "",
            hourlyRate = pref.getFloat(KEY_HOURLY_RATE, 0f).toDouble()
        )
    }

    /**
     * Kiểm tra trạng thái đã đăng nhập trước đó hay chưa (Auto-login)
     */
    fun isLoggedIn(): Boolean = pref.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserRole(): String? = pref.getString(KEY_ROLE, null)
    fun getUserId(): Int = pref.getInt(KEY_USER_ID, -1)
    fun getUsername(): String? = pref.getString(KEY_USERNAME, null)
    fun getEmployeeId(): Int = pref.getInt(KEY_EMPLOYEE_ID, -1)
    fun getEmployeeCode(): String = pref.getString(KEY_EMPLOYEE_CODE, "") ?: ""
    fun getEmployeeName(): String = pref.getString(KEY_EMPLOYEE_NAME, "") ?: ""
    fun getHourlyRate(): Double = pref.getFloat(KEY_HOURLY_RATE, 0f).toDouble()

    /**
     * Xóa sạch phiên đăng nhập khi bấm Logout
     */
    fun clearSession() {
        pref.edit().clear().apply()
    }
}
