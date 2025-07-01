@file:OptIn(ExperimentalMaterial3Api::class)
package com.deeksha.avrentertainment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.deeksha.avrentertainment.models.Expense
import com.deeksha.avrentertainment.models.ExpenseStatus
import com.deeksha.avrentertainment.models.Project
import com.deeksha.avrentertainment.repository.ExpenseRepository
import com.deeksha.avrentertainment.repository.ProjectRepository
import kotlinx.coroutines.launch
import android.widget.Toast
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import com.deeksha.avrentertainment.utils.ExportUtils
import com.deeksha.avrentertainment.utils.ReportData
import com.deeksha.avrentertainment.utils.DepartmentReportItem
import com.deeksha.avrentertainment.utils.ReportFilters

// Data class for budget overview table
data class BudgetOverviewItem(
    val department: String,
    val budget: Double,
    val spent: Double,
    val percentage: Double
)

// Data class for department analytics
data class DepartmentAnalytics(
    val name: String,
    val totalSpent: Double,
    val totalBudget: Double,
    val expenseCount: Int,
    val averageExpense: Double,
    val utilizationPercentage: Double
)

@Composable
fun NewReportsScreen(
    navController: NavHostController,
    projectRepository: ProjectRepository,
    expenseRepository: ExpenseRepository,
    projectId: String,
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // State variables
    var allProjects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var allExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var filteredExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Filter states
    var selectedDateRange by remember { mutableStateOf("This Year") }
    var selectedDepartment by remember { mutableStateOf("All Departments") }
    var dateRangeExpanded by remember { mutableStateOf(false) }
    var departmentExpanded by remember { mutableStateOf(false) }
    
    // Available filter options
    var availableDepartments by remember { mutableStateOf<List<String>>(emptyList()) }
    var availableProjects by remember { mutableStateOf<List<String>>(emptyList()) }
    
    // Analytics data
    var departmentAnalytics by remember { mutableStateOf<List<DepartmentAnalytics>>(emptyList()) }
    var budgetOverviewData by remember { mutableStateOf<List<BudgetOverviewItem>>(emptyList()) }
    var totalSpent by remember { mutableStateOf(0.0) }
    var totalBudget by remember { mutableStateOf(0.0) }
    
    // Handle back press
    BackHandler {
        onBack()
    }
    
    // Load all data
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoading = true
                error = null
                
                // Load all projects
                projectRepository.getAllProjects().fold(
                    onSuccess = { projects ->
                        allProjects = projects
                        availableProjects = projects.map { it.name }
                        android.util.Log.d("ReportsScreen", "Loaded ${projects.size} projects")
                    },
                    onFailure = { e ->
                        error = "Failed to load projects: ${e.message}"
                        android.util.Log.e("ReportsScreen", "Error loading projects", e)
                    }
                )
                
                // Load all expenses
                expenseRepository.getAllExpenses().fold(
                    onSuccess = { expenses ->
                        allExpenses = expenses
                        filteredExpenses = expenses
                        availableDepartments = expenses.map { it.department }.distinct().sorted()
                        
                        android.util.Log.d("ReportsScreen", "Loaded ${expenses.size} expenses")
                    },
                    onFailure = { e ->
                        error = "Failed to load expenses: ${e.message}"
                        android.util.Log.e("ReportsScreen", "Error loading expenses", e)
                    }
                )
                
                isLoading = false
            } catch (e: Exception) {
                error = "Error loading data: ${e.message}"
                isLoading = false
                android.util.Log.e("ReportsScreen", "Unexpected error", e)
            }
        }
    }
    
    // Filter expenses and recalculate data when filter changes
    LaunchedEffect(selectedDateRange, selectedDepartment, projectId, allExpenses, allProjects) {
        // Filter expenses by all criteria
        filteredExpenses = allExpenses.filter { expense ->
            // Date range filter
            val dateMatch = when (selectedDateRange) {
                "All Time" -> true
                "This Month" -> {
                    val calendar = java.util.Calendar.getInstance()
                    val currentMonth = calendar.get(java.util.Calendar.MONTH)
                    val currentYear = calendar.get(java.util.Calendar.YEAR)
                    
                    calendar.timeInMillis = expense.submittedAt.seconds * 1000
                    val expenseMonth = calendar.get(java.util.Calendar.MONTH)
                    val expenseYear = calendar.get(java.util.Calendar.YEAR)
                    
                    expenseMonth == currentMonth && expenseYear == currentYear
                }
                "Last 3 Months" -> {
                    val calendar = java.util.Calendar.getInstance()
                    calendar.add(java.util.Calendar.MONTH, -3)
                    val threeMonthsAgo = calendar.timeInMillis / 1000
                    
                    expense.submittedAt.seconds >= threeMonthsAgo
                }
                "Last 6 Months" -> {
                    val calendar = java.util.Calendar.getInstance()
                    calendar.add(java.util.Calendar.MONTH, -6)
                    val sixMonthsAgo = calendar.timeInMillis / 1000
                    
                    expense.submittedAt.seconds >= sixMonthsAgo
                }
                "This Year" -> {
                    val calendar = java.util.Calendar.getInstance()
                    val currentYear = calendar.get(java.util.Calendar.YEAR)
                    
                    calendar.timeInMillis = expense.submittedAt.seconds * 1000
                    val expenseYear = calendar.get(java.util.Calendar.YEAR)
                    
                    expenseYear == currentYear
                }
                else -> true
            }
            
            // Department filter
            val departmentMatch = selectedDepartment == "All Departments" || expense.department == selectedDepartment
            
            // Project filter - only show expenses for the specific project
            val projectMatch = expense.projectId == projectId
            
            dateMatch && departmentMatch && projectMatch
        }
        
        // Calculate department analytics (only approved expenses)
        val approvedExpenses = filteredExpenses.filter { it.status == ExpenseStatus.APPROVED }
        
        // Calculate total budget and spent for the specific project
        val projectsToConsider = allProjects.filter { it.id == projectId }
        
        totalBudget = projectsToConsider.sumOf { it.budget }
        totalSpent = approvedExpenses.sumOf { it.amount }
        
        // Calculate department analytics
        val departmentGroups = approvedExpenses.groupBy { it.department }
        val departmentBudgetMap = mutableMapOf<String, Double>()
        
        // Calculate budget allocation per department
        projectsToConsider.forEach { project ->
            val deptBudget = project.budget / 5.0 // Equal split among 5 departments
            listOf("Art", "Camera", "Costumes", "Other", "Sound").forEach { dept ->
                departmentBudgetMap[dept] = (departmentBudgetMap[dept] ?: 0.0) + deptBudget
            }
        }
        
        departmentAnalytics = departmentGroups.map { (dept, expenses) ->
            val spent = expenses.sumOf { it.amount }
            val budget = departmentBudgetMap[dept] ?: 0.0
            val utilizationPercentage = if (budget > 0) (spent / budget) * 100 else 0.0
            
            DepartmentAnalytics(
                name = dept,
                totalSpent = spent,
                totalBudget = budget,
                expenseCount = expenses.size,
                averageExpense = if (expenses.isNotEmpty()) spent / expenses.size else 0.0,
                utilizationPercentage = utilizationPercentage
            )
        }.sortedByDescending { it.totalSpent }
        
        // Create budget overview data
        budgetOverviewData = departmentBudgetMap.map { (dept, budget) ->
            val spent = departmentGroups[dept]?.sumOf { it.amount } ?: 0.0
            val percentage = if (budget > 0) (spent / budget) * 100 else 0.0
            
            BudgetOverviewItem(
                department = dept,
                budget = budget,
                spent = spent,
                percentage = percentage
            )
        }.sortedByDescending { it.spent }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    "Reports",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7B68EE))
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color(0xFF7B68EE)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Loading reports data...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF666666)
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
                        "Error: $error",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                error = null
                                // Reload data logic here
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B68EE))
                    ) {
                        Text("Retry", color = Color.White)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Filter Row
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Date Range Filter
                                ExposedDropdownMenuBox(
                                    expanded = dateRangeExpanded,
                                    onExpandedChange = { dateRangeExpanded = !dateRangeExpanded },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = selectedDateRange,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Time Range") },
                                        trailingIcon = {
                                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF7B68EE),
                                            focusedLabelColor = Color(0xFF7B68EE)
                                        )
                                    )
                                    
                                    ExposedDropdownMenu(
                                        expanded = dateRangeExpanded,
                                        onDismissRequest = { dateRangeExpanded = false }
                                    ) {
                                        listOf("All Time", "This Month", "Last 3 Months", "Last 6 Months", "This Year").forEach { range ->
                                            DropdownMenuItem(
                                                text = { Text(range) },
                                                onClick = {
                                                    selectedDateRange = range
                                                    dateRangeExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                
                                // Department Filter
                                ExposedDropdownMenuBox(
                                    expanded = departmentExpanded,
                                    onExpandedChange = { departmentExpanded = !departmentExpanded },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = selectedDepartment,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Department") },
                                        trailingIcon = {
                                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF7B68EE),
                                            focusedLabelColor = Color(0xFF7B68EE)
                                        )
                                    )
                                    
                                    ExposedDropdownMenu(
                                        expanded = departmentExpanded,
                                        onDismissRequest = { departmentExpanded = false }
                                    ) {
                                        listOf("All Departments").plus(availableDepartments).forEach { dept ->
                                            DropdownMenuItem(
                                                text = { Text(dept) },
                                                onClick = {
                                                    selectedDepartment = dept
                                                    departmentExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            

                        }
                    }
                }
                
                // Summary Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total Spent Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF4285F4)),
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Total Spent",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    formatIndianNumber(totalSpent),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        // Budget Utilization Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF34A853)),
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Budget Usage",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val percentage = if (totalBudget > 0) (totalSpent / totalBudget) * 100 else 0.0
                                Text(
                                    "${String.format("%.1f", percentage)}%",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                

                
                // Subcategory Split (Bar Chart) - Exactly like your image
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Subcategory Split",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF2E2E2E)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (departmentAnalytics.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No data available",
                                        color = Color(0xFF666666),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            } else {
                                DynamicBarChart(
                                    data = departmentAnalytics.associate { it.name to it.totalSpent },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    budgetData = departmentAnalytics.associate { it.name to it.totalBudget }
                                )
                            }
                        }
                    }
                }
                
                // Budget Overview Table - Exactly like your image
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Budget Overview Table",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF2E2E2E)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Table Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Dept",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "Budget",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "Spent",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFFE0E0E0))
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Table Rows
                            budgetOverviewData.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        item.department,
                                        color = Color(0xFF2E2E2E),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        formatIndianNumber(item.budget),
                                        color = Color(0xFF2E2E2E),
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        formatIndianNumber(item.spent),
                                        color = Color(0xFF34A853),
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Export Buttons - Exactly like your image
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Export PDF Button
                            Button(
                                onClick = {
                                    val reportData = ReportData(
                                        title = "Expense Report",
                                        totalSpent = totalSpent,
                                        totalBudget = totalBudget,
                                        departmentData = budgetOverviewData.map { item ->
                                            DepartmentReportItem(
                                                department = item.department,
                                                budget = item.budget,
                                                spent = item.spent,
                                                percentage = if (item.budget > 0) (item.spent / item.budget) * 100 else 0.0
                                            )
                                        },
                                        filters = ReportFilters(
                                            dateRange = selectedDateRange,
                                            department = selectedDepartment,
                                            project = allProjects.find { it.id == projectId }?.name ?: "Unknown Project"
                                        )
                                    )
                                    
                                    ExportUtils.exportToPDF(
                                        context = context,
                                        reportData = reportData,
                                        onSuccess = { file ->
                                            Toast.makeText(context, "PDF exported successfully!", Toast.LENGTH_SHORT).show()
                                            ExportUtils.shareFile(context, file, "application/pdf")
                                        },
                                        onError = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Export PDF",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            // Export Excel Button
                            Button(
                                onClick = {
                                    val reportData = ReportData(
                                        title = "Expense Report",
                                        totalSpent = totalSpent,
                                        totalBudget = totalBudget,
                                        departmentData = budgetOverviewData.map { item ->
                                            DepartmentReportItem(
                                                department = item.department,
                                                budget = item.budget,
                                                spent = item.spent,
                                                percentage = if (item.budget > 0) (item.spent / item.budget) * 100 else 0.0
                                            )
                                        },
                                        filters = ReportFilters(
                                            dateRange = selectedDateRange,
                                            department = selectedDepartment,
                                            project = allProjects.find { it.id == projectId }?.name ?: "Unknown Project"
                                        )
                                    )
                                    
                                    ExportUtils.exportToExcel(
                                        context = context,
                                        reportData = reportData,
                                        onSuccess = { file ->
                                            Toast.makeText(context, "Excel exported successfully!", Toast.LENGTH_SHORT).show()
                                            ExportUtils.shareFile(context, file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                                        },
                                        onError = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Export Excel",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Chart functions moved to MainActivity.kt to avoid conflicts 