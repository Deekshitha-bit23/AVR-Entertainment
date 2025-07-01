package com.deeksha.avrentertainment.models

import com.google.firebase.Timestamp

// Project data model
data class Project(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val budget: Double = 0.0,
    val startDate: String = "",
    val endDate: String = "",
    val status: ProjectStatus = ProjectStatus.ACTIVE,
    val managerId: String = "",
    val teamMembers: List<String> = emptyList(),
    val departmentAllocations: Map<String, Double> = emptyMap(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

enum class ProjectStatus {
    PENDING, ACTIVE, INACTIVE, COMPLETED, ON_HOLD, CANCELLED
} 