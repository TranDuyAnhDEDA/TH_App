package com.example.th_app2.data.model

data class Employee(
    val id: Int = 0,
    val code: String,
    val fullname: String,
    val email: String? = null,
    val phone: String? = null,
    val hourlyRate: Double = 0.0,
    val accountId: Int? = null
)
