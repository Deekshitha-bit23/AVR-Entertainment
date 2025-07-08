package com.deeksha.avrentertainment.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.deeksha.avrentertainment.models.Project
import com.deeksha.avrentertainment.models.NotificationData
import com.deeksha.avrentertainment.models.NotificationType
import com.deeksha.avrentertainment.models.UserRole
import com.deeksha.avrentertainment.repository.NotificationRepository
import com.deeksha.avrentertainment.repository.AuthRepository
import com.deeksha.avrentertainment.repository.ExpenseRepository
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextOverflow

// Enhanced data class for notification summaries
data class NotificationSummary(
    val projectId: String,
    val projectName: String,
    val count: Int,
    val type: NotificationType,
    val lastNotification: NotificationData,
    val totalAmount: Double = 0.0
)

// Enhanced Notification Item Component with role-specific icons
@Composable
fun EnhancedNotificationItem(
    summary: NotificationSummary,
    userRole: UserRole,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (summary.type) {
                NotificationType.EXPENSE_SUBMITTED, NotificationType.PENDING_APPROVAL_REMINDER -> Color(0xFFE3F2FD)
                NotificationType.EXPENSE_APPROVED -> Color(0xFFE8F5E8)
                NotificationType.EXPENSE_REJECTED -> Color(0xFFFFEBEE)
                NotificationType.BUDGET_EXCEEDED_PROJECT, NotificationType.BUDGET_EXCEEDED_DEPARTMENT -> Color(0xFFFFEBEE)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            }
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Role-specific icon
                Icon(
                    imageVector = when (summary.type) {
                        NotificationType.EXPENSE_SUBMITTED, NotificationType.PENDING_APPROVAL_REMINDER -> Icons.Default.PendingActions
                        NotificationType.EXPENSE_APPROVED -> Icons.Default.CheckCircle
                        NotificationType.EXPENSE_REJECTED -> Icons.Default.Cancel
                        NotificationType.BUDGET_EXCEEDED_PROJECT, NotificationType.BUDGET_EXCEEDED_DEPARTMENT -> Icons.Default.Warning
                        else -> Icons.Default.Notifications
                    },
                    contentDescription = summary.type.name,
                    tint = when (summary.type) {
                        NotificationType.EXPENSE_SUBMITTED, NotificationType.PENDING_APPROVAL_REMINDER -> Color(0xFF2196F3)
                        NotificationType.EXPENSE_APPROVED -> Color(0xFF4CAF50)
                        NotificationType.EXPENSE_REJECTED -> Color(0xFFE53E3E)
                        NotificationType.BUDGET_EXCEEDED_PROJECT, NotificationType.BUDGET_EXCEEDED_DEPARTMENT -> Color(0xFFFF9800)
                        else -> Color.Gray
                    },
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = summary.projectName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Role-specific message
                    val message = when (userRole) {
                        UserRole.PRODUCTION_HEAD -> {
                            when (summary.type) {
                                NotificationType.EXPENSE_SUBMITTED, NotificationType.PENDING_APPROVAL_REMINDER -> 
                                    "${summary.count} expenses pending approval"
                                else -> "${summary.count} notifications"
                            }
                        }
                        UserRole.APPROVER -> {
                            when (summary.type) {
                                NotificationType.EXPENSE_SUBMITTED, NotificationType.PENDING_APPROVAL_REMINDER -> 
                                    "${summary.count} expenses awaiting your approval"
                                else -> "${summary.count} notifications"
                            }
                        }
                        UserRole.USER -> {
                            when (summary.type) {
                                NotificationType.EXPENSE_APPROVED -> "${summary.count} expenses approved"
                                NotificationType.EXPENSE_REJECTED -> "${summary.count} expenses rejected"
                                else -> "${summary.count} updates"
                            }
                        }
                    }
                    
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Show total amount for relevant notifications
                    if (summary.totalAmount > 0) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Total: ₹${String.format("%,.0f", summary.totalAmount)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            color = Color(0xFF2E5CFF)
                        )
                    }
                }
            }
            
            // Badge counter
            if (summary.count > 0) {
                Badge(
                    containerColor = when (summary.type) {
                        NotificationType.EXPENSE_SUBMITTED, NotificationType.PENDING_APPROVAL_REMINDER -> Color(0xFF2196F3)
                        NotificationType.EXPENSE_APPROVED -> Color(0xFF4CAF50)
                        NotificationType.EXPENSE_REJECTED -> Color(0xFFE53E3E)
                        NotificationType.BUDGET_EXCEEDED_PROJECT, NotificationType.BUDGET_EXCEEDED_DEPARTMENT -> Color(0xFFFF9800)
                        else -> Color.Gray
                    },
                    contentColor = Color.White
                ) {
                    Text(
                        text = summary.count.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Enhanced Notification Panel with role-specific filtering
@Composable
fun EnhancedNotificationPanel(
    notifications: List<NotificationSummary>,
    userRole: UserRole,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onNotificationClick: (NotificationSummary) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpanded() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF4169E1),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = when (userRole) {
                                UserRole.PRODUCTION_HEAD -> "Pending Approvals"
                                UserRole.APPROVER -> "Approval Requests"
                                UserRole.USER -> "Expense Updates"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4169E1)
                            )
                        )
                        if (notifications.isNotEmpty()) {
                            Text(
                                text = "Across ${notifications.size} project${if (notifications.size > 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray
                                )
                            )
                        }
                    }
                    
                    if (notifications.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(
                            containerColor = Color(0xFFE53E3E),
                            contentColor = Color.White
                        ) {
                            Text(
                                text = notifications.sumOf { it.count }.toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = Color(0xFF4169E1)
                )
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                if (notifications.isEmpty()) {
                    Text(
                        text = when (userRole) {
                            UserRole.PRODUCTION_HEAD -> "No pending approvals across all projects"
                            UserRole.APPROVER -> "No approval requests for your projects"
                            UserRole.USER -> "No expense updates at this time"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(notifications) { summary ->
                            EnhancedNotificationItem(
                                summary = summary,
                                userRole = userRole,
                                onClick = { onNotificationClick(summary) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSelectionScreen(
    navController: NavController,
    projects: List<Project>,
    isLoading: Boolean,
    error: String?,
    onProjectSelected: (String) -> Unit,
    onCreateNewProject: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Enhanced notification states
    var notificationSummaries by remember { mutableStateOf<List<NotificationSummary>>(emptyList()) }
    var currentUserRole by remember { mutableStateOf<UserRole?>(null) }
    var isLoadingNotifications by remember { mutableStateOf(true) }
    var isNotificationPanelExpanded by remember { mutableStateOf(false) }
    
    // Load role-specific notifications
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoadingNotifications = true
                val authRepository = AuthRepository()
                val currentUserId = authRepository.getCurrentUserPhoneNumber()
                
                if (currentUserId != null) {
                    // Get user role
                    authRepository.getUserRole(currentUserId).fold(
                        onSuccess = { userRole ->
                            currentUserRole = userRole
                            val notificationRepository = NotificationRepository(context)
                            val expenseRepository = ExpenseRepository()
                            
                            android.util.Log.d("ProjectSelection", "🔍 Loading notifications for $userRole: $currentUserId")
                            
                            when (userRole) {
                                UserRole.PRODUCTION_HEAD -> {
                                    val pendingExpensesResult = expenseRepository.getPendingExpenses()
                                    pendingExpensesResult.fold(
                                        onSuccess = { pendingExpenses ->
                                            val summaries = pendingExpenses
                                                .groupBy { it.projectId }
                                                .map { (projectId, expenses) ->
                                                    NotificationSummary(
                                                        projectId = projectId,
                                                        projectName = expenses.first().projectName,
                                                        count = expenses.size,
                                                        type = NotificationType.PENDING_APPROVAL_REMINDER,
                                                        lastNotification = NotificationData(
                                                            title = "Pending Approvals",
                                                            message = "${expenses.size} expenses pending approval",
                                                            type = NotificationType.PENDING_APPROVAL_REMINDER,
                                                            projectId = projectId,
                                                            amount = expenses.sumOf { it.amount ?: 0.0 }
                                                        ),
                                                        totalAmount = expenses.sumOf { it.amount ?: 0.0 }
                                                    )
                                                }
                                                .sortedByDescending { it.count }
                                            notificationSummaries = summaries
                                            android.util.Log.d("ProjectSelection", "✅ Loaded ${summaries.size} pending approval summaries for Production Head")
                                        },
                                        onFailure = { e ->
                                            android.util.Log.e("ProjectSelection", "❌ Error loading pending expenses: ${e.message}")
                                        }
                                    )
                                }
                                
                                UserRole.APPROVER -> {
                                    notificationRepository.getNotificationsForUser(currentUserId, userRole).fold(
                                        onSuccess = { notifications ->
                                            val summaries = notifications
                                                .filter { it.type == NotificationType.EXPENSE_SUBMITTED || it.type == NotificationType.PENDING_APPROVAL_REMINDER }
                                                .groupBy { it.projectId ?: "unknown" }
                                                .mapNotNull { (projectId, projectNotifications) ->
                                                    if (projectId != "unknown") {
                                                        val projectName = projectNotifications.firstOrNull()?.let { notification ->
                                                            projects.find { it.id == projectId }?.name ?: "Unknown Project"
                                                        } ?: "Unknown Project"
                                                        NotificationSummary(
                                                            projectId = projectId,
                                                            projectName = projectName,
                                                            count = projectNotifications.size,
                                                            type = NotificationType.EXPENSE_SUBMITTED,
                                                            lastNotification = projectNotifications.first(),
                                                            totalAmount = projectNotifications.sumOf { it.amount ?: 0.0 }
                                                        )
                                                    } else null
                                                }
                                                .sortedByDescending { it.count }
                                            notificationSummaries = summaries
                                            android.util.Log.d("ProjectSelection", "✅ Loaded ${summaries.size} approval request summaries for Approver")
                                        },
                                        onFailure = { e ->
                                            android.util.Log.e("ProjectSelection", "❌ Error loading approver notifications: ${e.message}")
                                        }
                                    )
                                }
                                
                                UserRole.USER -> {
                                    android.util.Log.d("ProjectSelection", "🔍 Loading expense status notifications for USER: $currentUserId")
                                    
                                    // Debug and ensure notifications exist for this user
                                    notificationRepository.debugUserNotifications(currentUserId)
                                    
                                    notificationRepository.createNotificationsForCurrentUser().fold(
                                        onSuccess = {
                                            android.util.Log.d("ProjectSelection", "✅ Notifications ensured for current user")
                                        },
                                        onFailure = { e ->
                                            android.util.Log.e("ProjectSelection", "❌ Failed to ensure notifications: ${e.message}")
                                        }
                                    )
                                    
                                    // Now load notifications for this user
                                    notificationRepository.getNotificationsForUser(currentUserId, userRole).fold(
                                        onSuccess = { notifications ->
                                            android.util.Log.d("ProjectSelection", "📋 Found ${notifications.size} notifications for USER")
                                            
                                            // Filter for only approved and rejected expense notifications
                                            val relevantNotifications = notifications
                                                .filter { it.type == NotificationType.EXPENSE_APPROVED || it.type == NotificationType.EXPENSE_REJECTED }
                                                .also { filtered ->
                                                    android.util.Log.d("ProjectSelection", "📋 Filtered to ${filtered.size} relevant notifications (approved/rejected)")
                                                    filtered.forEach { notification ->
                                                        android.util.Log.d("ProjectSelection", "   - ${notification.title}: ${notification.message}")
                                                    }
                                                }
                                            
                                            // Group by project
                                            val summaries = relevantNotifications
                                                .groupBy { it.projectId ?: "unknown" }
                                                .mapNotNull { (projectId, projectNotifications) ->
                                                    if (projectId != "unknown") {
                                                        val projectName = projectNotifications.firstOrNull()?.let { notification ->
                                                            projects.find { it.id == projectId }?.name ?: "Unknown Project"
                                                        } ?: "Unknown Project"
                                                        
                                                        android.util.Log.d("ProjectSelection", "📁 Creating summary for project: $projectName (${projectNotifications.size} notifications)")
                                                        
                                                        NotificationSummary(
                                                            projectId = projectId,
                                                            projectName = projectName,
                                                            count = projectNotifications.size,
                                                            type = if (projectNotifications.any { it.type == NotificationType.EXPENSE_APPROVED }) 
                                                                NotificationType.EXPENSE_APPROVED 
                                                            else NotificationType.EXPENSE_REJECTED,
                                                            lastNotification = projectNotifications.first(),
                                                            totalAmount = projectNotifications.sumOf { it.amount ?: 0.0 }
                                                        )
                                                    } else {
                                                        android.util.Log.w("ProjectSelection", "⚠️ Skipping notification with unknown project ID")
                                                        null
                                                    }
                                                }
                                                .sortedByDescending { it.count }
                                            
                                            notificationSummaries = summaries
                                            android.util.Log.d("ProjectSelection", "✅ Loaded ${summaries.size} expense update summaries for User")
                                            
                                            // Log summary for debugging
                                            summaries.forEach { summary ->
                                                android.util.Log.d("ProjectSelection", "   📊 ${summary.projectName}: ${summary.count} notifications, ₹${String.format("%.0f", summary.totalAmount)}")
                                            }
                                        },
                                        onFailure = { e ->
                                            android.util.Log.e("ProjectSelection", "❌ Error loading user notifications: ${e.message}")
                                        }
                                    )
                                }
                                else -> {
                                    // Optionally log or handle unknown role
                                    notificationSummaries = emptyList()
                                }
                            }
                        },
                        onFailure = { e ->
                            android.util.Log.e("ProjectSelection", "❌ Error getting user role: ${e.message}")
                        }
                    )
                } else {
                    android.util.Log.d("ProjectSelection", "No logged in user found")
                }
            } catch (e: Exception) {
                android.util.Log.e("ProjectSelection", "❌ Exception loading notifications: ${e.message}")
            } finally {
                isLoadingNotifications = false
            }
        }
    }
    
    // Auto-expand notification panel if there are notifications
    LaunchedEffect(notificationSummaries) {
        if (notificationSummaries.isNotEmpty()) {
            isNotificationPanelExpanded = true
        }
    }
    
    // Dynamic routing function
    fun handleNotificationClick(summary: NotificationSummary) {
        android.util.Log.d("NotificationClick", "🔄 Handling click for ${summary.projectName} (${summary.type})")
        
        when (currentUserRole) {
            UserRole.PRODUCTION_HEAD -> {
                navController.navigate("production_head_home/${summary.projectId}")
            }
            UserRole.APPROVER -> {
                navController.navigate("production_head_home/${summary.projectId}")
            }
            UserRole.USER -> {
                navController.navigate("project_details/${summary.projectId}")
            }
            else -> {
                navController.navigate("project_details/${summary.projectId}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AVR ENTERTAINMENT",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4169E1)
                            )
                        )
                        Text(
                            text = "Select Project",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            navController.navigate("all_projects_reports")
                        }
                    ) {
                        Icon(
                            Icons.Default.Assessment,
                            contentDescription = "All Projects Report",
                            tint = Color(0xFF4169E1)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // Create User FAB
                FloatingActionButton(
                    onClick = { navController.navigate("create_user") },
                    containerColor = Color(0xFF2E5CFF),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Create User",
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Create Project FAB
                FloatingActionButton(
                    onClick = { onCreateNewProject() },
                    containerColor = Color(0xFF4169E1),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Create New Project",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Enhanced Notification Panel (always show if there are notifications)
                if (!isLoadingNotifications && notificationSummaries.isNotEmpty() && currentUserRole != null) {
                    EnhancedNotificationPanel(
                        notifications = notificationSummaries,
                        userRole = currentUserRole!!,
                        isExpanded = isNotificationPanelExpanded,
                        onToggleExpanded = { isNotificationPanelExpanded = !isNotificationPanelExpanded },
                        onNotificationClick = { summary -> handleNotificationClick(summary) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4169E1)
                        )
                    }
                } else if (error != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error loading projects",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        text = "Choose a project to continue:",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.DarkGray
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (projects.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No projects available.\nClick + to create a new project.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(projects) { project ->
                                ProjectCard(
                                    project = project,
                                    onClick = { onProjectSelected(project.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectCard(
    project: Project,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Project initial circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8EAF6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = project.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF4169E1)
                        )
                    )
                }

                // Project details
                Column {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Budget: ₹${project.budget}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        )
                    )
                    if (project.description != null) {
                        Text(
                            text = project.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray
                            )
                        )
                    }
                }
            }

            // Forward arrow
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Select Project",
                tint = Color(0xFF4169E1)
            )
        }
    }
}

// Project Details with Notifications Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsWithNotificationsScreen(
    navController: NavController,
    projectId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Project and notification states
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var projectNotifications by remember { mutableStateOf<List<NotificationData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isNotificationPanelExpanded by remember { mutableStateOf(true) }
    
    // Load project details and notifications
    LaunchedEffect(projectId) {
        scope.launch {
            try {
                isLoading = true
                error = null
                
                // Load project details
                val projectRepository = com.deeksha.avrentertainment.repository.ProjectRepository()
                projectRepository.getProjectById(projectId).fold(
                    onSuccess = { project ->
                        selectedProject = project
                        android.util.Log.d("ProjectDetails", "✅ Loaded project: ${project.name}")
                        
                        // Send project selection notification and load project-specific notifications
                        val notificationRepository = NotificationRepository(context)
                        val authRepository = com.deeksha.avrentertainment.repository.AuthRepository()
                        val currentUserId = authRepository.getCurrentUserPhoneNumber()
                        
                        if (currentUserId != null) {
                            // Send project selection notification
                            notificationRepository.sendProjectSelectionNotification(
                                userId = currentUserId,
                                projectId = projectId,
                                projectName = project.name
                            )
                            
                            // Get user role to determine what notifications to show
                            authRepository.getUserRole(currentUserId).fold(
                                onSuccess = { userRole ->
                                    val effectiveRole = userRole ?: com.deeksha.avrentertainment.models.UserRole.USER
                                    
                                    // Load project-specific notifications
                                    notificationRepository.getProjectSpecificNotifications(
                                        userId = currentUserId,
                                        projectId = projectId,
                                        userRole = effectiveRole
                                    ).fold(
                                        onSuccess = { notifications ->
                                            projectNotifications = notifications
                                            android.util.Log.d("ProjectDetails", "✅ Loaded ${notifications.size} project notifications for $currentUserId")
                                        },
                                        onFailure = { e ->
                                            android.util.Log.e("ProjectDetails", "❌ Error loading project notifications: ${e.message}")
                                        }
                                    )
                                },
                                onFailure = { e ->
                                    android.util.Log.e("ProjectDetails", "❌ Error getting user role: ${e.message}")
                                    // Fallback to USER role
                                    notificationRepository.getProjectSpecificNotifications(
                                        userId = currentUserId,
                                        projectId = projectId,
                                        userRole = com.deeksha.avrentertainment.models.UserRole.USER
                                    ).fold(
                                        onSuccess = { notifications ->
                                            projectNotifications = notifications
                                            android.util.Log.d("ProjectDetails", "✅ Loaded ${notifications.size} project notifications (fallback)")
                                        },
                                        onFailure = { e ->
                                            android.util.Log.e("ProjectDetails", "❌ Error loading project notifications (fallback): ${e.message}")
                                        }
                                    )
                                }
                            )
                        } else {
                            android.util.Log.e("ProjectDetails", "No logged in user found")
                        }
                    },
                    onFailure = { e ->
                        error = "Failed to load project: ${e.message}"
                        android.util.Log.e("ProjectDetails", "❌ Error loading project: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                error = "Error loading project data: ${e.message}"
                android.util.Log.e("ProjectDetails", "❌ Exception: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = selectedProject?.name ?: "Project Details",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4169E1)
                            )
                        )
                        Text(
                            text = "Project Dashboard",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Navigate to ProductionHeadHomeScreen for full project management
                            navController.navigate("production_head_home/$projectId") {
                                popUpTo("project_details/$projectId") { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Go to Dashboard",
                            tint = Color(0xFF4169E1)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4169E1)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading project details...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = error!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Project Information Card
                    item {
                        selectedProject?.let { project ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = project.name,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4169E1)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = project.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Budget: ₹${String.format("%.0f", project.budget)}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = Color(0xFF2E5CFF)
                                        )
                                        Text(
                                            text = "Status: ${project.status}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = Color(0xFF34C759)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Project Notifications Panel
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isNotificationPanelExpanded = !isNotificationPanelExpanded },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Notifications,
                                            contentDescription = "Notifications",
                                            tint = Color(0xFF4169E1),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Project Notifications",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF4169E1)
                                            )
                                        )
                                        if (projectNotifications.isNotEmpty()) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Badge(
                                                containerColor = Color(0xFFE53E3E),
                                                contentColor = Color.White
                                            ) {
                                                Text(
                                                    text = projectNotifications.size.toString(),
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                    
                                    Icon(
                                        if (isNotificationPanelExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = if (isNotificationPanelExpanded) "Collapse" else "Expand",
                                        tint = Color(0xFF4169E1)
                                    )
                                }
                                
                                if (isNotificationPanelExpanded) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    if (projectNotifications.isEmpty()) {
                                        Text(
                                            text = "No project notifications yet. You'll be notified about budget changes, approvals, and project updates.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        LazyColumn(
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.heightIn(max = 300.dp)
                                        ) {
                                            items(projectNotifications) { notification ->
                                                NotificationItem(
                                                    title = notification.title,
                                                    description = notification.message,
                                                    time = notification.createdAt.toDate().toString(),
                                                    isRead = notification.isRead
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Action Buttons
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate("production_head_home/$projectId")
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4169E1)
                                )
                            ) {
                                Text("Open Dashboard")
                            }
                            
                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Back to Projects")
                            }
                        }
                    }
                }
            }
        }
    }
} 