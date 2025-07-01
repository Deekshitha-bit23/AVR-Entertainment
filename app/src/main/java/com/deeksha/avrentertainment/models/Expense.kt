package com.deeksha.avrentertainment.models

import com.google.firebase.Timestamp

// Enhanced Expense data model
data class Expense(
    val id: String = "",
    val projectId: String = "",
    val projectName: String = "",
    val date: String = "",
    val amount: Double = 0.0,
    val department: String = "",
    val category: String = "",
    val modeOfPayment: String = "",
    val description: String = "",
    val attachment: String? = null,
    val submittedBy: String = "",
    val submittedById: String = "",
    val approvedBy: String? = null,
    val approvedById: String? = null,
    val status: ExpenseStatus = ExpenseStatus.PENDING,
    val reviewerNote: String? = null,
    val submittedAt: Timestamp = Timestamp.now(),
    val reviewedAt: Timestamp? = null
)

enum class ExpenseStatus {
    PENDING, APPROVED, REJECTED
} 