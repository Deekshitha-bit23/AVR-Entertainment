package com.deeksha.avrentertainment.models

import com.google.firebase.Timestamp

data class BudgetItem(
    val slNo: Int = 0,
    val particulars: String = "",
    val cost: Double = 0.0,
    val unit: Int = 0,
    val amount: Double = 0.0,
    val numberOfPeople: Int = 0
)

data class Budget(
    val id: String = "",
    val category: String = "Pre Production",
    val items: List<BudgetItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) 