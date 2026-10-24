package com.example.th_app2.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.th_app2.data.db.AccountDao
import com.example.th_app2.data.pref.PrefManager

// Bảng màu Dark Mode đồng bộ giao diện
private val DarkBackground = Color(0xFF0F172A)    // Nền tối chính
private val DarkCard = Color(0xFF1E293B)          // Khung Card tối
private val DarkInputBg = Color(0xFF0F172A)       // Nền ô nhập liệu
private val DarkBorder = Color(0xFF334155)        // Viền input / card
private val AccentBlue = Color(0xFFA5B4FC)        // Xanh mềm giống nút "+ Thêm Nhân Viên"
private val PrimaryBlue = Color(0xFF38BDF8)       // Xanh active tab
private val TextWhite = Color(0xFFF8FAFC)         // Chữ trắng sáng
private val TextMuted = Color(0xFF94A3B8)         // Chữ xám phụ

@Composable
fun LoginScreen(
    accountDao: AccountDao,
    prefManager: PrefManager,
    onLoginSuccess: (role: String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Đăng nhập, 1: Đăng ký

    // State Đăng nhập
    var loginUsername by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isLoginPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // State Đăng ký
    var regFullname by remember { mutableStateOf("") }
    var regUsername by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var isRegPasswordVisible by remember { mutableStateOf(false) }
    var regError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tiêu đề to duy nhất ở trên cùng
                Text(
                    text = "Quản lý Chấm công & Bảng lương",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 22.dp)
                )

                // Thanh Tab chuyển đổi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AuthTabItem(
                        text = "👤 Đăng nhập",
                        isSelected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            loginError = null
                            regError = null
                        },
                        modifier = Modifier.weight(1f)
                    )

                    AuthTabItem(
                        text = "📝 Đăng ký",
                        isSelected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            loginError = null
                            regError = null
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (selectedTab == 0) {
                    // FORM ĐĂNG NHẬP
                    DarkInputField(
                        value = loginUsername,
                        onValueChange = {
                            loginUsername = it
                            loginError = null
                        },
                        label = "Tên đăng nhập",
                        placeholder = "Nhập tài khoản",
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextMuted)
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DarkInputField(
                        value = loginPassword,
                        onValueChange = {
                            loginPassword = it
                            loginError = null
                        },
                        label = "Mật khẩu",
                        placeholder = "Nhập mật khẩu",
                        isPassword = true,
                        isPasswordVisible = isLoginPasswordVisible,
                        onTogglePassword = { isLoginPasswordVisible = !isLoginPasswordVisible },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted)
                        }
                    )

                    if (loginError != null) {
                        Text(
                            text = loginError ?: "",
                            color = Color(0xFFF87171),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Nút Đăng nhập
                    Button(
                        onClick = {
                            val u = loginUsername.trim()
                            val p = loginPassword.trim()

                            if (u.isEmpty() || p.isEmpty()) {
                                loginError = "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!"
                                return@Button
                            }

                            val session = accountDao.authenticate(u, p)
                            if (session != null) {
                                prefManager.saveSession(session)
                                Toast.makeText(context, "Đăng nhập thành công (${session.role})!", Toast.LENGTH_SHORT).show()
                                onLoginSuccess(session.role)
                            } else {
                                loginError = "Tên đăng nhập hoặc mật khẩu không chính xác!"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            contentColor = Color(0xFF0F172A)
                        )
                    ) {
                        Text(
                            text = "Đăng nhập",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // FORM ĐĂNG KÝ
                    DarkInputField(
                        value = regFullname,
                        onValueChange = { regFullname = it; regError = null },
                        label = "Họ và tên",
                        placeholder = "VD: Nguyễn Văn C",
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = TextMuted)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DarkInputField(
                        value = regUsername,
                        onValueChange = { regUsername = it; regError = null },
                        label = "Tên đăng nhập",
                        placeholder = "VD: emp3",
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextMuted)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DarkInputField(
                        value = regPassword,
                        onValueChange = { regPassword = it; regError = null },
                        label = "Mật khẩu",
                        placeholder = "Tối thiểu 6 ký tự",
                        isPassword = true,
                        isPasswordVisible = isRegPasswordVisible,
                        onTogglePassword = { isRegPasswordVisible = !isRegPasswordVisible },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DarkInputField(
                        value = regConfirmPassword,
                        onValueChange = { regConfirmPassword = it; regError = null },
                        label = "Xác nhận mật khẩu",
                        placeholder = "Nhập lại mật khẩu",
                        isPassword = true,
                        isPasswordVisible = isRegPasswordVisible,
                        onTogglePassword = { isRegPasswordVisible = !isRegPasswordVisible },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DarkInputField(
                        value = regPhone,
                        onValueChange = { regPhone = it; regError = null },
                        label = "Số điện thoại",
                        placeholder = "09xxxxxxxx",
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = TextMuted)
                        }
                    )

                    if (regError != null) {
                        Text(
                            text = regError ?: "",
                            color = Color(0xFFF87171),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val fn = regFullname.trim()
                            val u = regUsername.trim()
                            val p = regPassword.trim()
                            val cp = regConfirmPassword.trim()

                            if (fn.isEmpty() || u.isEmpty() || p.isEmpty()) {
                                regError = "Vui lòng nhập đủ Họ tên, Tên đăng nhập và Mật khẩu!"
                                return@Button
                            }

                            if (p != cp) {
                                regError = "Mật khẩu xác nhận không khớp!"
                                return@Button
                            }

                            val (success, message) = accountDao.registerEmployee(
                                fullname = fn,
                                username = u,
                                pass = p,
                                phone = regPhone.trim(),
                                hourlyRate = 50000.0
                            )

                            if (success) {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                loginUsername = u
                                loginPassword = p
                                selectedTab = 0
                            } else {
                                regError = message
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            contentColor = Color(0xFF0F172A)
                        )
                    ) {
                        Text(
                            text = "Tạo tài khoản nhân viên",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tab gạch chân đồng bộ với giao diện của nhóm
 */
@Composable
private fun AuthTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) TextWhite else TextMuted
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(2.5.dp)
                .background(if (isSelected) PrimaryBlue else Color.Transparent)
        )
    }
}

/**
 * Ô nhập liệu tối giản phong cách Dark mode
 */
@Composable
private fun DarkInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextMuted, fontSize = 13.sp) },
        placeholder = { Text(placeholder, color = Color(0xFF64748B), fontSize = 13.sp) },
        leadingIcon = leadingIcon,
        trailingIcon = if (isPassword && onTogglePassword != null) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password",
                        tint = TextMuted
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedContainerColor = DarkInputBg,
            unfocusedContainerColor = DarkInputBg,
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = DarkBorder
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DarkAuthScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Preview dark mode
    }
}
