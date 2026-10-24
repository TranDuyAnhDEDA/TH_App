package com.example.th_app2.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.th_app2.data.model.Employee

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "EmployeeManagement.db"
        const val DATABASE_VERSION = 1

        // Tên các bảng
        const val TABLE_ACCOUNT = "Account"
        const val TABLE_EMPLOYEE = "Employee"
        const val TABLE_WORKLOG = "WorkLog"
        const val TABLE_SALARY = "Salary"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Bảng Account (id, username, password, role)
        db.execSQL("""
            CREATE TABLE $TABLE_ACCOUNT (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL
            )
        """.trimIndent())

        // 2. Bảng Employee (id, code, fullname, email, phone, hourlyRate, accountId)
        db.execSQL("""
            CREATE TABLE $TABLE_EMPLOYEE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                code TEXT UNIQUE NOT NULL,
                fullname TEXT NOT NULL,
                email TEXT,
                phone TEXT,
                hourlyRate REAL NOT NULL,
                accountId INTEGER,
                FOREIGN KEY (accountId) REFERENCES $TABLE_ACCOUNT(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 3. Bảng WorkLog (id, employeeId, workDate, checkInTime, checkOutTime, totalHours, status)
        db.execSQL("""
            CREATE TABLE $TABLE_WORKLOG (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                employeeId INTEGER NOT NULL,
                workDate TEXT NOT NULL,
                checkInTime TEXT NOT NULL,
                checkOutTime TEXT,
                totalHours REAL DEFAULT 0.0,
                status TEXT NOT NULL,
                FOREIGN KEY (employeeId) REFERENCES $TABLE_EMPLOYEE(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 4. Bảng Salary (id, employeeId, month, year, totalHours, overtimeHours, totalSalary, status)
        db.execSQL("""
            CREATE TABLE $TABLE_SALARY (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                employeeId INTEGER NOT NULL,
                month INTEGER NOT NULL,
                year INTEGER NOT NULL,
                totalHours REAL NOT NULL,
                overtimeHours REAL NOT NULL,
                totalSalary REAL NOT NULL,
                status TEXT NOT NULL,
                FOREIGN KEY (employeeId) REFERENCES $TABLE_EMPLOYEE(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // Tự động nạp dữ liệu mẫu ban đầu (T1.2)
        seedSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SALARY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKLOG")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EMPLOYEE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNT")
        onCreate(db)
    }

    /**
     * Nạp dữ liệu ban đầu cho hệ thống (T1.2):
     * - 1 Tài khoản Admin: admin / 123456
     * - 2 Tài khoản & Hồ sơ Nhân viên: emp1 / 123456 (NV001) và emp2 / 123456 (NV002)
     * - 3 bản ghi WorkLog mẫu trong tháng 10/2026
     * - 1 bản ghi Salary mẫu
     */
    fun seedSampleData(db: SQLiteDatabase) {
        // 1. Admin Account
        val adminAccount = ContentValues().apply {
            put("username", "admin")
            put("password", "123456")
            put("role", "ADMIN")
        }
        db.insert(TABLE_ACCOUNT, null, adminAccount)

        // 2. Employee 1 (emp1)
        val empAccount1 = ContentValues().apply {
            put("username", "emp1")
            put("password", "123456")
            put("role", "EMPLOYEE")
        }
        val empAccountId1 = db.insert(TABLE_ACCOUNT, null, empAccount1)

        val empProfile1 = ContentValues().apply {
            put("code", "NV001")
            put("fullname", "Nguyen Van An")
            put("email", "an.nguyen@company.com")
            put("phone", "0901234567")
            put("hourlyRate", 50000.0) // 50,000 VND / giờ
            put("accountId", empAccountId1)
        }
        val empId1 = db.insert(TABLE_EMPLOYEE, null, empProfile1)

        // 3. Employee 2 (emp2)
        val empAccount2 = ContentValues().apply {
            put("username", "emp2")
            put("password", "123456")
            put("role", "EMPLOYEE")
        }
        val empAccountId2 = db.insert(TABLE_ACCOUNT, null, empAccount2)

        val empProfile2 = ContentValues().apply {
            put("code", "NV002")
            put("fullname", "Tran Thi Binh")
            put("email", "binh.tran@company.com")
            put("phone", "0912345678")
            put("hourlyRate", 60000.0) // 60,000 VND / giờ
            put("accountId", empAccountId2)
        }
        val empId2 = db.insert(TABLE_EMPLOYEE, null, empProfile2)

        // 4. 3 bản ghi WorkLog mẫu tháng hiện tại
        // Bản ghi 1: NV001 ngày 2026-10-22
        val workLog1 = ContentValues().apply {
            put("employeeId", empId1)
            put("workDate", "2026-10-22")
            put("checkInTime", "08:00")
            put("checkOutTime", "17:30")
            put("totalHours", 9.5)
            put("status", "Completed")
        }
        db.insert(TABLE_WORKLOG, null, workLog1)

        // Bản ghi 2: NV001 ngày 2026-10-23
        val workLog2 = ContentValues().apply {
            put("employeeId", empId1)
            put("workDate", "2026-10-23")
            put("checkInTime", "08:30")
            put("checkOutTime", "17:30")
            put("totalHours", 9.0)
            put("status", "Completed")
        }
        db.insert(TABLE_WORKLOG, null, workLog2)

        // Bản ghi 3: NV002 ngày 2026-10-23
        val workLog3 = ContentValues().apply {
            put("employeeId", empId2)
            put("workDate", "2026-10-23")
            put("checkInTime", "08:00")
            put("checkOutTime", "17:00")
            put("totalHours", 9.0)
            put("status", "Completed")
        }
        db.insert(TABLE_WORKLOG, null, workLog3)

        // 5. Bản ghi Salary mẫu
        val salarySample = ContentValues().apply {
            put("employeeId", empId1)
            put("month", 10)
            put("year", 2026)
            put("totalHours", 18.5)
            put("overtimeHours", 2.5)
            put("totalSalary", 987500.0) // 16h*50k + 2.5h*50k*1.5
            put("status", "Pending")
        }
        db.insert(TABLE_SALARY, null, salarySample)
    }

    // ==========================================================
    // CÁC HÀM CRUD QUẢN LÝ NHÂN VIÊN (EMPLOYEE MANAGEMENT)
    // ==========================================================

    /**
     * Lấy toàn bộ danh sách nhân viên từ CSDL
     */
    fun getAllEmployees(): List<Employee> {
        val list = mutableListOf<Employee>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, code, fullname, email, phone, hourlyRate, accountId FROM $TABLE_EMPLOYEE ORDER BY id DESC",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Employee(
                        id = it.getInt(0),
                        code = it.getString(1),
                        fullname = it.getString(2),
                        email = if (it.isNull(3)) "" else it.getString(3),
                        phone = if (it.isNull(4)) "" else it.getString(4),
                        hourlyRate = it.getDouble(5),
                        accountId = if (it.isNull(6)) null else it.getInt(6)
                    )
                )
            }
        }
        return list
    }

    /**
     * Tìm kiếm nhân viên theo Tên hoặc Mã nhân viên
     */
    fun searchEmployees(keyword: String): List<Employee> {
        if (keyword.isBlank()) return getAllEmployees()
        val list = mutableListOf<Employee>()
        val db = readableDatabase
        val queryPattern = "%${keyword.trim()}%"
        val cursor = db.rawQuery(
            """
            SELECT id, code, fullname, email, phone, hourlyRate, accountId 
            FROM $TABLE_EMPLOYEE 
            WHERE fullname LIKE ? OR code LIKE ? 
            ORDER BY id DESC
            """.trimIndent(),
            arrayOf(queryPattern, queryPattern)
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Employee(
                        id = it.getInt(0),
                        code = it.getString(1),
                        fullname = it.getString(2),
                        email = if (it.isNull(3)) "" else it.getString(3),
                        phone = if (it.isNull(4)) "" else it.getString(4),
                        hourlyRate = it.getDouble(5),
                        accountId = if (it.isNull(6)) null else it.getInt(6)
                    )
                )
            }
        }
        return list
    }

    /**
     * Thêm mới nhân viên đồng thời tự động tạo tài khoản đăng nhập (Account)
     * Trả về:
     *  > 0 : Thành công (trả về employeeId)
     *  -2  : Mã nhân viên đã tồn tại
     *  -3  : Tên đăng nhập (username) đã tồn tại
     *  -1  : Lỗi thêm dữ liệu
     */
    fun insertEmployeeWithAccount(
        code: String,
        fullname: String,
        email: String,
        phone: String,
        hourlyRate: Double,
        username: String = "",
        password: String = "123456"
    ): Long {
        val db = writableDatabase
        db.beginTransaction()
        try {
            // Kiểm tra mã nhân viên đã tồn tại chưa
            val checkCursor = db.rawQuery("SELECT id FROM $TABLE_EMPLOYEE WHERE code = ?", arrayOf(code.trim()))
            val codeExists = checkCursor.moveToFirst()
            checkCursor.close()
            if (codeExists) return -2L

            // Tạo tài khoản cho nhân viên
            val accUsername = if (username.isNotBlank()) username.trim() else code.trim().lowercase()
            val accValues = ContentValues().apply {
                put("username", accUsername)
                put("password", password)
                put("role", "EMPLOYEE")
            }
            val accId = db.insert(TABLE_ACCOUNT, null, accValues)
            if (accId == -1L) return -3L

            // Tạo hồ sơ nhân viên liên kết với accountId
            val empValues = ContentValues().apply {
                put("code", code.trim())
                put("fullname", fullname.trim())
                put("email", email.trim())
                put("phone", phone.trim())
                put("hourlyRate", hourlyRate)
                put("accountId", accId)
            }
            val empId = db.insert(TABLE_EMPLOYEE, null, empValues)
            if (empId != -1L) {
                db.setTransactionSuccessful()
            }
            return empId
        } finally {
            db.endTransaction()
        }
    }

    /**
     * Cập nhật thông tin nhân viên và mức lương theo giờ
     */
    fun updateEmployee(
        id: Int,
        fullname: String,
        email: String,
        phone: String,
        hourlyRate: Double
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("fullname", fullname.trim())
            put("email", email.trim())
            put("phone", phone.trim())
            put("hourlyRate", hourlyRate)
        }
        val rowsAffected = db.update(TABLE_EMPLOYEE, values, "id = ?", arrayOf(id.toString()))
        return rowsAffected > 0
    }

    /**
     * Xóa nhân viên theo ID
     */
    fun deleteEmployee(id: Int): Boolean {
        val db = writableDatabase
        var accountId: Int? = null
        val cursor = db.rawQuery("SELECT accountId FROM $TABLE_EMPLOYEE WHERE id = ?", arrayOf(id.toString()))
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            accountId = cursor.getInt(0)
        }
        cursor.close()

        val rowsAffected = db.delete(TABLE_EMPLOYEE, "id = ?", arrayOf(id.toString()))
        if (accountId != null) {
            db.delete(TABLE_ACCOUNT, "id = ?", arrayOf(accountId.toString()))
        }
        return rowsAffected > 0
    }

    // ==========================================================
    // CÁC HÀM QUẢN LÝ VÀ TÍNH LƯƠNG (SALARY MANAGEMENT)
    // ==========================================================

    /**
     * Lấy báo cáo lương theo tháng và năm
     */
    fun getMonthlySalaries(month: Int, year: Int): List<SalaryReportItem> {
        val list = mutableListOf<SalaryReportItem>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT e.id, e.code, e.fullname, e.hourlyRate, 
                   COALESCE(s.totalHours, 0.0), 
                   COALESCE(s.overtimeHours, 0.0), 
                   COALESCE(s.totalSalary, 0.0), 
                   COALESCE(s.status, 'Chưa tính')
            FROM $TABLE_EMPLOYEE e
            LEFT JOIN $TABLE_SALARY s ON e.id = s.employeeId AND s.month = ? AND s.year = ?
            ORDER BY e.id DESC
            """.trimIndent(),
            arrayOf(month.toString(), year.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                val totalH = it.getDouble(4)
                val otH = it.getDouble(5)
                val regH = if (totalH >= otH) totalH - otH else totalH
                list.add(
                    SalaryReportItem(
                        employeeId = it.getInt(0),
                        employeeCode = it.getString(1),
                        employeeName = it.getString(2),
                        hourlyRate = it.getDouble(3),
                        totalHours = totalH,
                        overtimeHours = otH,
                        regularHours = regH,
                        totalSalary = it.getDouble(6),
                        status = it.getString(7)
                    )
                )
            }
        }
        return list
    }

    /**
     * Tự động tính toán lương cho tất cả nhân viên theo tháng và năm
     * Công thức: Lương = (Giờ thường * hourlyRate) + (Giờ OT * hourlyRate * 1.5)
     */
    fun calculateMonthlySalaries(month: Int, year: Int): Int {
        val employees = getAllEmployees()
        val db = writableDatabase
        val monthStr = if (month < 10) "0$month" else "$month"
        val datePrefix = "$year-$monthStr-%"
        var calculatedCount = 0

        db.beginTransaction()
        try {
            for (emp in employees) {
                // Tính giờ làm từ WorkLog
                val logCursor = db.rawQuery(
                    "SELECT totalHours FROM $TABLE_WORKLOG WHERE employeeId = ? AND workDate LIKE ?",
                    arrayOf(emp.id.toString(), datePrefix)
                )
                var totalRegularHours = 0.0
                var totalOtHours = 0.0

                logCursor.use {
                    while (it.moveToNext()) {
                        val hours = it.getDouble(0)
                        if (hours <= 8.0) {
                            totalRegularHours += hours
                        } else {
                            totalRegularHours += 8.0
                            totalOtHours += (hours - 8.0)
                        }
                    }
                }

                val totalHours = totalRegularHours + totalOtHours
                val salaryAmount = (totalRegularHours * emp.hourlyRate) + (totalOtHours * emp.hourlyRate * 1.5)

                // Kiểm tra xem đã có bản ghi lương tháng này chưa
                val checkCursor = db.rawQuery(
                    "SELECT id FROM $TABLE_SALARY WHERE employeeId = ? AND month = ? AND year = ?",
                    arrayOf(emp.id.toString(), month.toString(), year.toString())
                )
                val exists = checkCursor.moveToFirst()
                checkCursor.close()

                val values = ContentValues().apply {
                    put("employeeId", emp.id)
                    put("month", month)
                    put("year", year)
                    put("totalHours", totalHours)
                    put("overtimeHours", totalOtHours)
                    put("totalSalary", salaryAmount)
                    put("status", "Đã tính")
                }

                if (exists) {
                    db.update(
                        TABLE_SALARY,
                        values,
                        "employeeId = ? AND month = ? AND year = ?",
                        arrayOf(emp.id.toString(), month.toString(), year.toString())
                    )
                } else {
                    db.insert(TABLE_SALARY, null, values)
                }
                calculatedCount++
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return calculatedCount
    }
}

data class SalaryReportItem(
    val employeeId: Int,
    val employeeCode: String,
    val employeeName: String,
    val hourlyRate: Double,
    val totalHours: Double,
    val overtimeHours: Double,
    val regularHours: Double,
    val totalSalary: Double,
    val status: String
)
