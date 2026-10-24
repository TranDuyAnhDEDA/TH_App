package com.example.th_app2.data.db

import android.content.ContentValues
import android.database.Cursor
import com.example.th_app2.data.model.Account
import com.example.th_app2.data.model.Employee
import com.example.th_app2.data.model.UserSession

class AccountDao(private val dbHelper: DatabaseHelper) {

    /**
     * Xác thực thông tin đăng nhập từ bảng Account
     */
    fun checkLogin(username: String, pass: String): Account? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT id, username, password, role FROM ${DatabaseHelper.TABLE_ACCOUNT} WHERE username = ? AND password = ? LIMIT 1",
            arrayOf(username, pass)
        )
        var acc: Account? = null
        if (cursor.moveToFirst()) {
            acc = Account(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
            )
        }
        cursor.close()
        return acc
    }

    /**
     * Lấy thông tin hồ sơ Employee liên kết với AccountId
     */
    fun getEmployeeByAccountId(accountId: Int): Employee? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT id, code, fullname, email, phone, hourlyRate, accountId 
            FROM ${DatabaseHelper.TABLE_EMPLOYEE} 
            WHERE accountId = ? LIMIT 1
            """.trimIndent(),
            arrayOf(accountId.toString())
        )
        var emp: Employee? = null
        if (cursor.moveToFirst()) {
            emp = Employee(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                code = cursor.getString(cursor.getColumnIndexOrThrow("code")),
                fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                hourlyRate = cursor.getDouble(cursor.getColumnIndexOrThrow("hourlyRate")),
                accountId = cursor.getInt(cursor.getColumnIndexOrThrow("accountId"))
            )
        }
        cursor.close()
        return emp
    }

    /**
     * Xác thực và đóng gói toàn bộ UserSession sẵn sàng cho PrefManager
     */
    fun authenticate(username: String, pass: String): UserSession? {
        val account = checkLogin(username, pass) ?: return null

        return if (account.role.equals("ADMIN", ignoreCase = true)) {
            UserSession(
                userId = account.id,
                username = account.username,
                role = "ADMIN",
                employeeName = "Administrator"
            )
        } else {
            val emp = getEmployeeByAccountId(account.id)
            UserSession(
                userId = account.id,
                username = account.username,
                role = "EMPLOYEE",
                employeeId = emp?.id ?: -1,
                employeeCode = emp?.code ?: "",
                employeeName = emp?.fullname ?: account.username,
                hourlyRate = emp?.hourlyRate ?: 0.0
            )
        }
    }

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    fun isUsernameExists(username: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM ${DatabaseHelper.TABLE_ACCOUNT} WHERE username = ? LIMIT 1",
            arrayOf(username)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    /**
     * Tự động sinh mã nhân viên tiếp theo: NV001, NV002, NV003...
     */
    fun generateNextEmployeeCode(): String {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT code FROM ${DatabaseHelper.TABLE_EMPLOYEE} ORDER BY id DESC LIMIT 1",
            null
        )
        var nextCode = "NV001"
        if (cursor.moveToFirst()) {
            val lastCode = cursor.getString(0) ?: "NV000"
            val numPart = lastCode.filter { it.isDigit() }.toIntOrNull() ?: 0
            nextCode = String.format("NV%03d", numPart + 1)
        }
        cursor.close()
        return nextCode
    }

    /**
     * Đăng ký tài khoản nhân viên mới (kèm hồ sơ Employee)
     */
    fun registerEmployee(
        fullname: String,
        username: String,
        pass: String,
        phone: String = "",
        email: String = "",
        hourlyRate: Double = 50000.0
    ): Pair<Boolean, String> {
        if (isUsernameExists(username)) {
            return Pair(false, "Tên đăng nhập '$username' đã tồn tại!")
        }

        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            // 1. Tạo tài khoản trong Account
            val accValues = ContentValues().apply {
                put("username", username)
                put("password", pass)
                put("role", "EMPLOYEE")
            }
            val accountId = db.insert(DatabaseHelper.TABLE_ACCOUNT, null, accValues)
            if (accountId == -1L) {
                return Pair(false, "Không thể tạo tài khoản người dùng!")
            }

            // 2. Tạo hồ sơ trong Employee
            val code = generateNextEmployeeCode()
            val empValues = ContentValues().apply {
                put("code", code)
                put("fullname", fullname)
                put("email", if (email.isNotEmpty()) email else "$username@company.com")
                put("phone", phone)
                put("hourlyRate", if (hourlyRate > 0) hourlyRate else 50000.0)
                put("accountId", accountId)
            }
            val empId = db.insert(DatabaseHelper.TABLE_EMPLOYEE, null, empValues)
            if (empId == -1L) {
                return Pair(false, "Không thể tạo hồ sơ nhân viên!")
            }

            db.setTransactionSuccessful()
            return Pair(true, "Đăng ký thành công! Mã nhân viên: $code")
        } catch (e: Exception) {
            return Pair(false, "Lỗi: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }

    /**
     * Tạo tài khoản mới (Dùng cho Member 2 khi thêm nhân viên)
     */
    fun createAccount(username: String, pass: String, role: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("password", pass)
            put("role", role.uppercase())
        }
        return db.insert(DatabaseHelper.TABLE_ACCOUNT, null, values)
    }
}
