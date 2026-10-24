package com.example.th_app2.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
}
