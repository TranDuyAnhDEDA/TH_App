# PHẦN VIỆC CỦA THÀNH VIÊN 1: MODELS / ENTITIES
Khai báo 4 data class tương ứng với 4 bảng:
1. Account(id, username, password, role)
2. Employee(id, code, fullname, email, phone, hourlyRate, accountId)
3. WorkLog(id, employeeId, workDate, checkInTime, checkOutTime, totalHours, status)
4. Salary(id, employeeId, month, year, totalHours, overtimeHours, totalSalary, status)
