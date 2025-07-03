package com.deeksha.avrentertainment.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.BackHandler
import kotlinx.coroutines.launch
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.deeksha.avrentertainment.models.*
import com.deeksha.avrentertainment.repository.*

// Helper functions for formatting and calculations
fun formatIndianNumber(amount: Double): String {
    val amountStr = String.format("%.0f", amount)
    val length = amountStr.length
    
    return when {
        length <= 3 -> "₹$amountStr"
        length <= 5 -> "₹${amountStr.substring(0, length-3)},${amountStr.substring(length-3)}"
        length <= 7 -> "₹${amountStr.substring(0, length-5)},${amountStr.substring(length-5, length-3)},${amountStr.substring(length-3)}"
        length <= 9 -> "₹${amountStr.substring(0, length-7)},${amountStr.substring(length-7, length-5)},${amountStr.substring(length-5, length-3)},${amountStr.substring(length-3)}"
        else -> "₹${amountStr.substring(0, length-9)},${amountStr.substring(length-9, length-7)},${amountStr.substring(length-7, length-5)},${amountStr.substring(length-5, length-3)},${amountStr.substring(length-3)}"
    }
}

fun getTimeAgo(timeInMillis: Long): String {
    val currentTime = System.currentTimeMillis()
    val timeDiff = currentTime - timeInMillis
    
    return when {
        timeDiff < 60 * 1000 -> "Just now"
        timeDiff < 60 * 60 * 1000 -> "${timeDiff / (60 * 1000)} min ago"
        timeDiff < 24 * 60 * 60 * 1000 -> "${timeDiff / (60 * 60 * 1000)} hr ago"
        else -> "${timeDiff / (24 * 60 * 60 * 1000)} days ago"
    }
}

// Data class for department budget tracking
data class DepartmentBudgetData(
    val budget: Double,
    val spent: Double,
    val remaining: Double
)

// Helper function to calculate department budgets from expenses
fun calculateDepartmentBudgets(expenses: List<Expense>, totalProjectBudget: Double): Map<String, DepartmentBudgetData> {
    if (expenses.isEmpty()) return emptyMap()

    val departmentSpending = expenses.groupBy { it.department }
        .mapValues { (_, expenseList) ->
            expenseList.sumOf { it.amount }
        }

    val totalSpent = departmentSpending.values.sum()
    val budgetPercentage = if (totalSpent > 0) totalProjectBudget / totalSpent else 1.0

    return departmentSpending.mapValues { (_, spent) ->
        val allocatedBudget = maxOf(spent * budgetPercentage, spent + 50000.0)
        val remaining = allocatedBudget - spent
        
        DepartmentBudgetData(
            budget = allocatedBudget,
            spent = spent,
            remaining = remaining
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionHeadHomeScreen(
    navController: NavHostController,
    projectId: String,
    onBack: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    BackHandler {
        onBack()
    }

    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var projectExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var departmentBudgets by remember { mutableStateOf<Map<String, DepartmentBudgetData>>(emptyMap()) }
    var isLoadingProject by remember { mutableStateOf(true) }
    var projectError by remember { mutableStateOf<String?>(null) }

    var recentExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var isLoadingNotifications by remember { mutableStateOf(true) }
    var productionHeadNotifications by remember { mutableStateOf<List<NotificationData>>(emptyList()) }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(projectId) {
        scope.launch {
            try {
                isLoadingProject = true
                projectError = null
                
                val projectRepository = ProjectRepository()
                projectRepository.getProjectById(projectId).fold(
                    onSuccess = { project ->
                        selectedProject = project
                        android.util.Log.d("ProductionHeadDashboard", "✅ Loaded project: ${project.name}")
                    },
                    onFailure = { e ->
                        projectError = "Failed to load project: ${e.message}"
                        android.util.Log.e("ProductionHeadDashboard", "❌ Error loading project", e)
                    }
                )

                val expenseRepository = ExpenseRepository()
                expenseRepository.getAllExpenses().fold(
                    onSuccess = { allExpenses ->
                        projectExpenses = allExpenses.filter { expense -> 
                            expense.projectId == projectId && expense.status == ExpenseStatus.APPROVED 
                        }
                        android.util.Log.d("ProductionHeadDashboard", "✅ Loaded ${projectExpenses.size} approved expenses for project")
                        
                        departmentBudgets = calculateDepartmentBudgets(projectExpenses, selectedProject?.budget ?: 0.0)
                    },
                    onFailure = { e ->
                        projectError = "Failed to load expenses: ${e.message}"
                        android.util.Log.e("ProductionHeadDashboard", "❌ Error loading expenses", e)
                    }
                )
                
                isLoadingProject = false
            } catch (e: Exception) {
                projectError = "Error loading project data: ${e.message}"
                isLoadingProject = false
            }
        }
    }

    fun loadProductionHeadNotifications() {
        scope.launch {
            try {
                val notificationRepository = NotificationRepository(context)
                val expenseRepository = ExpenseRepository()
                val authRepository = AuthRepository()
                val currentProductionHeadId = authRepository.getCurrentUserPhoneNumber() ?: "+919481484157"

                val storedNotificationsResult = notificationRepository.getNotificationsForUser(
                    userId = currentProductionHeadId,
                    userRole = UserRole.PRODUCTION_HEAD
                )

                val recentExpensesResult = expenseRepository.getAllExpenses()
                val combinedNotifications = mutableListOf<NotificationData>()

                storedNotificationsResult.fold(
                    onSuccess = { notifications ->
                        combinedNotifications.addAll(notifications)
                    },
                    onFailure = { }
                )

                recentExpensesResult.fold(
                    onSuccess = { expenses ->
                        val currentTime = System.currentTimeMillis()
                        val oneDayAgo = currentTime - (24 * 60 * 60 * 1000)

                        val recentSubmissions = expenses.filter { expense ->
                            val expenseTime = expense.submittedAt.seconds * 1000
                            expenseTime > oneDayAgo && expense.projectId == projectId
                        }

                        recentSubmissions.forEach { expense ->
                            if (expense.status == ExpenseStatus.PENDING) {
                                val submissionNotification = NotificationData(
                                    id = "dynamic_new_submission_${expense.id}",
                                    title = "New expense submitted:",
                                    message = "${expense.department}, ${formatIndianNumber(expense.amount)}",
                                    type = NotificationType.EXPENSE_SUBMITTED,
                                    recipientId = currentProductionHeadId,
                                    senderId = expense.submittedById,
                                    expenseId = expense.id,
                                    projectId = expense.projectId,
                                    departmentId = expense.department,
                                    amount = expense.amount,
                                    isRead = false,
                                    createdAt = expense.submittedAt
                                )

                                if (!combinedNotifications.any { it.expenseId == expense.id && it.type == NotificationType.EXPENSE_SUBMITTED }) {
                                    combinedNotifications.add(submissionNotification)
                                }
                            }
                        }

                        // Remove pending expenses summary - only show individual expense submissions
                        android.util.Log.d("ProductionHeadNotifications", "✅ Skipping pending summary - showing only individual submissions")
                    },
                    onFailure = { }
                )

                val sortedNotifications = combinedNotifications
                    .distinctBy { "${it.type}_${it.expenseId}_${it.title}" }
                    .sortedByDescending { it.createdAt.seconds }
                    .take(10)

                productionHeadNotifications = sortedNotifications
                isLoadingNotifications = false
            } catch (e: Exception) {
                isLoadingNotifications = false
            }
        }
    }

    fun loadPendingExpensesForProductionHead() {
        scope.launch {
            try {
                val expenseRepository = ExpenseRepository()
                expenseRepository.getPendingExpenses().fold(
                    onSuccess = { expenses ->
                        recentExpenses = expenses.filter { it.projectId == projectId }
                            .sortedByDescending { it.submittedAt.seconds }
                            .take(10)
                        isLoadingNotifications = false
                    },
                    onFailure = { isLoadingNotifications = false }
                )
            } catch (e: Exception) {
                isLoadingNotifications = false
            }
        }
    }

    LaunchedEffect(refreshTrigger) {
        loadProductionHeadNotifications()
        loadPendingExpensesForProductionHead()

        while (true) {
            kotlinx.coroutines.delay(15_000)
            loadProductionHeadNotifications()
            loadPendingExpensesForProductionHead()
        }
    }

    val drawerItems = listOf("Dashboard", "Pending Approvals", "Add Expenses")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    drawerItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                when (item) {
                                    "Dashboard" -> { }
                                    "Pending Approvals" -> navController.navigate("pending_approvals/$projectId")
                                    "Add Expenses" -> navController.navigate("new_expense/$projectId")
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "AVR ENTERTAINMENT",
                        color = Color(0xFF4169E1),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    Row {
                        IconButton(onClick = { onBack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF4169E1))
                        }
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF4169E1))
                        }
                    }
                },
                actions = {
                    // Notifications
                    var showNotifications by remember { mutableStateOf(false) }
                    
                    Box {
                        IconButton(onClick = {
                            showNotifications = !showNotifications
                            if (showNotifications) {
                                refreshTrigger++
                            }
                        }) {
                            val unreadCount = productionHeadNotifications.count { !it.isRead }
                            if (unreadCount > 0) {
                                Badge(containerColor = Color.Red) {
                                    Text("$unreadCount", color = Color.White, fontSize = 12.sp)
                                }
                            }
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFF4169E1))
                        }

                        DropdownMenu(
                            expanded = showNotifications,
                            onDismissRequest = { showNotifications = false },
                            modifier = Modifier.width(320.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "Notifications",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                if (isLoadingNotifications) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                } else if (productionHeadNotifications.isEmpty()) {
                                    Text(
                                        "No notifications",
                                        fontSize = 14.sp,
                                        color = Color(0xFF999999),
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    productionHeadNotifications.take(4).forEachIndexed { index, notification ->
                                        val timeAgo = getTimeAgo(notification.createdAt.seconds * 1000)

                                        NotificationItem(
                                            title = notification.title,
                                            description = notification.message,
                                            time = timeAgo,
                                            isRead = notification.isRead
                                        )
                                        
                                        if (index < productionHeadNotifications.take(4).size - 1) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }

                                    if (productionHeadNotifications.size > 4) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "+${productionHeadNotifications.size - 4} more notifications",
                                            fontSize = 12.sp,
                                            color = Color(0xFF666666),
                                            fontStyle = FontStyle.Italic,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    "View all",
                                    color = Color(0xFF1565C0),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .clickable { }
                                )
                            }
                        }
                    }
                }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column {
                    Text(
                        "Dashboard",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFF2E2E2E)
                        )
                        selectedProject?.let { project ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                project.name,
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                color = Color(0xFF1565C0)
                            )
                            Text(
                    "Budget: ${formatIndianNumber(project.budget)}",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                        }
                    }
                }

                item {
                    selectedProject?.let { project ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Assessment,
                                        contentDescription = "Budget",
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Budget Summary",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF2E2E2E)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            "Total Budget",
                                            fontSize = 14.sp,
                                            color = Color(0xFF666666)
                                        )
                                        Text(
                                            formatIndianNumber(project.budget),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = Color(0xFF2E2E2E)
                                        )
                                    }
                                    
                                    Column {
                                        Text(
                                            "Spent",
                                            fontSize = 14.sp,
                                            color = Color(0xFF666666)
                                        )
                                        val totalSpent = departmentBudgets.values.sumOf { it.spent }
                                        Text(
                                            formatIndianNumber(totalSpent),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = Color(0xFF2E2E2E)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        "View Report",
                                        color = Color(0xFF1565C0),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.clickable {
                                            navController.navigate("reports/$projectId")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    if (isLoadingProject) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (projectError != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    projectError!!,
                                    color = Color.Red,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        if (departmentBudgets.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(4.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "No expenses found for this project",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color(0xFF666666)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Department budgets will appear as expenses are added",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF999999),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            val departmentList = departmentBudgets.entries.toList()
                            val colors = listOf(
                                Color(0xFF1565C0), Color(0xFF4CAF50), Color(0xFF9C27B0),
                                Color(0xFFFF9800), Color(0xFFF44336), Color(0xFF607D8B)
                            )
                            
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                departmentList.chunked(2).forEachIndexed { chunkIndex, rowItems ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowItems.forEachIndexed { rowIndex, (department, budgetData) ->
                                            val globalIndex = chunkIndex * 2 + rowIndex
                        DepartmentBudgetCard(
                                                department = department,
                                                budget = budgetData.budget,
                                                spent = budgetData.spent,
                                                remaining = budgetData.remaining,
                                                color = colors[globalIndex % colors.size],
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(80.dp),
                                                onClick = {
                                                    navController.navigate("department_report/$projectId/$department")
                                                }
                                            )
                                        }
                                        if (rowItems.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Budget Allocated",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF2E2E2E)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PieChart(
                                    modifier = Modifier.size(120.dp),
                                    departmentBudgets = departmentBudgets
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (departmentBudgets.isNotEmpty()) {
                                        val departmentColors = mapOf(
                                            "Sound" to Color(0xFF1565C0),      
                                            "Other" to Color(0xFF4CAF50),      
                                            "Art" to Color(0xFF9C27B0),        
                                            "Camera" to Color(0xFFFF9800),     
                                            "Costumes" to Color(0xFFF44336),   
                                            "Lighting" to Color(0xFF607D8B),   
                                            "Set Design" to Color(0xFF795548), 
                                            "Set" to Color(0xFF795548)         
                                        )
                                        
                                        departmentBudgets.entries.take(6).forEach { (department, budgetData) ->
                                            val color = departmentColors[department] ?: Color.Gray
                                    LegendItem(
                                                color = color,
                                                label = "$department - ₹${String.format("%,.0f", budgetData.spent)}"
                                            )
                                        }
                                    } else {
                                        Text(
                                            "No expense data available",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF999999)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    title: String,
    description: String,
    time: String,
    isRead: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isRead) Color.Transparent else Color(0xFFF3F4F6),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = if (isRead) FontWeight.Normal else FontWeight.SemiBold,
            color = Color(0xFF2E2E2E),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            fontSize = 13.sp,
            color = Color(0xFF666666),
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = time,
            fontSize = 11.sp,
            color = Color(0xFF999999)
        )
    }
}

@Composable
fun DepartmentBudgetCard(
    department: String,
    budget: Double,
    spent: Double,
    remaining: Double,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = department,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Total: ₹${String.format("%.0f", budget)}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 10.sp
                    )
                    Text(
                        text = "Spent: ₹${String.format("%.0f", spent)}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Remaining: ₹${String.format("%.0f", remaining)}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    departmentBudgets: Map<String, DepartmentBudgetData>
) {
    val data = departmentBudgets.mapValues { it.value.spent }
    val total = data.values.sum()
    
    if (total <= 0) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No Spending",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF999999)
            )
        }
        return
    }

    val colors = listOf(
        Color(0xFF1565C0), Color(0xFF4CAF50), Color(0xFF9C27B0),
        Color(0xFFFF9800), Color(0xFFF44336), Color(0xFF607D8B)
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f * 0.8f
        
        var startAngle = -90f
        
        data.entries.forEachIndexed { index, (_, value) ->
            val sweepAngle = (value / total * 360f).toFloat()
            val color = colors[index % colors.size]
            
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = Size(radius * 2, radius * 2)
            )
            
            startAngle += sweepAngle
        }
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF2E2E2E),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
} 