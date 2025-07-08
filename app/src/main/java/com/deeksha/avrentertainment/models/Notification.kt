package com.deeksha.avrentertainment.models

import com.google.firebase.Timestamp

// Notification data model
data class NotificationData(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: NotificationType = NotificationType.EXPENSE_SUBMITTED,
    val recipientId: String = "",
    val senderId: String = "",
    val expenseId: String? = null,
    val projectId: String? = null,
    val departmentId: String? = null,
    val budgetId: String? = null,
    val amount: Double? = null,
    val isRead: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)

enum class NotificationType {
    // Expense related
    EXPENSE_SUBMITTED,
    EXPENSE_APPROVED,
    EXPENSE_REJECTED,
    
    // Budget related
    BUDGET_ADDED,
    BUDGET_DEDUCTED,
    BUDGET_EXCEEDED_PROJECT,
    BUDGET_EXCEEDED_DEPARTMENT,
    
    // Project related
    PROJECT_CREATED,
    PROJECT_UPDATED,
    PROJECT_ASSIGNMENT,
    
    // Approval related
    PENDING_APPROVAL_REMINDER
} 