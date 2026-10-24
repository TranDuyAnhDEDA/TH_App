package com.example.th_app2.data.model

data class Account(
    val id: Int = 0,
    val username: String,
    val password: String,
    val role: String // "ADMIN" hoặc "EMPLOYEE"
)
