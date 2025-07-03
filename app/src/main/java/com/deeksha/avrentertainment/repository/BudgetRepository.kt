package com.deeksha.avrentertainment.repository

import com.deeksha.avrentertainment.models.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Context
import com.deeksha.avrentertainment.services.LocalNotificationService

class BudgetRepository {
    private val db = FirebaseFirestore.getInstance()
    private val budgetsCollection = db.collection("budgets")

    suspend fun createBudget(budget: Budget): Result<String> = withContext(Dispatchers.IO) {
        try {
            val docRef = budgetsCollection.document()
            val budgetWithId = budget.copy(id = docRef.id)
            docRef.set(budgetWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBudget(id: String): Result<Budget> = withContext(Dispatchers.IO) {
        try {
            val doc = budgetsCollection.document(id).get().await()
            val budget = doc.toObject(Budget::class.java)
            if (budget != null) {
                Result.success(budget)
            } else {
                Result.failure(Exception("Budget not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllBudgets(): Flow<Result<List<Budget>>> = flow {
        try {
            val snapshot = budgetsCollection.get().await()
            val budgets = snapshot.documents.mapNotNull { it.toObject(Budget::class.java) }
            emit(Result.success(budgets))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun updateBudget(budget: Budget): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            budgetsCollection.document(budget.id)
                .set(budget.copy(updatedAt = Timestamp.now()))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBudget(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            budgetsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper function to create a budget from the provided data
    fun createPreProductionBudget(): Budget {
        val items = listOf(
            BudgetItem(1, "Office Expenses", 15000.0, 8, 120000.0, 8),
            BudgetItem(2, "Stationary", 10000.0, 2, 20000.0, 2),
            BudgetItem(3, "Printer", 15000.0, 1, 15000.0, 1),
            BudgetItem(4, "Poster Designer", 150000.0, 1, 150000.0, 1),
            BudgetItem(5, "Final Reckie", 50000.0, 1, 50000.0, 1),
            BudgetItem(6, "Photoshoot", 100000.0, 1, 100000.0, 1),
            BudgetItem(7, "Costume", 300000.0, 1, 300000.0, 1),
            BudgetItem(8, "Properties", 300000.0, 1, 300000.0, 1),
            BudgetItem(9, "Colour Pallet", 150000.0, 1, 150000.0, 1)
        )
        
        return Budget(
            category = "Pre Production",
            items = items,
            totalAmount = items.sumOf { item -> item.amount }
        )
    }

    // Enhanced Budget monitoring with duplicate prevention and cooldown
    suspend fun checkBudgetLimitsAndNotify(
        projectId: String,
        projectName: String,
        expenseRepository: ExpenseRepository,
        notificationRepository: NotificationRepository
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("BudgetMonitor", "🔍 Checking budget limits for project: $projectName")
            
            // Get all approved expenses for the project
            val expensesResult = expenseRepository.getExpensesByProject(projectId)
            if (expensesResult.isFailure) {
                return@withContext Result.failure(expensesResult.exceptionOrNull() ?: Exception("Failed to get expenses"))
            }
            
            val expenses = expensesResult.getOrNull()?.filter { it.status == ExpenseStatus.APPROVED } ?: emptyList()
            android.util.Log.d("BudgetMonitor", "📊 Found ${expenses.size} approved expenses")
            
            // Define budget limits with more realistic values
            val departmentBudgets = mapOf(
                "Costumes" to 100000.0,
                "Set Design" to 300000.0,
                "Camera" to 150000.0,
                "Lighting" to 100000.0,
                "Sound" to 80000.0,
                "Art" to 200000.0, // Reduced from unrealistic 1175909.0
                "Equipment" to 500000.0, // Reduced from 934000.0
                "Location & Accommodation" to 500000.0,
                "Wages & Crew" to 400000.0,
                "Miscellaneous" to 200000.0
            )
            
            val projectBudgetLimit = 2500000.0 // Reduced from 50 lakh to 25 lakh for more realistic alerts
            
            // Calculate current spending by department
            val departmentSpending = expenses.groupBy { expense -> expense.department }
                .mapValues { entry -> entry.value.sumOf { expense -> expense.amount } }
            
            val totalProjectSpending = expenses.sumOf { expense -> expense.amount }
            android.util.Log.d("BudgetMonitor", "💰 Total project spending: ₹${String.format("%.0f", totalProjectSpending)}")
            
            // Get project team members
            val (allUserIds, approverIds) = notificationRepository.getProjectTeamMembers(projectId).getOrElse { 
                listOf("+919876543210") to listOf("+918765432109")
            }
            
            val allRecipients = allUserIds + approverIds
            
            // Check department budgets with enhanced duplicate prevention
            val departmentExceedances = mutableListOf<Pair<String, Double>>()
            
            departmentSpending.forEach { (department, spent) ->
                val budgetLimit = departmentBudgets[department] ?: return@forEach // Skip if no budget defined
                
                // Only process if significantly exceeded (10% threshold to reduce noise)  
                val significantExcess = budgetLimit * 0.10 // 10% threshold
                if (spent > budgetLimit + significantExcess) {
                    val exceededAmount = spent - budgetLimit
                    departmentExceedances.add(department to exceededAmount)
                    android.util.Log.d("BudgetMonitor", "⚠️ Department $department exceeded by ₹${String.format("%.0f", exceededAmount)}")
                }
            }
            
            // Check project budget
            val projectExceedance = if (totalProjectSpending > projectBudgetLimit) {
                totalProjectSpending - projectBudgetLimit
            } else null
            
            // Send consolidated budget exceeded notification if any department or project is exceeded
            if (departmentExceedances.isNotEmpty() || projectExceedance != null) {
                notificationRepository.sendConsolidatedBudgetExceededNotification(
                    projectId = projectId,
                    projectName = projectName,
                    departmentExceedances = departmentExceedances,
                    projectExceedance = projectExceedance,
                    projectBudgetLimit = projectBudgetLimit,
                    recipientIds = allRecipients
                )
            } else {
                android.util.Log.d("BudgetMonitor", "✅ All budgets within limits")
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("BudgetMonitor", "❌ Error in budget monitoring: ${e.message}")
            Result.failure(e)
        }
    }
}

// Expense Repository
class ExpenseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val expensesCollection = db.collection("expenses")

    suspend fun createExpense(expense: Expense): Result<String> = withContext(Dispatchers.IO) {
        try {
            val docRef = expensesCollection.document()
            val expenseWithId = expense.copy(id = docRef.id)
            docRef.set(expenseWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllExpenses(): Result<List<Expense>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = expensesCollection.get().await()
            val expenses = snapshot.documents.mapNotNull { it.toObject(Expense::class.java) }
                .sortedByDescending { it.submittedAt.seconds } // Sort in memory by timestamp
            Result.success(expenses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPendingExpenses(): Result<List<Expense>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = expensesCollection
                .whereEqualTo("status", ExpenseStatus.PENDING.name)
                .get().await()
            val expenses = snapshot.documents.mapNotNull { it.toObject(Expense::class.java) }
                .sortedByDescending { it.submittedAt.seconds } // Sort in memory by timestamp
            Result.success(expenses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get pending expenses for a specific project
    suspend fun getPendingExpensesByProject(projectId: String): Result<List<Expense>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = expensesCollection
                .whereEqualTo("status", ExpenseStatus.PENDING.name)
                .whereEqualTo("projectId", projectId)
                .get().await()
            val expenses = snapshot.documents.mapNotNull { it.toObject(Expense::class.java) }
                .sortedByDescending { it.submittedAt.seconds } // Sort in memory by timestamp
            Result.success(expenses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpensesByProject(projectId: String): Result<List<Expense>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = expensesCollection
                .whereEqualTo("projectId", projectId)
                .get().await()
            val expenses = snapshot.documents.mapNotNull { it.toObject(Expense::class.java) }
                .sortedByDescending { it.submittedAt.seconds } // Sort in memory by timestamp
            Result.success(expenses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateExpenseStatus(
        expenseId: String,
        status: ExpenseStatus,
        reviewerId: String,
        reviewerName: String,
        reviewerNote: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updates = mapOf(
                "status" to status.name,
                "approvedBy" to reviewerName,
                "approvedById" to reviewerId,
                "reviewerNote" to reviewerNote,
                "reviewedAt" to Timestamp.now()
            )
            expensesCollection.document(expenseId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Create sample expenses for testing
    suspend fun createSampleExpenses(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ExpenseRepository", "Checking if sample expenses need to be created...")
            
            // Check if any pending expenses already exist
            val existingPendingExpenses = getPendingExpenses()
            if (existingPendingExpenses.isSuccess && existingPendingExpenses.getOrNull()?.isNotEmpty() == true) {
                android.util.Log.d("ExpenseRepository", "Pending expenses already exist, skipping sample expense creation")
                return@withContext Result.success(Unit)
            }
            
            android.util.Log.d("ExpenseRepository", "Creating sample expenses...")
            
            val sampleExpenses = listOf(
                Expense(
                    projectId = "project1",
                    projectName = "Movie Production A",
                    date = "10/04/2024",
                    amount = 7900.0,
                    department = "Set Design",
                    category = "Wages",
                    modeOfPayment = "By cash",
                    description = "Paid to makeup crew for 3-day shoot",
                    submittedBy = "Anil",
                    submittedById = "user1",
                    status = ExpenseStatus.PENDING
                ),
                Expense(
                    projectId = "project2", 
                    projectName = "Documentary Project",
                    date = "11/04/2024",
                    amount = 15000.0,
                    department = "Costumes",
                    category = "Equipment",
                    modeOfPayment = "By UPI",
                    description = "Costume rental for period drama",
                    submittedBy = "Priya",
                    submittedById = "user2",
                    status = ExpenseStatus.PENDING
                ),
                Expense(
                    projectId = "project1",
                    projectName = "Movie Production A", 
                    date = "12/04/2024",
                    amount = 5500.0,
                    department = "Miscellaneous",
                    category = "Travel",
                    modeOfPayment = "By cash",
                    description = "Transportation for location scouting",
                    submittedBy = "Ramesh",
                    submittedById = "user3",
                    status = ExpenseStatus.PENDING
                ),
                Expense(
                    projectId = "project3",
                    projectName = "Commercial Ads",
                    date = "13/04/2024", 
                    amount = 12000.0,
                    department = "Camera",
                    category = "Equipment",
                    modeOfPayment = "By check",
                    description = "Camera equipment rental",
                    submittedBy = "John",
                    submittedById = "user4",
                    status = ExpenseStatus.PENDING
                )
            )

            var successCount = 0
            sampleExpenses.forEach { expense ->
                android.util.Log.d("ExpenseRepository", "Creating expense: ${expense.department} - ₹${expense.amount}")
                createExpense(expense).fold(
                    onSuccess = { expenseId ->
                        android.util.Log.d("ExpenseRepository", "Successfully created expense with ID: $expenseId")
                        successCount++
                    },
                    onFailure = { e ->
                        android.util.Log.e("ExpenseRepository", "Failed to create expense: ${e.message}")
                    }
                )
            }
            
            android.util.Log.d("ExpenseRepository", "Created $successCount out of ${sampleExpenses.size} sample expenses")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("ExpenseRepository", "Error in createSampleExpenses: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ENHANCED MONITORING AND NOTIFICATION SYSTEM
    
    // 6. PENDING APPROVAL MONITORING (Enhanced)
    suspend fun checkPendingApprovalsAndNotify(
        notificationRepository: NotificationRepository,
        projectRepository: ProjectRepository
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("PendingApprovalMonitor", "🔍 Checking for pending approvals requiring notifications")
            
            // Get all pending expenses
            val pendingExpensesResult = getPendingExpenses()
            if (pendingExpensesResult.isFailure) {
                return@withContext Result.failure(pendingExpensesResult.exceptionOrNull() ?: Exception("Failed to get pending expenses"))
            }
            
            val pendingExpenses = pendingExpensesResult.getOrNull() ?: emptyList()
            android.util.Log.d("PendingApprovalMonitor", "📊 Found ${pendingExpenses.size} total pending expenses")
            
            // Group by project
            val expensesByProject = pendingExpenses.groupBy { it.projectId }
            
            expensesByProject.forEach { (projectId, expenses) ->
                val projectName = expenses.firstOrNull()?.projectName ?: "Unknown Project"
                                        val totalAmount = expenses.sumOf { expense -> expense.amount }
                val count = expenses.size
                
                android.util.Log.d("PendingApprovalMonitor", "📋 Project $projectName: $count expenses, ₹${String.format("%.0f", totalAmount)}")
                
                // Send reminder if there are multiple pending expenses or old expenses (>6 hours for faster testing)
                val hasOldExpenses = expenses.any { 
                    (System.currentTimeMillis() - it.submittedAt.seconds * 1000) > 6 * 60 * 60 * 1000 // Older than 6 hours
                }
                
                if (count > 1 || hasOldExpenses) {
                    android.util.Log.d("PendingApprovalMonitor", "⏰ Sending approval reminder for project $projectName")
                    
                    // Use the enhanced notification orchestrator
                    notificationRepository.sendRoleSpecificNotifications(
                        notificationType = NotificationType.PENDING_APPROVAL_REMINDER,
                        projectId = projectId,
                        projectName = projectName,
                        amount = totalAmount,
                        additionalData = mapOf(
                            "pendingCount" to count,
                            "expenseDetails" to expenses
                        )
                    )
                }
            }
            
            android.util.Log.d("PendingApprovalMonitor", "✅ Completed pending approval check")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("PendingApprovalMonitor", "❌ Error in checkPendingApprovalsAndNotify: ${e.message}")
            Result.failure(e)
        }
    }

    // Trigger budget monitoring after expense approval
    suspend fun approveExpenseWithBudgetCheck(
        expenseId: String,
        reviewerId: String,
        reviewerName: String,
        reviewerNote: String?,
        budgetRepository: BudgetRepository,
        notificationRepository: NotificationRepository,
        projectRepository: ProjectRepository
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // First, approve the expense
            val updateResult = updateExpenseStatus(expenseId, ExpenseStatus.APPROVED, reviewerId, reviewerName, reviewerNote)
            if (updateResult.isFailure) {
                return@withContext updateResult
            }
            
            // Get the expense details
            val allExpensesResult = getAllExpenses()
            if (allExpensesResult.isFailure) {
                return@withContext Result.success(Unit) // Expense was approved, but we couldn't check budget
            }
            
            val expense = allExpensesResult.getOrNull()?.find { it.id == expenseId }
            if (expense != null) {
                android.util.Log.d("ExpenseRepository", "🔔 Sending approval notification for expense: ${expense.id}")
                android.util.Log.d("ExpenseRepository", "👤 Recipient: ${expense.submittedById}")
                
                // Send approval notification to user and wait for completion
                val notificationResult = notificationRepository.sendExpenseStatusNotification(
                    expense, ExpenseStatus.APPROVED, reviewerName
                )
                notificationResult.fold(
                    onSuccess = {
                        android.util.Log.d("ExpenseRepository", "✅ Approval notification sent successfully")
                    },
                    onFailure = { error ->
                        android.util.Log.e("ExpenseRepository", "❌ Failed to send approval notification: ${error.message}")
                    }
                )
                
                // Check budget limits and send notifications if exceeded
                budgetRepository.checkBudgetLimitsAndNotify(
                    projectId = expense.projectId,
                    projectName = expense.projectName,
                    expenseRepository = this@ExpenseRepository,
                    notificationRepository = notificationRepository
                )
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // BUDGET CHANGE NOTIFICATION TRIGGERS
    suspend fun addBudgetAndNotify(
        projectId: String,
        projectName: String,
        department: String?,
        amount: Double,
        notificationRepository: NotificationRepository
    ): Result<Unit> {
        try {
            android.util.Log.d("BudgetRepository", "💰 Adding ₹$amount to ${department ?: "project"} budget")
            
            // Here you would normally update the budget in Firestore
            // For now, we'll just send the notification
            
            // Send notification to all project members
            notificationRepository.sendRoleSpecificNotifications(
                notificationType = NotificationType.BUDGET_ADDED,
                projectId = projectId,
                projectName = projectName,
                department = department,
                amount = amount,
                senderId = "admin"
            )
            
            android.util.Log.d("BudgetRepository", "✅ Budget added and notifications sent")
            return Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("BudgetRepository", "❌ Error adding budget: ${e.message}")
            return Result.failure(e)
        }
    }
    
    suspend fun deductBudgetAndNotify(
        projectId: String,
        projectName: String,
        department: String?,
        amount: Double,
        notificationRepository: NotificationRepository
    ): Result<Unit> {
        try {
            android.util.Log.d("BudgetRepository", "💸 Deducting ₹$amount from ${department ?: "project"} budget")
            
            // Here you would normally update the budget in Firestore
            // For now, we'll just send the notification
            
            // Send notification to all project members
            notificationRepository.sendRoleSpecificNotifications(
                notificationType = NotificationType.BUDGET_DEDUCTED,
                projectId = projectId,
                projectName = projectName,
                department = department,
                amount = amount,
                senderId = "admin"
            )
            
            android.util.Log.d("BudgetRepository", "✅ Budget deducted and notifications sent")
            return Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("BudgetRepository", "❌ Error deducting budget: ${e.message}")
            return Result.failure(e)
        }
    }
}

// Notification Repository
class NotificationRepository(private val context: Context? = null) {
    private val db = FirebaseFirestore.getInstance()
    private val notificationsCollection = db.collection("notifications")
    private val localNotificationService = context?.let { LocalNotificationService(it) }

    suspend fun createNotification(notification: NotificationData): Result<String> = withContext(Dispatchers.IO) {
        try {
            val docRef = notificationsCollection.document()
            val notificationWithId = notification.copy(id = docRef.id)
            docRef.set(notificationWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotificationsForUser(userId: String): Result<List<NotificationData>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = notificationsCollection
                .whereEqualTo("recipientId", userId)
                .get().await()
            val notifications = snapshot.documents.mapNotNull { document -> document.toObject(NotificationData::class.java) }
                .sortedByDescending { notification -> notification.createdAt.seconds } // Sort in memory by timestamp
            Result.success(notifications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markNotificationAsRead(notificationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            notificationsCollection.document(notificationId)
                .update("isRead", true)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendExpenseSubmittedNotification(
        expense: Expense,
        approverIds: List<String>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("NotificationRepository", "🔔 Creating expense submission notifications")
            android.util.Log.d("NotificationRepository", "📱 Expense ID: ${expense.id}")
            android.util.Log.d("NotificationRepository", "👤 Submitter: ${expense.submittedById}")
            android.util.Log.d("NotificationRepository", "🎯 Approvers: ${approverIds.joinToString()}")
            
            // Check if notifications for this expense already exist to prevent duplicates
            val existingNotifications = notificationsCollection
                .whereEqualTo("expenseId", expense.id)
                .whereEqualTo("type", NotificationType.EXPENSE_SUBMITTED.name)
                .get().await()
            
            if (existingNotifications.documents.isNotEmpty()) {
                android.util.Log.d("NotificationRepository", "⚠️ Expense submission notifications already exist for expense ${expense.id}, skipping")
                return@withContext Result.success(Unit)
            }
            
            // Filter out Production Heads from approver list - they should not get notifications
            val authRepository = AuthRepository()
            val filteredApproverIds = approverIds.filter { approverId ->
                // Check user role and exclude Production Heads
                try {
                    val userResult = authRepository.getUserRole(approverId).getOrNull()
                    val isProductionHead = userResult == com.deeksha.avrentertainment.models.UserRole.PRODUCTION_HEAD
                    if (isProductionHead) {
                        android.util.Log.d("NotificationRepository", "🚫 Excluding Production Head $approverId from notifications")
                    }
                    !isProductionHead
                } catch (e: Exception) {
                    android.util.Log.w("NotificationRepository", "Could not determine role for $approverId, including in notifications")
                    true // Include if role cannot be determined
                }
            }
            
            // Only send to filtered approvers (excluding Production Heads)
            filteredApproverIds.forEach { approverId ->
                val notification = NotificationData(
                    title = "New Expense Submitted",
                    message = "₹${expense.amount} expense for ${expense.projectName} needs approval",
                    type = NotificationType.EXPENSE_SUBMITTED,
                    recipientId = approverId,
                    senderId = expense.submittedById,
                    expenseId = expense.id,
                    projectId = expense.projectId,
                    departmentId = expense.department,
                    amount = expense.amount
                )
                
                val createResult = createNotification(notification)
                createResult.fold(
                    onSuccess = { notificationId ->
                        android.util.Log.d("NotificationRepository", "✅ Created submission notification $notificationId for approver $approverId")
                    },
                    onFailure = { error ->
                        android.util.Log.e("NotificationRepository", "❌ Failed to create submission notification for approver $approverId: ${error.message}")
                    }
                )
                
                // Send push notification to approver (not Production Head)
                sendPushNotification(
                    recipientId = approverId,
                    title = "New Expense Submitted",
                    body = "₹${String.format("%.0f", expense.amount)} expense for ${expense.projectName} needs approval",
                    data = mapOf(
                        "type" to "EXPENSE_SUBMITTED",
                        "expenseId" to expense.id,
                        "projectId" to expense.projectId,
                        "expenseAmount" to String.format("%.0f", expense.amount),
                        "projectName" to expense.projectName,
                        "submitterName" to expense.submittedBy
                    )
                )
            }
            android.util.Log.d("NotificationRepository", "✅ Sent expense submission notifications to ${filteredApproverIds.size} approvers (excluded ${approverIds.size - filteredApproverIds.size} Production Heads)")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("NotificationRepository", "❌ Error in sendExpenseSubmittedNotification: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun sendExpenseStatusNotification(
        expense: Expense,
        status: ExpenseStatus,
        reviewerName: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val statusText = when (status) {
                ExpenseStatus.APPROVED -> "approved"
                ExpenseStatus.REJECTED -> "rejected"
                else -> "updated"
            }
            
            android.util.Log.d("NotificationRepository", "🔔 Creating $statusText notification")
            android.util.Log.d("NotificationRepository", "👤 Recipient: ${expense.submittedById}")
            android.util.Log.d("NotificationRepository", "💰 Amount: ₹${expense.amount}")
            android.util.Log.d("NotificationRepository", "📋 Project: ${expense.projectName}")
            
            // Verify that the submitter is actually a USER (not Production Head or Approver)
            val authRepository = AuthRepository()
            val submitterRole = authRepository.getUserRole(expense.submittedById).getOrNull()
            
            if (submitterRole == com.deeksha.avrentertainment.models.UserRole.USER) {
                // Only send approval/rejection notifications to the USER who submitted the expense
                val notification = NotificationData(
                    title = "Expense ${statusText.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}",
                    message = "Your ₹${expense.amount} expense for ${expense.projectName} has been $statusText by $reviewerName",
                    type = when (status) {
                        ExpenseStatus.APPROVED -> NotificationType.EXPENSE_APPROVED
                        ExpenseStatus.REJECTED -> NotificationType.EXPENSE_REJECTED
                        else -> NotificationType.EXPENSE_SUBMITTED
                    },
                    recipientId = expense.submittedById, // Only to the submitter (USER)
                    senderId = expense.approvedById ?: "",
                    expenseId = expense.id,
                    projectId = expense.projectId,
                    departmentId = expense.department,
                    amount = expense.amount
                )
                
                val createResult = createNotification(notification)
                createResult.fold(
                    onSuccess = { notificationId ->
                        android.util.Log.d("NotificationRepository", "✅ Successfully created notification with ID: $notificationId")
                    },
                    onFailure = { error ->
                        android.util.Log.e("NotificationRepository", "❌ Failed to create notification: ${error.message}")
                    }
                )
                
                android.util.Log.d("NotificationRepository", "Sent expense $statusText notification to USER: ${expense.submittedById}")
                
                // Send push notification only to USER
                sendPushNotification(
                    recipientId = expense.submittedById,
                    title = "Expense ${statusText.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}",
                    body = "Your ₹${String.format("%.0f", expense.amount)} expense for ${expense.projectName} has been $statusText by $reviewerName",
                    data = mapOf(
                        "type" to when (status) {
                            ExpenseStatus.APPROVED -> "EXPENSE_APPROVED"
                            ExpenseStatus.REJECTED -> "EXPENSE_REJECTED"
                            else -> "EXPENSE_UPDATED"
                        },
                        "expenseId" to expense.id,
                        "projectId" to expense.projectId
                    )
                )
            } else {
                android.util.Log.d("NotificationRepository", "🚫 Skipping notification - submitter ${expense.submittedById} is not a USER (role: $submitterRole)")
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("NotificationRepository", "❌ Error in sendExpenseStatusNotification: ${e.message}")
            Result.failure(e)
        }
    }

    // COMPREHENSIVE NOTIFICATION SYSTEM FOR ALL ROLES

    // 1. BUDGET CHANGE NOTIFICATIONS (Users + Approvers)
    suspend fun sendBudgetChangeNotification(
        projectId: String,
        projectName: String,
        department: String,
        changeType: String, // "added" or "deducted"
        amount: Double,
        recipientIds: List<String>,
        senderId: String,
        isProjectLevel: Boolean = false
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val notificationType = when (changeType) {
                "added" -> NotificationType.BUDGET_ADDED
                "deducted" -> NotificationType.BUDGET_DEDUCTED
                else -> NotificationType.BUDGET_ADDED
            }
            
            val scope = if (isProjectLevel) "project" else "department"
            val scopeName = if (isProjectLevel) projectName else "$department department"
            
            android.util.Log.d("BudgetNotification", "💰 Creating budget $changeType notification for $scopeName")
            
            recipientIds.forEach { recipientId ->
                val notification = NotificationData(
                    title = "Budget ${changeType.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}",
                    message = "₹${String.format("%.0f", amount)} has been $changeType to $scopeName budget",
                    type = notificationType,
                    recipientId = recipientId,
                    senderId = senderId,
                    projectId = projectId,
                    departmentId = if (!isProjectLevel) department else null,
                    amount = amount
                )
                
                val createResult = createNotification(notification)
                createResult.fold(
                    onSuccess = { notificationId ->
                        android.util.Log.d("BudgetNotification", "✅ Created budget $changeType notification $notificationId for $recipientId")
                    },
                    onFailure = { error ->
                        android.util.Log.e("BudgetNotification", "❌ Failed to create budget notification: ${error.message}")
                    }
                )
                
                // Send push notification
                sendPushNotification(
                    recipientId = recipientId,
                    title = "Budget ${changeType.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}",
                    body = "₹${String.format("%.0f", amount)} has been $changeType to $scopeName budget",
                    data = mapOf(
                        "type" to notificationType.name,
                        "projectId" to projectId,
                        "department" to (department ?: ""),
                        "amount" to amount.toString(),
                        "isProjectLevel" to isProjectLevel.toString()
                    )
                )
            }
            android.util.Log.d("BudgetNotification", "✅ Sent budget $changeType notifications to ${recipientIds.size} recipients")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("BudgetNotification", "❌ Error in sendBudgetChangeNotification: ${e.message}")
            Result.failure(e)
        }
    }

    // CONSOLIDATED BUDGET EXCEEDED NOTIFICATION (Prevents duplicates and summarizes)
    suspend fun sendConsolidatedBudgetExceededNotification(
        projectId: String,
        projectName: String,
        departmentExceedances: List<Pair<String, Double>>,
        projectExceedance: Double?,
        projectBudgetLimit: Double,
        recipientIds: List<String>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("BudgetNotification", "🔄 Creating consolidated budget exceeded notification for $projectName")
            
            // Check cooldown period (only send budget exceeded notifications once every 30 minutes per project)
            val cooldownKey = "budget_exceeded_$projectId"
            val lastNotificationTime = getLastNotificationTime(cooldownKey)
            val currentTime = System.currentTimeMillis()
            val cooldownPeriod = 30 * 60 * 1000 // 30 minutes in milliseconds
            
            if (currentTime - lastNotificationTime < cooldownPeriod) {
                android.util.Log.d("BudgetNotification", "⏰ Budget exceeded notification in cooldown period, skipping")
                return@withContext Result.success(Unit)
            }
            
            recipientIds.forEach { recipientId ->
                // Clear ALL old budget exceeded notifications for this project/user
                clearAllBudgetExceededNotifications(recipientId, projectId)
                
                // Create consolidated message
                val messageBuilder = StringBuilder()
                var totalExceededAmount = 0.0
                
                // Add department exceedances (show only top 3 to avoid clutter)
                if (departmentExceedances.isNotEmpty()) {
                    val topExceedances = departmentExceedances.sortedByDescending { it.second }.take(3)
                    messageBuilder.append("Departments exceeded: ")
                    topExceedances.forEachIndexed { index, (dept, amount) ->
                        if (index > 0) messageBuilder.append(", ")
                        messageBuilder.append("$dept (+₹${String.format("%.0f", amount)})")
                        totalExceededAmount += amount
                    }
                    
                    if (departmentExceedances.size > 3) {
                        messageBuilder.append(" and ${departmentExceedances.size - 3} more")
                    }
                }
                
                // Add project exceedance
                projectExceedance?.let { exceeded ->
                    if (messageBuilder.isNotEmpty()) messageBuilder.append(". ")
                    messageBuilder.append("Project budget exceeded by ₹${String.format("%.0f", exceeded)}")
                    totalExceededAmount += exceeded
                }
                
                val title = if (departmentExceedances.isNotEmpty() && projectExceedance != null) {
                    "⚠️ Multiple Budget Alerts - $projectName"
                } else if (departmentExceedances.isNotEmpty()) {
                    "⚠️ Department Budget Exceeded - $projectName"
                } else {
                    "⚠️ Project Budget Exceeded - $projectName"
                }
                
                val notification = NotificationData(
                    title = title,
                    message = messageBuilder.toString(),
                    type = NotificationType.BUDGET_EXCEEDED_PROJECT, // Use project type for consolidated
                    recipientId = recipientId,
                    senderId = "system",
                    projectId = projectId,
                    departmentId = null, // Set to null for consolidated notifications
                    amount = totalExceededAmount
                )
                
                val createResult = createNotification(notification)
                createResult.fold(
                    onSuccess = { notificationId ->
                        android.util.Log.d("BudgetNotification", "✅ Created consolidated budget notification $notificationId")
                        // Update last notification time
                        updateLastNotificationTime(cooldownKey, currentTime)
                    },
                    onFailure = { error ->
                        android.util.Log.e("BudgetNotification", "❌ Failed to create consolidated notification: ${error.message}")
                    }
                )
                
                // Send push notification
                sendPushNotification(
                    recipientId = recipientId,
                    title = title,
                    body = messageBuilder.toString(),
                    data = mapOf(
                        "type" to "BUDGET_EXCEEDED_CONSOLIDATED",
                        "projectId" to projectId,
                        "projectName" to projectName,
                        "totalExceeded" to totalExceededAmount.toString(),
                        "departmentCount" to departmentExceedances.size.toString()
                    )
                )
            }
            
            android.util.Log.d("BudgetNotification", "✅ Sent consolidated budget notifications to ${recipientIds.size} recipients")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("BudgetNotification", "❌ Error in consolidated budget notification: ${e.message}")
            Result.failure(e)
        }
    }

    // AGGRESSIVE DUPLICATE PREVENTION - Enhanced budget exceeded notification
    suspend fun sendBudgetExceededNotification(
        projectId: String,
        projectName: String,
        department: String?,
        currentSpent: Double,
        budgetLimit: Double,
        recipientIds: List<String>,
        isProjectLevel: Boolean = false
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // STEP 1: ALWAYS clean up ALL existing budget notifications first
            android.util.Log.d("BudgetNotification", "🧹 AGGRESSIVE CLEANUP: Removing ALL existing budget notifications...")
            cleanupDuplicateBudgetNotifications()
            
            // STEP 2: Check cooldown to prevent spam (10 minutes minimum between notifications)
            val cooldownKey = "budget_exceeded_${projectId}_${department ?: "project"}"
            val lastTime = getLastNotificationTime(cooldownKey)
            val currentTime = System.currentTimeMillis()
            val cooldownPeriod = 10 * 60 * 1000L // 10 minutes
            
            if (currentTime - lastTime < cooldownPeriod) {
                val remainingMinutes = (cooldownPeriod - (currentTime - lastTime)) / 60000
                android.util.Log.d("BudgetNotification", "⏰ COOLDOWN ACTIVE: Skipping notification (${remainingMinutes} minutes remaining)")
                return@withContext Result.success(Unit)
            }
            
            // STEP 3: Create SINGLE consolidated notification
            val exceededAmount = currentSpent - budgetLimit
            val scope = if (isProjectLevel) "Project" else "Department"
            val targetName = if (isProjectLevel) projectName else "$department department"
            
            val consolidatedTitle = "⚠️ Budget Alert"
            val consolidatedMessage = "$targetName exceeded by ₹${String.format("%.0f", exceededAmount)}"
            
            android.util.Log.d("BudgetNotification", "📱 Creating SINGLE consolidated notification: $consolidatedMessage")
            
            // Create ONE notification for all recipients
            recipientIds.forEach { recipientId ->
                val notification = NotificationData(
                    title = consolidatedTitle,
                    message = consolidatedMessage,
                    type = if (isProjectLevel) NotificationType.BUDGET_EXCEEDED_PROJECT else NotificationType.BUDGET_EXCEEDED_DEPARTMENT,
                    recipientId = recipientId,
                    senderId = "system",
                    projectId = projectId,
                    departmentId = department,
                    amount = exceededAmount
                )
                
                createNotification(notification).fold(
                    onSuccess = { notificationId ->
                        android.util.Log.d("BudgetNotification", "✅ Created consolidated alert $notificationId for $recipientId")
                    },
                    onFailure = { error ->
                        android.util.Log.e("BudgetNotification", "❌ Failed to create alert: ${error.message}")
                    }
                )
            }
            
            // STEP 4: Update cooldown to prevent future spam
            updateLastNotificationTime(cooldownKey, currentTime)
            
            android.util.Log.d("BudgetNotification", "✅ COMPLETED: Sent 1 consolidated alert to ${recipientIds.size} recipients")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("BudgetNotification", "❌ Error in enhanced budget notification: ${e.message}")
            Result.failure(e)
        }
    }

    // Enhanced duplicate clearing - removes ALL budget exceeded notifications for a project/user
    private suspend fun clearAllBudgetExceededNotifications(
        recipientId: String,
        projectId: String
    ) {
        try {
            val budgetExceededTypes = listOf(
                NotificationType.BUDGET_EXCEEDED_PROJECT.name,
                NotificationType.BUDGET_EXCEEDED_DEPARTMENT.name
            )
            
            // Query for ALL budget exceeded notifications for this recipient and project
            val snapshot = notificationsCollection
                .whereEqualTo("recipientId", recipientId)
                .whereEqualTo("projectId", projectId)
                .get().await()
            
            var deletedCount = 0
            
            // Filter and delete budget exceeded notifications
            snapshot.documents.forEach { document ->
                val notification = document.toObject(NotificationData::class.java)
                if (notification != null && notification.type.name in budgetExceededTypes) {
                    try {
                        document.reference.delete().await()
                        deletedCount++
                        android.util.Log.d("BudgetNotification", "Deleted old budget exceeded notification: ${notification.title}")
                    } catch (e: Exception) {
                        android.util.Log.w("BudgetNotification", "Failed to delete notification ${document.id}: ${e.message}")
                    }
                }
            }
            
            android.util.Log.d("BudgetNotification", "Cleared $deletedCount old budget exceeded notifications for project $projectId")
        } catch (e: Exception) {
            android.util.Log.w("BudgetNotification", "Failed to clear old budget exceeded notifications: ${e.message}")
        }
    }

    // Cooldown management methods
    private val notificationCooldowns = mutableMapOf<String, Long>()
    
    private fun getLastNotificationTime(cooldownKey: String): Long {
        return notificationCooldowns[cooldownKey] ?: 0L
    }
    
    private fun updateLastNotificationTime(cooldownKey: String, time: Long) {
        notificationCooldowns[cooldownKey] = time
        android.util.Log.d("BudgetNotification", "Updated cooldown for $cooldownKey")
    }

    // Clear old budget exceeded notifications for the same project/department (Legacy method)
    private suspend fun clearOldBudgetExceededNotifications(
        recipientId: String,
        projectId: String,
        department: String?,
        isProjectLevel: Boolean
    ) {
        // Delegate to the enhanced method
        clearAllBudgetExceededNotifications(recipientId, projectId)
    }

    // 2. PENDING APPROVAL REMINDERS (Approvers Only)
    suspend fun sendPendingApprovalReminder(
        projectId: String,
        projectName: String,
        pendingCount: Int,
        totalAmount: Double,
        approverIds: List<String>,
        expenseDetails: List<Expense> = emptyList()
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ApprovalReminder", "⏰ Creating pending approval reminder for project: $projectName")
            android.util.Log.d("ApprovalReminder", "📊 $pendingCount expenses totaling ₹${String.format("%.0f", totalAmount)}")
            
            approverIds.forEach { approverId ->
                // Create detailed message with expense breakdown
                val detailMessage = if (expenseDetails.isNotEmpty()) {
                    val topExpenses = expenseDetails.take(3)
                    val expenseList = topExpenses.joinToString(", ") { "₹${String.format("%.0f", it.amount)} (${it.department})" }
                    val moreText = if (pendingCount > 3) " and ${pendingCount - 3} more" else ""
                    "$pendingCount expenses awaiting approval: $expenseList$moreText"
                } else {
                    "$pendingCount expenses (₹${String.format("%.0f", totalAmount)}) awaiting approval for $projectName"
                }
                
                val notification = NotificationData(
                    title = "⏰ Pending Approvals - $projectName",
                    message = detailMessage,
                    type = NotificationType.PENDING_APPROVAL_REMINDER,
                    recipientId = approverId,
                    senderId = "system",
                    projectId = projectId,
                    amount = totalAmount
                )
                
                val createResult = createNotification(notification)
                createResult.fold(
                    onSuccess = { notificationId ->
                        android.util.Log.d("ApprovalReminder", "✅ Created approval reminder $notificationId for approver $approverId")
                    },
                    onFailure = { error ->
                        android.util.Log.e("ApprovalReminder", "❌ Failed to create approval reminder: ${error.message}")
                    }
                )
                
                // Send push notification
                sendPushNotification(
                    recipientId = approverId,
                    title = "⏰ Pending Approvals - $projectName",
                    body = "$pendingCount expenses (₹${String.format("%.0f", totalAmount)}) awaiting approval",
                    data = mapOf(
                        "type" to "PENDING_APPROVAL_REMINDER",
                        "projectId" to projectId,
                        "pendingCount" to pendingCount.toString(),
                        "totalAmount" to totalAmount.toString(),
                        "projectName" to projectName
                    )
                )
            }
            android.util.Log.d("ApprovalReminder", "✅ Sent approval reminders to ${approverIds.size} approvers")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("ApprovalReminder", "❌ Error in sendPendingApprovalReminder: ${e.message}")
            Result.failure(e)
        }
    }

    // Get notifications by type for role-specific filtering
    suspend fun getNotificationsByType(
        userId: String,
        types: List<NotificationType>
    ): Result<List<NotificationData>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = notificationsCollection
                .whereEqualTo("recipientId", userId)
                .whereIn("type", types.map { type: NotificationType -> type.name })
                .get().await()
            val notifications = snapshot.documents.mapNotNull { document -> document.toObject(NotificationData::class.java) }
                .sortedByDescending { notification -> notification.createdAt.seconds }
            Result.success(notifications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. ROLE-SPECIFIC NOTIFICATION FILTERING
    suspend fun getNotificationsForUser(
        userId: String,
        userRole: com.deeksha.avrentertainment.models.UserRole
    ): Result<List<NotificationData>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("NotificationFilter", "🔍 Loading notifications for $userRole: $userId")
            
            val relevantTypes = when (userRole) {
                com.deeksha.avrentertainment.models.UserRole.USER -> listOf(
                    // User sees: ONLY approval/rejection results for their OWN submitted expenses
                    NotificationType.EXPENSE_APPROVED,
                    NotificationType.EXPENSE_REJECTED
                )
                com.deeksha.avrentertainment.models.UserRole.APPROVER -> listOf(
                    // Approver sees: New submissions and pending reminders for expenses they need to approve
                    NotificationType.EXPENSE_SUBMITTED,
                    NotificationType.PENDING_APPROVAL_REMINDER
                )
                com.deeksha.avrentertainment.models.UserRole.PRODUCTION_HEAD -> listOf(
                    // Production Head sees: NO notifications as per business requirement
                )
            }
            
            android.util.Log.d("NotificationFilter", "📋 Filtering for types: ${relevantTypes.joinToString()}")
            
            // Production Head gets no notifications
            if (userRole == com.deeksha.avrentertainment.models.UserRole.PRODUCTION_HEAD) {
                android.util.Log.d("NotificationFilter", "🚫 PRODUCTION HEAD - No notifications returned as per business requirements")
                return@withContext Result.success(emptyList())
            }
            
            val snapshot = notificationsCollection
                .whereEqualTo("recipientId", userId)
                .get().await()
            val notifications = snapshot.documents.mapNotNull { document -> document.toObject(NotificationData::class.java) }
                .filter { notification -> 
                    // Basic type filtering
                    val typeMatch = notification.type in relevantTypes
                    
                    // Additional role-specific filtering
                    when (userRole) {
                        com.deeksha.avrentertainment.models.UserRole.USER -> {
                            // Users only see notifications for expenses THEY submitted
                            typeMatch && notification.recipientId == userId
                        }
                        com.deeksha.avrentertainment.models.UserRole.APPROVER -> {
                            // Approvers only see notifications for expenses they need to approve
                            typeMatch && notification.recipientId == userId
                        }
                        com.deeksha.avrentertainment.models.UserRole.PRODUCTION_HEAD -> {
                            // Production Head sees nothing
                            false
                        }
                    }
                }
                .sortedByDescending { notification -> notification.createdAt.seconds }
            
            android.util.Log.d("NotificationFilter", "✅ Filtered ${notifications.size} notifications for $userRole")
            Result.success(notifications)
        } catch (e: Exception) {
            android.util.Log.e("NotificationFilter", "❌ Error filtering notifications: ${e.message}")
            Result.failure(e)
        }
    }

    // 4. PROJECT TEAM MANAGEMENT FOR NOTIFICATIONS
    suspend fun getProjectTeamMembers(projectId: String): Result<Pair<List<String>, List<String>>> = withContext(Dispatchers.IO) {
        try {
            // In a real app, this would query the project members from Firestore
            // For now, using mock data based on our test users
            val users = listOf("+919876543210", "+917654321098") // Mock users
            val approvers = listOf("+918765432109") // Mock approvers
            
            android.util.Log.d("ProjectTeam", "📋 Project $projectId team - Users: ${users.size}, Approvers: ${approvers.size}")
            Result.success(users to approvers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 5. COMPREHENSIVE NOTIFICATION ORCHESTRATOR
    suspend fun sendRoleSpecificNotifications(
        notificationType: NotificationType,
        projectId: String,
        projectName: String,
        department: String? = null,
        amount: Double? = null,
        expenseId: String? = null,
        senderId: String = "system",
        additionalData: Map<String, Any> = emptyMap()
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("NotificationOrchestrator", "🎯 Orchestrating $notificationType for project: $projectName")
            
            val (users, approvers) = getProjectTeamMembers(projectId).getOrElse { 
                android.util.Log.w("NotificationOrchestrator", "Failed to get project team, using defaults")
                listOf("+919876543210") to listOf("+918765432109")
            }
            
            when (notificationType) {
                NotificationType.BUDGET_ADDED, NotificationType.BUDGET_DEDUCTED -> {
                    // Send to both users and approvers
                    val allRecipients = users + approvers
                    val changeType = if (notificationType == NotificationType.BUDGET_ADDED) "added" else "deducted"
                    sendBudgetChangeNotification(
                        projectId = projectId,
                        projectName = projectName,
                        department = department ?: "Project",
                        changeType = changeType,
                        amount = amount ?: 0.0,
                        recipientIds = allRecipients,
                        senderId = senderId,
                        isProjectLevel = department == null
                    )
                }
                
                NotificationType.BUDGET_EXCEEDED_PROJECT, NotificationType.BUDGET_EXCEEDED_DEPARTMENT -> {
                    // Send to both users and approvers
                    val allRecipients = users + approvers
                    sendBudgetExceededNotification(
                        projectId = projectId,
                        projectName = projectName,
                        department = department,
                        currentSpent = additionalData["currentSpent"] as? Double ?: 0.0,
                        budgetLimit = additionalData["budgetLimit"] as? Double ?: 0.0,
                        recipientIds = allRecipients,
                        isProjectLevel = notificationType == NotificationType.BUDGET_EXCEEDED_PROJECT
                    )
                }
                
                NotificationType.PENDING_APPROVAL_REMINDER -> {
                    // Send only to approvers
                    val expenseDetails = additionalData["expenseDetails"] as? List<Expense> ?: emptyList()
                    sendPendingApprovalReminder(
                        projectId = projectId,
                        projectName = projectName,
                        pendingCount = additionalData["pendingCount"] as? Int ?: 0,
                        totalAmount = amount ?: 0.0,
                        approverIds = approvers,
                        expenseDetails = expenseDetails
                    )
                }
                
                else -> {
                    android.util.Log.d("NotificationOrchestrator", "ℹ️ Notification type $notificationType handled by specific methods")
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("NotificationOrchestrator", "❌ Error in notification orchestrator: ${e.message}")
            Result.failure(e)
        }
    }
    
    // NUCLEAR CLEANUP - Remove ALL budget exceeded notifications
    suspend fun cleanupDuplicateBudgetNotifications(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("NotificationCleanup", "🧹 NUCLEAR CLEANUP: Removing ALL budget exceeded notifications...")
            
            // Get ALL budget exceeded notifications
            val budgetExceededTypes = listOf(
                NotificationType.BUDGET_EXCEEDED_PROJECT.name,
                NotificationType.BUDGET_EXCEEDED_DEPARTMENT.name
            )
            
            val snapshot = notificationsCollection.get().await()
            var totalDeleted = 0
            
            // Delete EVERY SINGLE budget exceeded notification
            snapshot.documents.forEach { document ->
                try {
                    val notification = document.toObject(NotificationData::class.java)
                    if (notification != null && notification.type.name in budgetExceededTypes) {
                        document.reference.delete().await()
                        totalDeleted++
                        android.util.Log.d("NotificationCleanup", "🗑️ Deleted: ${notification.title}")
                    }
                } catch (e: Exception) {
                    android.util.Log.w("NotificationCleanup", "Failed to delete document ${document.id}: ${e.message}")
                }
            }
            
            android.util.Log.d("NotificationCleanup", "🎯 NUCLEAR CLEANUP COMPLETE:")
            android.util.Log.d("NotificationCleanup", "   • DELETED: $totalDeleted budget exceeded notifications")
            android.util.Log.d("NotificationCleanup", "   • REMAINING: 0 budget exceeded notifications")
            android.util.Log.d("NotificationCleanup", "   • STATUS: Clean slate achieved! ✨")
            
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("NotificationCleanup", "❌ Nuclear cleanup failed: ${e.message}")
            Result.failure(e)
        }
    }

    // Periodic cleanup method to maintain clean notification data
    suspend fun performPeriodicNotificationCleanup(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("NotificationCleanup", "🔄 Performing periodic notification cleanup...")
            
            // Remove notifications older than 30 days
            val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000)
            
            val snapshot = notificationsCollection.get().await()
            var oldNotificationCount = 0
            
            snapshot.documents.forEach { document ->
                val notification = document.toObject(NotificationData::class.java)
                if (notification != null) {
                    val notificationTime = notification.createdAt.seconds * 1000
                    if (notificationTime < thirtyDaysAgo) {
                        try {
                            document.reference.delete().await()
                            oldNotificationCount++
                        } catch (e: Exception) {
                            android.util.Log.w("NotificationCleanup", "Failed to delete old notification: ${e.message}")
                        }
                    }
                }
            }
            
            android.util.Log.d("NotificationCleanup", "✅ Periodic cleanup completed. Removed $oldNotificationCount old notifications")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("NotificationCleanup", "❌ Periodic cleanup failed: ${e.message}")
            Result.failure(e)
        }
    }

    // Create balanced sample notifications for testing (only if no notifications exist)
    suspend fun createSampleNotifications(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Only create sample notifications if no notifications exist at all
            val existingNotifications = getNotificationsForUser("+919876543210").getOrNull()
            if (existingNotifications?.isNotEmpty() == true) {
                android.util.Log.d("NotificationRepository", "Notifications already exist (${existingNotifications.size}), skipping sample creation")
                return@withContext Result.success(Unit)
            }

            android.util.Log.d("NotificationRepository", "Creating balanced sample notifications for users (NO expense submitted notifications)...")

            // Create sample notifications for user +919876543210 (ONLY approved/rejected, budget changes, budget exceeded)
            val sampleNotifications = listOf(
                // Recent expense status notifications (ONLY approved/rejected)
                NotificationData(
                    title = "Expense Approved",
                    message = "Your ₹5,000 expense for Movie Production A has been approved by John Doe",
                    type = NotificationType.EXPENSE_APPROVED,
                    recipientId = "+919876543210",
                    senderId = "approver1",
                    expenseId = "exp1",
                    projectId = "project1",
                    amount = 5000.0
                ),
                NotificationData(
                    title = "Expense Approved",
                    message = "Your ₹3,400 expense for Set Design has been approved by Lead Approver",
                    type = NotificationType.EXPENSE_APPROVED,
                    recipientId = "+919876543210",
                    senderId = "approver4",
                    expenseId = "exp5",
                    projectId = "project1",
                    amount = 3400.0
                ),
                NotificationData(
                    title = "Expense Rejected",
                    message = "Your ₹7,900 expense for Movie Production A has been rejected by Current Approver",
                    type = NotificationType.EXPENSE_REJECTED,
                    recipientId = "+919876543210",
                    senderId = "approver1",
                    expenseId = "exp4",
                    projectId = "project1",
                    amount = 7900.0
                ),
                
                // Budget change notifications
                NotificationData(
                    title = "Budget Added",
                    message = "₹25,000 has been added to Costumes budget for Movie Production A",
                    type = NotificationType.BUDGET_ADDED,
                    recipientId = "+919876543210",
                    senderId = "admin",
                    projectId = "project1",
                    departmentId = "Costumes",
                    amount = 25000.0
                ),
                NotificationData(
                    title = "Budget Deducted",
                    message = "₹10,000 has been deducted from Equipment budget for Movie Production A",
                    type = NotificationType.BUDGET_DEDUCTED,
                    recipientId = "+919876543210",
                    senderId = "admin",
                    projectId = "project1",
                    departmentId = "Equipment",
                    amount = 10000.0
                ),
                
                // ONLY ONE consolidated budget exceeded notification (instead of multiple duplicates)
                NotificationData(
                    title = "⚠️ Multiple Budget Alerts - Movie Production A",
                    message = "Departments exceeded: Camera (+₹5,000), Lighting (+₹12,000), Sound (+₹8,000)",
                    type = NotificationType.BUDGET_EXCEEDED_PROJECT,
                    recipientId = "+919876543210",
                    senderId = "system",
                    projectId = "project1",
                    departmentId = null, // Consolidated notification
                    amount = 25000.0
                )
            )

            sampleNotifications.forEach { notification ->
                createNotification(notification).fold(
                    onSuccess = { notificationId ->
                        android.util.Log.d("NotificationRepository", "✅ Created sample notification: ${notification.title}")
                    },
                    onFailure = { error ->
                        android.util.Log.e("NotificationRepository", "❌ Failed to create sample notification: ${error.message}")
                    }
                )
            }

            android.util.Log.d("NotificationRepository", "Created ${sampleNotifications.size} balanced sample notifications")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("NotificationRepository", "Failed to create sample notifications: ${e.message}")
            Result.failure(e)
        }
    }
    
    // SMART PUSH NOTIFICATION SYSTEM - Role-specific and duplicate prevention
    private val sentPushNotifications = mutableSetOf<String>() // Track sent notifications to prevent duplicates
    private val userLoginTimes = mutableMapOf<String, Long>() // Track when users last logged in
    
    private suspend fun sendPushNotification(
        recipientId: String,
        title: String,
        body: String,
        data: Map<String, String>
    ) {
        try {
            // Create unique notification key to prevent duplicates
            val notificationKey = "${recipientId}_${data["type"]}_${data.get("expenseId") ?: data.get("projectId")}"
            
            // Skip if this exact notification was already sent
            if (sentPushNotifications.contains(notificationKey)) {
                android.util.Log.d("PushNotification", "⏭️ Skipping duplicate push notification: $notificationKey")
                return
            }
            
            // Get user role to apply role-specific filtering
            val userRole = getUserRole(recipientId)
            val shouldSendPush = shouldSendPushNotification(recipientId, data["type"] ?: "", userRole)
            
            if (!shouldSendPush) {
                android.util.Log.d("PushNotification", "🚫 Push notification filtered out for $userRole: ${data["type"]}")
                return
            }
            
            // Store in Firestore for persistence
            val pendingNotification = hashMapOf(
                "recipientId" to recipientId,
                "title" to title,
                "body" to body,
                "data" to data,
                "timestamp" to System.currentTimeMillis(),
                "notificationKey" to notificationKey
            )
            
            db.collection("pushNotifications")
                .add(pendingNotification)
            
            // Mark this notification as sent to prevent duplicates
            sentPushNotifications.add(notificationKey)
            
            android.util.Log.d("PushNotification", "✅ ROLE-SPECIFIC Push notification sent to $userRole: $title")
            
            // Show push notification simulation with role-specific filtering
            withContext(kotlinx.coroutines.Dispatchers.Main) {
                kotlinx.coroutines.delay(1000) // Reduced delay for better UX
                
                android.util.Log.d("PushNotification", "📱 ROLE-SPECIFIC PUSH NOTIFICATION:")
                android.util.Log.d("PushNotification", "Role: $userRole | Recipient: $recipientId")
                android.util.Log.d("PushNotification", "Title: $title | Type: ${data["type"]}")
                
                // Show notifications based on role and type
                localNotificationService?.let { notificationService ->
                    when (data["type"]) {
                        "EXPENSE_APPROVED" -> {
                            android.util.Log.d("PushNotification", "✅ Showing expense approved notification!")
                            val parts = body.split(" expense for ")
                            val amount = parts[0].replace("Your ₹", "").replace(",", "")
                            val projectName = if (parts.size > 1) {
                                parts[1].split(" has been ")[0]
                            } else "Unknown Project"
                            notificationService.sendExpenseApprovedNotification(
                                expenseAmount = amount,
                                projectName = projectName,
                                approverName = "Approver"
                            )
                        }
                        "EXPENSE_REJECTED" -> {
                            android.util.Log.d("PushNotification", "❌ Showing expense rejected notification!")
                            val parts = body.split(" expense for ")
                            val amount = parts[0].replace("Your ₹", "").replace(",", "")
                            val projectName = if (parts.size > 1) {
                                parts[1].split(" has been ")[0]
                            } else "Unknown Project"
                            notificationService.sendExpenseRejectedNotification(
                                expenseAmount = amount,
                                projectName = projectName,
                                approverName = "Approver"
                            )
                        }
                        "EXPENSE_SUBMITTED" -> {
                            android.util.Log.d("PushNotification", "🔔 Expense submitted notification logged (would go to approver's device)")
                            // Show notification for approvers only
                            notificationService.sendExpenseSubmittedNotification(
                                expenseAmount = data["expenseAmount"] ?: "0",
                                projectName = data["projectName"] ?: "Unknown Project",
                                submitterName = data["submitterName"] ?: "Unknown User"
                            )
                        }
                        "BUDGET_ADDED" -> {
                            android.util.Log.d("PushNotification", "💰 Showing budget added notification!")
                            notificationService.sendBudgetAddedNotification(
                                amount = data["amount"] ?: "0",
                                department = data["department"] ?: "Unknown Department",
                                projectName = data["projectName"] ?: "Unknown Project"
                            )
                        }
                        "BUDGET_DEDUCTED" -> {
                            android.util.Log.d("PushNotification", "💸 Showing budget deducted notification!")
                            notificationService.sendBudgetDeductedNotification(
                                amount = data["amount"] ?: "0",
                                department = data["department"] ?: "Unknown Department",
                                projectName = data["projectName"] ?: "Unknown Project"
                            )
                        }
                        "BUDGET_EXCEEDED_PROJECT", "BUDGET_EXCEEDED_DEPARTMENT" -> {
                            android.util.Log.d("PushNotification", "⚠️ Showing budget exceeded notification!")
                            val isProjectLevel = data["type"] == "BUDGET_EXCEEDED_PROJECT"
                            val scopeName = if (isProjectLevel) {
                                data["projectName"] ?: "Unknown Project"
                            } else {
                                data["department"] ?: "Unknown Department"
                            }
                            notificationService.sendBudgetExceededNotification(
                                scopeName = scopeName,
                                exceededAmount = data["exceededAmount"] ?: "0",
                                currentSpent = data["currentSpent"] ?: "0",
                                budgetLimit = data["budgetLimit"] ?: "0",
                                isProjectLevel = isProjectLevel
                            )
                        }
                        "PENDING_APPROVAL_REMINDER" -> {
                            android.util.Log.d("PushNotification", "🔔 Showing pending approval reminder!")
                            notificationService.sendPendingApprovalReminderNotification(
                                pendingCount = data["pendingCount"]?.toIntOrNull() ?: 0,
                                totalAmount = data["totalAmount"] ?: "0",
                                projectName = data["projectName"] ?: "Unknown Project"
                            )
                        }
                        else -> {
                            // Generic notification for any other type
                            notificationService.sendCustomNotification(title, body)
                        }
                    }
                } ?: run {
                    android.util.Log.w("PushNotification", "LocalNotificationService not available - notification not shown")
                }
            }
            
        } catch (e: Exception) {
            android.util.Log.e("PushNotification", "Failed to send push notification", e)
        }
    }
    
    // Helper method to get user role based on phone number
    private fun getUserRole(phoneNumber: String): UserRole {
        return when (phoneNumber) {
            "+918765432109" -> UserRole.APPROVER // Mock approver
            "+919876543210", "+917654321098" -> UserRole.USER // Mock users
            else -> {
                // In real app, query Firestore users collection
                android.util.Log.w("PushNotification", "Unknown user role for $phoneNumber, defaulting to USER")
                UserRole.USER
            }
        }
    }
    
    // Role-specific push notification filtering
    private fun shouldSendPushNotification(recipientId: String, notificationType: String, userRole: UserRole): Boolean {
        return when (userRole) {
            UserRole.USER -> {
                // Users only get push notifications for their OWN submitted expenses (approved/rejected)
                when (notificationType) {
                    "EXPENSE_APPROVED", "EXPENSE_REJECTED" -> true // Only for their own expenses
                    "EXPENSE_SUBMITTED" -> false // Users don't get notifications for submitting
                    "PENDING_APPROVAL_REMINDER" -> false // Users don't get pending approval reminders
                    "BUDGET_ADDED", "BUDGET_DEDUCTED" -> false // No budget notifications for users
                    "BUDGET_EXCEEDED_PROJECT", "BUDGET_EXCEEDED_DEPARTMENT" -> false // No budget exceeded for users
                    else -> false
                }
            }
            UserRole.APPROVER -> {
                // Approvers only get push notifications for expenses they need to approve in their projects
                when (notificationType) {
                    "EXPENSE_SUBMITTED" -> true // New expense submissions that need their approval
                    "PENDING_APPROVAL_REMINDER" -> true // Pending approval reminders for their projects
                    "EXPENSE_APPROVED", "EXPENSE_REJECTED" -> false // No notifications for their own approvals
                    "BUDGET_ADDED", "BUDGET_DEDUCTED" -> false // No budget change notifications for approvers
                    "BUDGET_EXCEEDED_PROJECT", "BUDGET_EXCEEDED_DEPARTMENT" -> false // No budget exceeded for approvers
                    else -> false
                }
            }
            UserRole.PRODUCTION_HEAD -> {
                // Production Head gets NO push notifications as per business requirement
                false
            }
        }
    }
    
    // Method to track user login and clear old push notification tracking
    suspend fun trackUserLogin(userId: String) {
        val currentTime = System.currentTimeMillis()
        val lastLoginTime = userLoginTimes[userId] ?: 0L
        
        userLoginTimes[userId] = currentTime
        
        // If user logged in more than 1 hour ago, clear their push notification tracking
        // This allows them to receive fresh notifications after login
        if (currentTime - lastLoginTime > 60 * 60 * 1000) { // 1 hour
            val userNotificationKeys = sentPushNotifications.filter { it.startsWith("${userId}_") }
            sentPushNotifications.removeAll(userNotificationKeys.toSet())
            android.util.Log.d("PushNotification", "🔄 Cleared push notification tracking for $userId after login")
        }
    }
    
    // Method to clear all push notification tracking (for testing)
    fun clearPushNotificationTracking() {
        sentPushNotifications.clear()
        userLoginTimes.clear()
        android.util.Log.d("PushNotification", "🧹 Cleared all push notification tracking")
    }
    
    // Send role-specific login notifications (only recent, relevant notifications)
    suspend fun sendLoginNotifications(userId: String, userRole: UserRole) {
        try {
            android.util.Log.d("LoginNotifications", "🔔 Sending login notifications for $userRole: $userId")
            
            when (userRole) {
                UserRole.USER -> {
                    // For users: Show recent approval/rejection notifications for THEIR submitted expenses only (last 7 days)
                    val recent7Days = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
                    
                    // Get recent notifications from Firestore for this specific user's expenses
                    val recentNotifications = getNotificationsForUser(userId, userRole).getOrNull()
                        ?.filter { it.createdAt.seconds * 1000 > recent7Days }
                        ?.filter { notification ->
                            // Only show approval/rejection notifications for expenses they submitted
                            when (notification.type) {
                                NotificationType.EXPENSE_APPROVED, NotificationType.EXPENSE_REJECTED -> {
                                    // Additional check: ensure this notification is for an expense they submitted
                                    notification.recipientId == userId
                                }
                                else -> false
                            }
                        }
                        ?.take(3) // Show only latest 3 notifications
                    
                    recentNotifications?.forEach { notification ->
                        val notificationKey = "${userId}_${notification.type}_${notification.expenseId}"
                        
                        // Only send if not already sent
                        if (!sentPushNotifications.contains(notificationKey)) {
                            val data = mapOf(
                                "type" to notification.type.toString(),
                                "expenseId" to (notification.expenseId ?: ""),
                                "projectId" to (notification.projectId ?: ""),
                                "amount" to notification.amount.toString()
                            )
                            
                            sendPushNotification(
                                recipientId = userId,
                                title = notification.title,
                                body = notification.message,
                                data = data
                            )
                            
                            sentPushNotifications.add(notificationKey)
                            android.util.Log.d("LoginNotifications", "📱 Sent USER notification: ${notification.title}")
                            
                            // Small delay between notifications
                            kotlinx.coroutines.delay(1500)
                        }
                    }
                    
                    android.util.Log.d("LoginNotifications", "✅ USER notifications completed - sent ${recentNotifications?.size ?: 0} notifications")
                }
                
                UserRole.APPROVER -> {
                    // For approvers: Show pending expense notifications for projects they can approve only
                    try {
                        val expenseRepository = ExpenseRepository()
                        expenseRepository.getPendingExpenses().fold(
                            onSuccess = { pendingExpenses ->
                                // Filter pending expenses - approvers should only see expenses that need their approval
                                val approverPendingExpenses = pendingExpenses.filter { expense ->
                                    // In a real app, check if this approver is assigned to this project
                                    // For now, show all pending expenses to demonstrate approver flow
                                    true
                                }
                                
                                if (approverPendingExpenses.isNotEmpty()) {
                                    val totalAmount = approverPendingExpenses.sumOf { it.amount }
                                    val notificationKey = "${userId}_PENDING_SUMMARY_${approverPendingExpenses.size}"
                                    
                                    if (!sentPushNotifications.contains(notificationKey)) {
                                        val data = mapOf(
                                            "type" to "PENDING_APPROVAL_REMINDER",
                                            "pendingCount" to approverPendingExpenses.size.toString(),
                                            "totalAmount" to totalAmount.toString(),
                                            "projectName" to "Multiple Projects"
                                        )
                                        
                                        sendPushNotification(
                                            recipientId = userId,
                                            title = "📋 ${approverPendingExpenses.size} Expenses Awaiting Your Approval",
                                            body = "Total: ₹${String.format("%.0f", totalAmount)} across all projects",
                                            data = data
                                        )
                                        
                                        sentPushNotifications.add(notificationKey)
                                        android.util.Log.d("LoginNotifications", "📱 Sent APPROVER notification: Pending approvals")
                                    }
                                }
                                
                                android.util.Log.d("LoginNotifications", "✅ APPROVER notifications completed - ${approverPendingExpenses.size} pending expenses")
                            },
                            onFailure = { error ->
                                android.util.Log.e("LoginNotifications", "Failed to get pending expenses: ${error.message}")
                            }
                        )
                    } catch (e: Exception) {
                        android.util.Log.e("LoginNotifications", "Error checking pending expenses: ${e.message}")
                    }
                }
                
                UserRole.PRODUCTION_HEAD -> {
                    // Production Head gets NO notifications as per requirement
                    android.util.Log.d("LoginNotifications", "🚫 PRODUCTION HEAD - No notifications sent as per business requirements")
                }
            }
            
            android.util.Log.d("LoginNotifications", "✅ Login notifications completed for $userRole")
        } catch (e: Exception) {
            android.util.Log.e("LoginNotifications", "Failed to send login notifications: ${e.message}")
        }
    }

    // PROJECT-SPECIFIC NOTIFICATION METHODS
    suspend fun getProjectSpecificNotifications(
        userId: String,
        projectId: String,
        userRole: UserRole
    ): Result<List<NotificationData>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ProjectNotifications", "🔍 Loading project-specific notifications for $userRole: $userId, project: $projectId")
            
            // Production Head gets no notifications
            if (userRole == UserRole.PRODUCTION_HEAD) {
                android.util.Log.d("ProjectNotifications", "🚫 PRODUCTION HEAD - No project notifications returned as per business requirements")
                return@withContext Result.success(emptyList())
            }
            
            val relevantTypes = when (userRole) {
                UserRole.USER -> listOf(
                    // Users only see approval/rejection for their own expenses in this project
                    NotificationType.EXPENSE_APPROVED,
                    NotificationType.EXPENSE_REJECTED
                )
                UserRole.APPROVER -> listOf(
                    // Approvers only see new submissions they need to approve in this project
                    NotificationType.EXPENSE_SUBMITTED,
                    NotificationType.PENDING_APPROVAL_REMINDER
                )
                UserRole.PRODUCTION_HEAD -> listOf(
                    // Production Head gets no notifications
                )
            }
            
            val snapshot = notificationsCollection
                .whereEqualTo("recipientId", userId)
                .whereEqualTo("projectId", projectId)
                .get().await()
                
            val notifications = snapshot.documents.mapNotNull { document -> 
                document.toObject(NotificationData::class.java) 
            }
                .filter { notification -> notification.type in relevantTypes }
                .sortedByDescending { notification -> notification.createdAt.seconds }
                .take(10) // Limit to 10 most recent notifications
            
            android.util.Log.d("ProjectNotifications", "✅ Found ${notifications.size} project-specific notifications")
            Result.success(notifications)
        } catch (e: Exception) {
            android.util.Log.e("ProjectNotifications", "❌ Error loading project notifications: ${e.message}")
            Result.failure(e)
        }
    }

    // Get approver notifications before login (for project selection screen)
    suspend fun getApproverNotificationsPreLogin(
        approverId: String,
        limitCount: Int = 5
    ): Result<List<NotificationData>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ApproverNotifications", "🔍 Loading pre-login notifications for approver: $approverId")
            
            val approverTypes = listOf(
                // Approvers only see expense submissions they need to approve
                NotificationType.EXPENSE_SUBMITTED,
                NotificationType.PENDING_APPROVAL_REMINDER
            )
            
            val snapshot = notificationsCollection
                .whereEqualTo("recipientId", approverId)
                .get().await()
                
            val notifications = snapshot.documents.mapNotNull { document -> 
                document.toObject(NotificationData::class.java) 
            }
                .filter { notification -> notification.type in approverTypes }
                .filter { !it.isRead } // Only unread notifications
                .sortedByDescending { notification -> notification.createdAt.seconds }
                .take(limitCount)
            
            android.util.Log.d("ApproverNotifications", "✅ Found ${notifications.size} pre-login approver notifications")
            Result.success(notifications)
        } catch (e: Exception) {
            android.util.Log.e("ApproverNotifications", "❌ Error loading approver notifications: ${e.message}")
            Result.failure(e)
        }
    }

    // Send project selection notification to user
    suspend fun sendProjectSelectionNotification(
        userId: String,
        projectId: String,
        projectName: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ProjectNotifications", "📱 Sending project selection notification for: $projectName")
            
            val notification = NotificationData(
                title = "Project Selected",
                message = "You've selected $projectName. Stay updated with project notifications!",
                type = NotificationType.PROJECT_UPDATED,
                recipientId = userId,
                senderId = "system",
                projectId = projectId
            )
            
            createNotification(notification).fold(
                onSuccess = { notificationId ->
                    android.util.Log.d("ProjectNotifications", "✅ Created project selection notification: $notificationId")
                },
                onFailure = { error ->
                    android.util.Log.e("ProjectNotifications", "❌ Failed to create project selection notification: ${error.message}")
                }
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("ProjectNotifications", "❌ Error in project selection notification: ${e.message}")
            Result.failure(e)
        }
    }
} 