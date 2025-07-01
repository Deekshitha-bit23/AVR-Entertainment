package com.deeksha.avrentertainment.models

import com.google.firebase.Timestamp

data class User(
    val phoneNumber: String = "",
    val name: String = "",
    val role: UserRole? = null,
    val createdBy: String = "",
    val timestamp: Timestamp = Timestamp.now()
)

enum class UserRole {
    USER,
    APPROVER,
    PRODUCTION_HEAD
} 