# EMPLOYEE WORKING HOURS AND SALARY MANAGEMENT

Dự án ứng dụng Android (Jetpack Compose + Kotlin) quản lý chấm công, tính giờ làm việc và tính toán tiền lương theo giờ cho nhân viên, hỗ trợ tính làm thêm giờ (Overtime) và phân quyền chặt chẽ giữa Quản trị viên (Admin) và Nhân viên (Employee).

> **Tiêu chí chấm điểm quan trọng:** Điểm số tính theo thời gian hoàn thành (hoàn thành càng nhanh, điểm càng cao). Dự án đã được thiết lập sẵn cấu trúc module độc lập để 3 thành viên code song song mà không bị xung đột Git.

---

## 1. YÊU CẦU ĐỀ BÀI & CƠ SỞ DỮ LIỆU

### 1.1. Bốn bảng CSDL bắt buộc (SQLite / Room)
1. **`Account`**: `(id, username, password, role)` — Role gồm `ADMIN` và `EMPLOYEE`.
2. **`Employee`**: `(id, code, fullname, email, phone, hourlyRate, accountId)` — Khóa ngoại liên kết tới `Account(id)`.
3. **`WorkLog`**: `(id, employeeId, workDate, checkInTime, checkOutTime, totalHours, status)` — Ghi nhận ca làm việc.
4. **`Salary`**: `(id, employeeId, month, year, totalHours, overtimeHours, totalSalary, status)` — Bảng lương tổng hợp theo tháng.

### 1.2. Các tính năng nghiệp vụ cốt lõi
- **Đăng nhập & Phân quyền**: Đăng nhập với 2 vai trò Admin và Employee, chuyển hướng màn hình tương ứng.
- **Lưu phiên đăng nhập**: Sử dụng `SharedPreferences` để lưu trạng thái đăng nhập, tự động vào app khi mở lại và hỗ trợ Logout.
- **Quản lý Nhân viên (Admin)**: Thêm, Sửa, Xóa, Tìm kiếm theo tên/mã, xem danh sách kèm mức lương theo giờ (`hourlyRate`).
- **Chấm công hàng ngày (Employee)**: Check-in và Check-out ghi nhận ngày, giờ vào, giờ ra.
- **Tự động tính giờ làm**: $\text{totalHours} = \text{checkOut} - \text{checkIn}$ (tính theo giờ).
- **Tự động tính lương & Overtime**: Lương = $(Tổng\ giờ \times hourlyRate) + (Giờ\ OT \times hourlyRate \times 1.5)$.
- **Quản lý lương**: Nhân viên xem bảng lương cá nhân; Admin xem và kích hoạt tính lương toàn công ty.
- **Lịch sử chấm công**: Nhân viên xem danh sách các ngày đã làm việc và tổng số giờ làm theo tháng.
- **Ràng buộc dữ liệu (Validation)**: Chống check-in trùng lặp (không cho check-in 2 lần/ngày khi chưa kết thúc ca), chặn check-out khi chưa check-in, check-out time phải sau check-in time.

---

## 2. THƯ VIỆN & CÔNG NGHỆ ĐÃ CÀI ĐẶT

- **Ngôn ngữ & UI**: Kotlin + Jetpack Compose + Material 3.
- **Navigation**: `androidx.navigation:navigation-compose` (Đã cấu hình trong Gradle).
- **Icons**: `androidx.compose.material:material-icons-extended` (Đã cấu hình trong Gradle).
- **Lưu trữ CSDL**: `SQLiteOpenHelper` (Android SDK tích hợp sẵn - không phụ thuộc plugin ngoài, build nhanh tức thì).
- **Lưu trữ Session**: `SharedPreferences` (Android SDK tích hợp sẵn).

---

## 3. CẤU TRÚC THƯ MỤC DỰ ÁN (PACKAGE STRUCTURE)

Dự án được phân bổ rõ ràng theo 3 domain để 3 thành viên làm việc độc lập:

```text
com.example.th_app2/
│
├── data/                                         <- THÀNH VIÊN 1 PHỤ TRÁCH
│   ├── db/          (README.md: Cài đặt Database 4 bảng & Seeder dữ liệu mẫu)
│   ├── model/       (README.md: Khai báo 4 Data class: Account, Employee, WorkLog, Salary)
│   └── pref/        (README.md: Quản lý SharedPreferences lưu phiên đăng nhập & Role)
│
├── ui/
│   ├── auth/                                     <- THÀNH VIÊN 1 PHỤ TRÁCH
│   │   └── README.md (Màn hình Login & Logic chuyển hướng theo Role)
│   │
│   ├── admin/                                    <- THÀNH VIÊN 2 PHỤ TRÁCH
│   │   ├── employee/ (Màn hình Danh sách, Thêm, Sửa, Xóa, Tìm kiếm Nhân viên)
│   │   └── salary/   (Màn hình Báo cáo Lương toàn công ty & Nút kích hoạt tính lương)
│   │
│   └── employee/                                 <- THÀNH VIÊN 3 PHỤ TRÁCH
│       ├── attendance/ (Màn hình Check-in, Check-out & Ràng buộc chống trùng)
│       ├── history/    (Màn hình Xem Lịch sử Chấm công theo ngày/tháng)
│       └── salary/     (Màn hình Xem Bảng lương cá nhân & Giờ làm thêm OT)
│
├── MainActivity.kt  (Activity chính chạy Jetpack Compose)
└── ui/theme/        (Cấu hình Theme, Color, Type Material3)
```

---

## 4. BẢNG PHÂN CHIA NHIỆM VỤ CHI TIẾT (WBS)

### 👨‍💻 THÀNH VIÊN 1: DATA CORE, SESSION & LOGIN LEAD
*Chịu trách nhiệm nền tảng dữ liệu cho cả nhóm và tính năng Đăng nhập.*

- **Thư mục làm việc**: `data/db/`, `data/model/`, `data/pref/`, `ui/auth/`
- **Các đầu việc cụ thể**:
  1. **Tạo Database 4 bảng**: Tạo `DatabaseHelper` định nghĩa đúng 4 bảng (`Account`, `Employee`, `WorkLog`, `Salary`).
  2. **Viết Data Seeder**: Nạp sẵn tài khoản mẫu (1 Admin, 2 Employee) và vài bản ghi chấm công mẫu để cả nhóm test ngay lập tức mà không cần gõ tay.
  3. **Quản lý Session (`PrefManager`)**: Viết các hàm `saveSession()`, `getSession()`, `clearSession()` dùng `SharedPreferences`.
  4. **Màn hình Đăng nhập (Login Screen)**: Nhập Username/Password, kiểm tra DB, lưu Session và chuyển sang màn hình Admin hoặc Employee theo Role.
  5. **Nút Đăng xuất (Logout)**: Xóa Session và quay về màn hình Login.

---

### 👨‍💼 THÀNH VIÊN 2: ADMIN MODULE (QUẢN LÝ NHÂN VIÊN & LƯƠNG CÔNG TY)
*Chịu trách nhiệm toàn bộ giao diện và nghiệp vụ của Quản trị viên.*

- **Thư mục làm việc**: `ui/admin/employee/`, `ui/admin/salary/`
- **Các đầu việc cụ thể**:
  1. **Danh sách Nhân viên**: Hiển thị danh sách nhân viên dạng Card (Mã, Tên, Email, SĐT, Lương theo giờ `hourlyRate`).
  2. **Thêm mới Nhân viên**: Form nhập thông tin NV và tự động tạo tài khoản đăng nhập tương ứng trong bảng `Account`.
  3. **Sửa & Xóa Nhân viên**: Cho phép cập nhật thông tin/lương theo giờ; Cho phép xóa NV (có Dialog xác nhận).
  4. **Tìm kiếm Nhân viên**: Thanh tìm kiếm lọc theo Tên hoặc Mã NV trong thời gian thực.
  5. **Quản lý & Kích hoạt Tính lương**:
     - Nút "Tính lương tháng": Quét toàn bộ `WorkLog` trong tháng để tính giờ chuẩn, giờ OT ($>8h/\text{ngày}$) và tổng tiền lương.
     - Hiển thị bảng tổng hợp lương của toàn công ty.

---

### 👷‍♂️ THÀNH VIÊN 3: EMPLOYEE MODULE (CHẤM CÔNG & LƯƠNG CÁ NHÂN)
*Chịu trách nhiệm toàn bộ giao diện và nghiệp vụ của Nhân viên.*

- **Thư mục làm việc**: `ui/employee/attendance/`, `ui/employee/history/`, `ui/employee/salary/`
- **Các đầu việc cụ thể**:
  1. **Giao diện Chấm công (Check-in/out)**:
     - Hiển thị ngày giờ thực tế và trạng thái ca làm việc hôm nay.
     - Nút **Check-in** và nút **Check-out**.
  2. **Ràng buộc Chấm công (Validation)**:
     - Chống check-in 2 lần trong 1 ngày (chặn nếu đã check-in).
     - Chặn check-out nếu chưa check-in.
     - Tính tự động: $\text{totalHours} = \text{checkOut} - \text{checkIn}$ khi bấm Check-out.
  3. **Lịch sử Chấm công**: Hiển thị danh sách các ngày đã làm việc trong tháng, kèm bộ lọc theo Tháng/Năm.
  4. **Xem Bảng lương Cá nhân**: Hiển thị thẻ tóm tắt thu nhập tháng của nhân viên: Tổng giờ làm, Giờ làm thêm (OT), Mức lương/giờ và **Tổng lương thực nhận**.

---

## 5. HỢP ĐỒNG GIAO TIẾP DỮ LIỆU (CONTRACT INTERFACE)

Thành viên 2 và Thành viên 3 có thể gọi các hàm sau từ `DatabaseHelper` mà Thành viên 1 cam kết triển khai:

```kotlin
interface AppRepository {
    // Member 1 (Auth & Session)
    fun login(username: String, password: String): Account?
    fun getEmployeeByAccountId(accountId: Int): Employee?
    
    // Member 2 (Admin Module)
    fun getAllEmployees(): List<Employee>
    fun searchEmployees(query: String): List<Employee>
    fun insertEmployee(emp: Employee, acc: Account): Boolean
    fun updateEmployee(emp: Employee): Boolean
    fun deleteEmployee(empId: Int): Boolean
    fun calculateMonthlySalary(month: Int, year: Int)
    fun getAllSalaries(month: Int, year: Int): List<SalaryDetailDto>
    
    // Member 3 (Employee Module)
    fun getTodayWorkLog(employeeId: Int): WorkLog?
    fun doCheckIn(employeeId: Int): Boolean
    fun doCheckOut(employeeId: Int): Boolean
    fun getWorkLogsByEmployee(employeeId: Int, month: Int, year: Int): List<WorkLog>
    fun getSalaryByEmployee(employeeId: Int, month: Int, year: Int): Salary?
}
```

---

## 6. QUY TẮC PHỐI HỢP GIT TRÁNH CONFLICT

1. **Phân nhánh (Branching)**:
   - `main`: Chỉ merge khi chạy hoàn chỉnh và không có lỗi build.
   - `feat/member1-db-auth`: Nhánh của Thành viên 1.
   - `feat/member2-admin`: Nhánh của Thành viên 2.
   - `feat/member3-employee`: Nhánh của Thành viên 3.
2. **Quy tắc làm việc**:
   - Thành viên 1 tạo khung Database, Model và Seeder trước, commit và push lên đầu tiên.
   - Thành viên 2 và 3 pull nhánh về và chỉ code trong thư mục phụ trách của mình (`ui/admin/` hoặc `ui/employee/`).
   - Tuyệt đối không chỉnh sửa file của thành viên khác để tránh xung đột khi merge.

---

## 7. BỘ DỮ LIỆU TEST MẪU (SEEDER DATA ĐỂ DEMO CHẤM ĐIỂM)

- **Admin**: `admin` / `admin123`
- **Nhân viên 1**: `john` / `123456` (Mã: `NV001`, Mức lương: `50.000 đ/h`)
- **Nhân viên 2**: `alice` / `123456` (Mã: `NV002`, Mức lương: `70.000 đ/h`)

---

## 8. CHECKLIST NGHIỆM THU TRƯỚC KHI NỘP BÀI

- [ ] Cài app lần đầu mở lên có sẵn tài khoản mẫu để test ngay.
- [ ] Đăng nhập đúng điều hướng vào Admin hoặc Employee.
- [ ] Tắt app mở lại vẫn giữ phiên đăng nhập (`SharedPreferences`). Bấm Logout quay lại Login.
- [ ] Admin: Thêm, sửa, xóa, tìm kiếm nhân viên mượt mà, lưu vào CSDL.
- [ ] Employee: Check-in ghi nhận ngày giờ, bấm lần 2 báo lỗi duplicate.
- [ ] Employee: Bấm Check-out khi chưa Check-in báo lỗi.
- [ ] Check-out hợp lệ tự động tính đúng số giờ làm `totalHours`.
- [ ] Admin bấm tính lương $\rightarrow$ Employee vào tab Lương thấy ngay số tiền được tính (đúng công thức OT x1.5).
