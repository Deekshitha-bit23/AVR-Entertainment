@file:OptIn(ExperimentalMaterial3Api::class)
package com.deeksha.avrentertainment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.deeksha.avrentertainment.ui.theme.AVREntertainmentTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import com.google.firebase.FirebaseApp
import com.deeksha.avrentertainment.repository.BudgetRepository
import kotlinx.coroutines.launch
import android.content.Context
import com.deeksha.avrentertainment.models.Budget
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExperimentalMaterial3Api
import android.app.DatePickerDialog
import java.util.Calendar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.material.ModalDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Divider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.foundation.lazy.itemsIndexed
// Firebase Auth imports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.MainScope
// Additional imports for new features
import com.deeksha.avrentertainment.models.*
import com.deeksha.avrentertainment.repository.*
import com.deeksha.avrentertainment.viewmodels.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deeksha.avrentertainment.ui.screens.ProductionHeadHomeScreen
import com.deeksha.avrentertainment.ui.screens.CreateProjectScreen
import com.deeksha.avrentertainment.ui.screens.CreateUserScreen
import com.deeksha.avrentertainment.NewReportsScreen
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.RadioButton
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.compose.ui.graphics.Brush
import com.google.firebase.messaging.FirebaseMessaging
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.deeksha.avrentertainment.services.LocalNotificationService
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.deeksha.avrentertainment.services.SMSBroadcastReceiver
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.rememberCoroutineScope
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.cos
import kotlin.math.sin
import android.widget.Toast
import com.deeksha.avrentertainment.utils.ExportUtils
import com.deeksha.avrentertainment.utils.ReportData
import com.deeksha.avrentertainment.utils.DepartmentReportItem
import com.deeksha.avrentertainment.utils.ReportFilters
import java.text.SimpleDateFormat

// Indian currency formatter function
fun formatIndianCurrency(amount: Double): String {
    return when {
        amount >= 10000000 -> { // 1 crore and above
            val crores = amount / 10000000
            "₹${String.format("%.1f", crores).trimEnd('0').trimEnd('.')} Cr"
        }
        amount >= 100000 -> { // 1 lakh and above
            val lakhs = amount / 100000
            "₹${String.format("%.1f", lakhs).trimEnd('0').trimEnd('.')} L"
        }
        amount >= 1000 -> { // 1 thousand and above
            val thousands = amount / 1000
            "₹${String.format("%.1f", thousands).trimEnd('0').trimEnd('.')} K"
        }
        else -> {
            "₹${String.format("%.0f", amount)}"
        }
    }
}

// Detailed Indian number formatting with proper comma placement
fun formatIndianNumber(amount: Double): String {
    val formatter = DecimalFormat()
    val symbols = DecimalFormatSymbols(Locale("en", "IN"))
    formatter.decimalFormatSymbols = symbols
    
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

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private lateinit var budgetRepository: BudgetRepository
    private lateinit var projectRepository: ProjectRepository
    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var authRepository: AuthRepository

    // Notification permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, initialize FCM
            initializeFCM()
        } else {
            // Permission denied
            android.util.Log.w("FCM", "Notification permission denied")
        }
    }

    // SMS permission launcher for automatic OTP reading
    private val requestSMSPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            android.util.Log.d("SMS", "SMS permission granted for automatic OTP reading")
        } else {
            android.util.Log.w("SMS", "SMS permission denied - manual OTP entry required")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Custom back navigation logic
        // This will be handled by the NavHost in Compose
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up secure OTP manager on app destroy
        com.deeksha.avrentertainment.services.SecureOTPManager.getInstance().onApplicationDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        budgetRepository = BudgetRepository()
        projectRepository = ProjectRepository()
        expenseRepository = ExpenseRepository()
        notificationRepository = NotificationRepository(this)
        authRepository = AuthRepository()

        // Request notification permission and initialize FCM
        requestNotificationPermission()

        // Request SMS permission for automatic OTP reading
        requestSMSPermission()

        enableEdgeToEdge()
        setContent {
            AVREntertainmentTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()

                // Create the pre-production budget and default projects when the app starts
                LaunchedEffect(Unit) {
                    scope.launch {
                        try {
                            // First, migrate any existing projects with Long timestamps to proper Timestamps
                            projectRepository.migrateProjectTimestamps().fold(
                                onSuccess = {
                                    android.util.Log.d("Firebase", "Successfully migrated project timestamps")
                                },
                                onFailure = { e ->
                                    android.util.Log.w("Firebase", "Migration completed with some warnings: ${e.message}")
                                }
                            )

                            // Create default budget
                            val budget = budgetRepository.createPreProductionBudget()
                            budgetRepository.createBudget(budget)

                            // Create default projects (this will only create projects that don't already exist)
                            projectRepository.createDefaultProjects()

                            // Create sample users for testing (this will only create users that don't already exist)
                            authRepository.createSampleUsers()

                            // AGGRESSIVE CLEANUP: Remove ALL existing budget notifications on startup
                            android.util.Log.d("StartupCleanup", "🧹 AGGRESSIVE STARTUP CLEANUP: Removing all duplicate budget notifications...")
                            notificationRepository.cleanupDuplicateBudgetNotifications()

                            // Additional cleanup: Remove any lingering budget exceeded notifications
                            notificationRepository.performPeriodicNotificationCleanup()

                            // Create sample notifications for all users to demonstrate the system
                            android.util.Log.d("Firebase", "Creating sample notifications for all users...")
                            notificationRepository.ensureAllUsersHaveNotifications()
                            
                            // Also create for current user if logged in
                            notificationRepository.createNotificationsForCurrentUser()

                            android.util.Log.d("Firebase", "Successfully initialized default data")

                            // Start notification monitoring services
                            startNotificationServices()
                        } catch (e: Exception) {
                            android.util.Log.e("Firebase", "Error initializing data: ${e.message}")
                            // Show user-friendly error if needed
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        projectRepository = projectRepository,
                        expenseRepository = expenseRepository,
                        notificationRepository = notificationRepository
                    )
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    initializeFCM()
                }
                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For older versions, permission is granted by default
            initializeFCM()
        }
    }

    private fun requestSMSPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                android.util.Log.d("SMS", "SMS permission already granted")
            }
            else -> {
                // Request permission
                requestSMSPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
            }
        }
    }

    private fun initializeFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                android.util.Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            android.util.Log.d("FCM", "FCM Registration Token: $token")

            // Store token locally
            val sharedPrefs = getSharedPreferences("FCM_TOKEN", MODE_PRIVATE)
            sharedPrefs.edit().putString("token", token).apply()

            // TODO: Send token to your server/Firestore
        }
    }

    private fun openCamera() { /* Launch camera intent */ }
    private fun openGallery() { /* Launch gallery intent */ }
    private fun openPdfPicker() { /* Launch PDF picker intent */ }

    // SMART NOTIFICATION MONITORING - Reduced frequency to prevent spam
    private fun startNotificationServices() {
        lifecycleScope.launch {
            // Add a flag to control monitoring frequency
            var fullMonitoringEnabled = false // Start with reduced monitoring
            var monitoringCycle = 0

            // Start periodic checks with SMART frequency control
            while (true) {
                try {
                    monitoringCycle++
                    android.util.Log.d("NotificationService", "🔄 SMART Monitoring cycle #$monitoringCycle")

                    // REDUCED FREQUENCY: Check pending approvals less frequently to prevent spam
                    if (monitoringCycle % 3 == 0) { // Every 3rd cycle (every 1.5 minutes instead of 30 seconds)
                        android.util.Log.d("NotificationService", "📋 Checking pending approvals (reduced frequency)")
                        expenseRepository.checkPendingApprovalsAndNotify(
                            notificationRepository = notificationRepository,
                            projectRepository = projectRepository
                        )
                    }

                    // Enable full monitoring after 10 minutes to prevent startup spam
                    if (monitoringCycle > 20) { // 20 cycles * 30 seconds = 10 minutes
                        fullMonitoringEnabled = true
                    }

                    // VERY REDUCED FREQUENCY: Check budget limits much less frequently
                    if (fullMonitoringEnabled && monitoringCycle % 10 == 0) { // Every 10th cycle (every 5 minutes)
                        android.util.Log.d("NotificationService", "💰 Budget monitoring enabled - checking limits (very reduced frequency)")
                        projectRepository.getActiveProjects().fold(
                            onSuccess = { projects: List<Project> ->
                                projects.forEach { project: Project ->
                                    budgetRepository.checkBudgetLimitsAndNotify(
                                        projectId = project.id,
                                        projectName = project.name,
                                        expenseRepository = expenseRepository,
                                        notificationRepository = notificationRepository
                                    )
                                }
                            },
                            onFailure = { e: Throwable ->
                                android.util.Log.w("NotificationService", "Failed to check project budgets: ${e.message}")
                            }
                        )
                    } else if (!fullMonitoringEnabled) {
                        android.util.Log.d("NotificationService", "⏰ Full monitoring disabled (warming up period...)")
                    }

                    android.util.Log.d("NotificationService", "✅ Completed SMART monitoring cycle #$monitoringCycle")

                    // Wait 30 seconds before next check
                    kotlinx.coroutines.delay(30 * 1000) // 30 seconds
                } catch (e: Exception) {
                    android.util.Log.e("NotificationService", "❌ Error in notification service: ${e.message}")
                    // Wait 10 minutes before retrying on error (increased from 5 minutes)
                    kotlinx.coroutines.delay(10 * 60 * 1000) // 10 minutes
                }
            }
        }
    }

    // BUDGET MANAGEMENT AND NOTIFICATION SYSTEM

    // Simulate budget changes (for testing purposes)
    private fun simulateBudgetChange(projectId: String, projectName: String, department: String, amount: Double, changeType: String) {
        lifecycleScope.launch {
            android.util.Log.d("BudgetSimulation", "💰 Simulating $changeType of ₹$amount for $department in $projectName")

            // Use the enhanced notification orchestrator
            val notificationType = if (changeType == "added") NotificationType.BUDGET_ADDED else NotificationType.BUDGET_DEDUCTED

            notificationRepository.sendRoleSpecificNotifications(
                notificationType = notificationType,
                projectId = projectId,
                projectName = projectName,
                department = department,
                amount = amount,
                senderId = "admin"
            )
        }
    }

    // Simulate budget exceeded scenarios for testing
    private fun simulateBudgetExceeded(projectId: String, projectName: String, department: String?, isProjectLevel: Boolean = false) {
        lifecycleScope.launch {
            val scope = if (isProjectLevel) "project" else "department"
            android.util.Log.d("BudgetSimulation", "⚠️ Simulating budget exceeded for $scope: ${department ?: projectName}")

            val notificationType = if (isProjectLevel) NotificationType.BUDGET_EXCEEDED_PROJECT else NotificationType.BUDGET_EXCEEDED_DEPARTMENT
            val currentSpent = if (isProjectLevel) 5500000.0 else 110000.0 // Mock values
            val budgetLimit = if (isProjectLevel) 5000000.0 else 100000.0 // Mock values

            notificationRepository.sendRoleSpecificNotifications(
                notificationType = notificationType,
                projectId = projectId,
                projectName = projectName,
                department = department,
                additionalData = mapOf(
                    "currentSpent" to currentSpent,
                    "budgetLimit" to budgetLimit
                )
            )
        }
    }

    // Test all notification types (for development/testing)
    private fun testAllNotificationTypes() {
        lifecycleScope.launch {
            android.util.Log.d("NotificationTest", "🧪 Testing all notification types")

            // Test budget changes (both project and department level)
            simulateBudgetChange("project1", "Movie Production A", "Costumes", 25000.0, "added")
            kotlinx.coroutines.delay(2000)
            simulateBudgetChange("project1", "Movie Production A", "Equipment", 15000.0, "deducted")
            kotlinx.coroutines.delay(2000)
            
            // Test project-level budget change
            simulateProjectBudgetChange("project1", "Movie Production A", 100000.0, "added")
            kotlinx.coroutines.delay(2000)

            // Test budget exceeded scenarios
            simulateBudgetExceeded("project1", "Movie Production A", "Camera", false)
            kotlinx.coroutines.delay(2000)
            simulateBudgetExceeded("project1", "Movie Production A", null, true)
            kotlinx.coroutines.delay(2000)
            
            // Test pending approval notifications
            simulatePendingApprovalReminder("project1", "Movie Production A", 5, 75000.0)

            android.util.Log.d("NotificationTest", "✅ All notification types tested")
        }
    }
    

    
    // Add method to simulate project-level budget changes
    private fun simulateProjectBudgetChange(projectId: String, projectName: String, amount: Double, changeType: String) {
        lifecycleScope.launch {
            android.util.Log.d("BudgetSimulation", "💰 Simulating project-level $changeType of ₹$amount for $projectName")

            val notificationType = if (changeType == "added") NotificationType.BUDGET_ADDED else NotificationType.BUDGET_DEDUCTED

            notificationRepository.sendRoleSpecificNotifications(
                notificationType = notificationType,
                projectId = projectId,
                projectName = projectName,
                department = null, // null for project-level
                amount = amount,
                senderId = "admin"
            )
        }
    }
    
    // Add method to simulate pending approval reminders
    private fun simulatePendingApprovalReminder(projectId: String, projectName: String, pendingCount: Int, totalAmount: Double) {
        lifecycleScope.launch {
            android.util.Log.d("BudgetSimulation", "⏰ Simulating pending approval reminder for $projectName")

            notificationRepository.sendRoleSpecificNotifications(
                notificationType = NotificationType.PENDING_APPROVAL_REMINDER,
                projectId = projectId,
                projectName = projectName,
                amount = totalAmount,
                additionalData = mapOf(
                    "pendingCount" to pendingCount,
                    "expenseDetails" to emptyList<Expense>()
                )
            )
        }
    }
    // Function to calculate days left
    private fun calculateDaysLeft(endDate: String): Int {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val end = dateFormat.parse(endDate)
            val today = Calendar.getInstance().time
            val diff = end.time - today.time
            (diff / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            -1 // Return -1 if there's an error parsing the date
        }
    }


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier, projectRepository: ProjectRepository, expenseRepository: ExpenseRepository, notificationRepository: NotificationRepository) {
    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
        composable("login") {
            SimplifiedLoginScreen(navController, notificationRepository)
        }
        composable("unknown_user") {
            UnknownUserScreen(
                onBack = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("no_role_assigned") {
            NoRoleAssignedScreen(
                onBack = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }


        composable("log_expense") {
            LogExpenseScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("track_submissions/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            TrackSubmissionsScreen(
                projectId = projectId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("chat") {
            ChatScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("review_approvals") {
            ReviewApprovalsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("reports") {
            ReportsScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }
        composable("delegates") {
            DelegatesScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("export_reports") {
            ExportReportsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "pending_approvals/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            PendingApprovalsScreen(
                navController = navController,
                projectId = projectId,
                expenseRepository = expenseRepository,
                notificationRepository = notificationRepository,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            "approval_modal/{expenseId}",
            arguments = listOf(navArgument("expenseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId") ?: ""
            ApprovalModalScreen(
                navController = navController,
                expenseId = expenseId,
                expenseRepository = expenseRepository,
                notificationRepository = notificationRepository,
                onBack = { navController.popBackStack() }
            )
        }
        composable("project_selection/{userRole}",
            arguments = listOf(navArgument("userRole") { type = NavType.StringType })
        ) { backStackEntry ->
            val userRole = backStackEntry.arguments?.getString("userRole") ?: "team_member"
            ProjectSelectionScreen(
                userRole = userRole,
                onProjectSelected = { selectedProject ->
                    // Navigate to appropriate home based on role with selected project
                    when (userRole) {
                        "team_member" -> navController.navigate("team_home/${selectedProject.id}")
                        "approver" -> navController.navigate("approver_home/${selectedProject.id}")
                        "production_head" -> navController.navigate("production_head_home/${selectedProject.id}")
                    }
                },
                onBack = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                projectRepository = projectRepository,
                navController = navController
            )
        }
        composable("team_home/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            TeamMemberHomeScreen(
                navController = navController,
                projectId = projectId,
                onBack = {
                    navController.navigate("project_selection/team_member") {
                        popUpTo("project_selection/team_member") { inclusive = true }
                    }
                }
            )
        }
        composable("approver_home/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            ApproverHomeScreen(
                navController = navController,
                projectId = projectId,
                onBack = {
                    navController.navigate("project_selection/approver") {
                        popUpTo("project_selection/approver") { inclusive = true }
                    }
                }
            )
        }
        composable("production_head_home/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            ProductionHeadHomeScreen(
                navController = navController,
                projectId = projectId,
                onBack = {
                    navController.navigate("project_selection/production_head") {
                        popUpTo("project_selection/production_head") { inclusive = true }
                    }
                }
            )
        }
        composable("new_expense/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            NewExpenseScreen(
                projectId = projectId,
                onSubmit = { expenseData ->
                    // Handle submit (e.g., save to Firestore)
                },
                onBack = { 
                    // Navigate back to the previous screen instead of hardcoded team_member
                    navController.popBackStack()
                },
                projectRepository = projectRepository,
                expenseRepository = expenseRepository,
                notificationRepository = notificationRepository
            )
        }
        composable("department_report/{projectId}/{department}",
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType },
                navArgument("department") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            val department = backStackEntry.arguments?.getString("department") ?: ""
            DepartmentReportScreen(
                navController = navController,
                projectId = projectId,
                department = department,
                projectRepository = projectRepository,
                expenseRepository = expenseRepository,
                onBack = { navController.popBackStack() }
            )
        }
        composable("reports/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            NewReportsScreen(
                navController = navController,
                projectRepository = projectRepository,
                expenseRepository = expenseRepository,
                projectId = projectId,
                onBack = { navController.popBackStack() }
            )
        }
        // All Projects Reports Route
        composable("all_projects_reports") {
            NewReportsScreen(
                navController = navController,
                projectRepository = projectRepository,
                expenseRepository = expenseRepository,
                projectId = "ALL_PROJECTS", // Special identifier for all projects
                onBack = { navController.popBackStack() }
            )
        }
        composable("create_project") {
            CreateProjectScreen(
                navController = navController,
                onProjectCreated = {
                    // Navigate back to project selection
                    navController.navigate("project_selection/production_head") {
                        popUpTo("project_selection/production_head") { inclusive = true }
                    }
                }
            )
        }
        composable("create_user") {
            CreateUserScreen(
                navController = navController,
                onUserCreated = {
                    // Navigate back to project selection screen for production head
                    navController.navigate("project_selection/production_head") {
                        popUpTo("project_selection/production_head") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun SimplifiedLoginScreen(navController: NavHostController, notificationRepository: NotificationRepository) {
    val viewModel = remember { LoginViewModel() }
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Clean up secure OTP session when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            com.deeksha.avrentertainment.services.SecureOTPManager.getInstance().endSession("Login screen disposed")
            android.util.Log.d("SecureOTP", "🧹 Login screen disposed - OTP session ended")
        }
    }

    // Handle navigation based on authentication state
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            // End OTP session securely
            com.deeksha.avrentertainment.services.SecureOTPManager.getInstance().endSession("Authentication successful")
            
            // Track user login for smart push notifications and send login notifications
            scope.launch {
                try {
                    val authRepository = AuthRepository()
                    val currentUserPhone = authRepository.getCurrentUserPhoneNumber()
                    currentUserPhone?.let { phone ->
                        android.util.Log.d("LoginTracking", "📱 User $phone logged in - tracking for push notifications")
                        notificationRepository.trackUserLogin(phone)
                        
                        // Send role-specific login notifications after a short delay
                        state.userRole?.let { role ->
                            kotlinx.coroutines.delay(3000) // Wait 3 seconds after login
                            android.util.Log.d("LoginTracking", "🔔 Sending login notifications for $role")
                            notificationRepository.sendLoginNotifications(phone, role)
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("LoginTracking", "Failed to track user login: ${e.message}")
                }
            }
            
            when {
                state.isUnknownUser -> {
                    navController.navigate("unknown_user") {
                        popUpTo("login") { inclusive = true }
                    }
                }
                state.hasNoRole -> {
                    navController.navigate("no_role_assigned") {
                        popUpTo("login") { inclusive = true }
                    }
                }
                state.userRole != null -> {
                    val roleString = when (state.userRole) {
                        com.deeksha.avrentertainment.models.UserRole.USER -> "team_member"
                        com.deeksha.avrentertainment.models.UserRole.APPROVER -> "approver"
                        com.deeksha.avrentertainment.models.UserRole.PRODUCTION_HEAD -> "production_head"
                        null -> "team_member" // This shouldn't happen since we check != null above, but needed for exhaustive check
                    }
                    navController.navigate("project_selection/$roleString") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
    }

    // Auto-fill OTP when received
    LaunchedEffect(state.otpSent) {
        if (state.otpSent) {
            // Initialize secure OTP session
            com.deeksha.avrentertainment.services.SecureOTPManager.getInstance().initializeSecureSession(context)
            android.util.Log.d("SecureOTP", "🔐 Secure OTP session initialized")
        }
    }

    // Monitor secure OTP state
    val secureOTPManager = remember { com.deeksha.avrentertainment.services.SecureOTPManager.getInstance() }
    val otpState by secureOTPManager.otpState.collectAsState()
    
    // Auto-fill OTP when received securely
    LaunchedEffect(otpState.isOTPValid, otpState.lastOTPReceived) {
        if (otpState.isOTPValid && otpState.lastOTPReceived != null) {
            val decryptedOTP = secureOTPManager.getDecryptedOTP()
            if (decryptedOTP != null) {
                viewModel.updateOtp(decryptedOTP)
                android.util.Log.d("SecureOTP", "✅ OTP auto-filled securely: ${decryptedOTP.take(2)}****")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "AVR ENTERTAINMENT",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E5CFF)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Expense Management System",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Phone Number/OTP Input Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                if (!state.otpSent) {
                    // Phone Number Input
                    Text(
                        text = "Enter Phone Number",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E5CFF)
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+91",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        OutlinedTextField(
                            value = state.phoneNumber,
                            onValueChange = { newValue ->
                                val cleaned = newValue.filter { it.isDigit() }
                                if (cleaned.length <= 10) {
                                    viewModel.updatePhoneNumber(cleaned)
                                }
                            },
                            label = { Text("Phone Number") },
                            placeholder = { Text("Enter 10-digit number") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2E5CFF),
                                focusedLabelColor = Color(0xFF2E5CFF)
                            ),
                            singleLine = true,
                            enabled = !state.isLoading
                        )
                    }
                } else {
                    // OTP Input
                    Text(
                        text = "Enter OTP",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E5CFF)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "OTP sent to +91${state.phoneNumber}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Column {
                        OutlinedTextField(
                            value = state.otp,
                            onValueChange = { newValue ->
                                val cleaned = newValue.filter { it.isDigit() }
                                if (cleaned.length <= 6) {
                                    viewModel.updateOtp(cleaned)
                                }
                            },
                            label = { Text("Enter OTP") },
                            placeholder = { 
                                Text(
                                    if (otpState.isAutoFillActive) "Auto-fill active..." 
                                    else "Enter 6-digit OTP"
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (otpState.isAutoFillActive) Color(0xFF4CAF50) else Color(0xFF2E5CFF),
                                focusedLabelColor = if (otpState.isAutoFillActive) Color(0xFF4CAF50) else Color(0xFF2E5CFF),
                                unfocusedBorderColor = if (otpState.isAutoFillActive) Color(0xFF4CAF50) else Color.Gray
                            ),
                            singleLine = true,
                            enabled = !state.isLoading,
                            trailingIcon = {
                                if (otpState.isAutoFillActive) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Auto-fill active",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        )
                        
                        // Auto-fill status indicator
                        if (otpState.isAutoFillActive) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Auto-fill status",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when {
                                        otpState.autoFillSource != null -> "✅ OTP auto-filled from ${otpState.autoFillSource}"
                                        otpState.error != null -> "⚠️ ${otpState.error}"
                                        else -> "📱 Waiting for SMS..."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (otpState.error != null) Color.Red else Color(0xFF4CAF50)
                                )
                            }
                            
                            // Show remaining time if OTP is valid
                            if (otpState.isOTPValid && otpState.timeRemaining > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "⏱️ OTP valid for ${otpState.timeRemaining / 1000}s",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }

        // Send OTP / Verify Button
        Button(
            onClick = {
                if (!state.otpSent) {
                    // Send OTP (will trigger auto-fill)
                    if (state.phoneNumber.length == 10) {
                        viewModel.sendOtp(context as Activity)
                    }
                } else {
                    // Verify OTP
                    if (state.otp.length == 6) {
                        viewModel.verifyOtp()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E5CFF),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp),
            enabled = !state.isLoading && if (!state.otpSent) {
                state.phoneNumber.length == 10
            } else {
                state.otp.length == 6
            }
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = when {
                    state.isLoading -> "Please wait..."
                    !state.otpSent -> "Send OTP"
                    else -> "Verify & Login"
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        // Development Bypass Button (for testing)
        if (state.otpSent) {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = {
                    viewModel.bypassAuthentication()
                },
                enabled = !state.isLoading
            ) {
                Text(
                    text = "Skip for Testing",
                    color = Color(0xFF2E5CFF),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // Error Message
        state.error?.let { errorMsg ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMsg,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Resend OTP option
        if (state.otpSent) {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = {
                    viewModel.resetState()
                }
            ) {
                Text(
                    text = "Resend OTP",
                    color = Color(0xFF2E5CFF),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // Helper text
        if (state.phoneNumber.isNotEmpty() && !state.otpSent) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "We'll send an OTP to verify your phone number",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        // Development bypass for Firebase Auth blocking
        if (!state.otpSent) {
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(
                onClick = {
                    // Skip OTP and directly check role for development
                    viewModel.bypassAuthentication()
                }
            ) {
                Text(
                    text = "Development Login (Skip OTP)",
                    color = Color(0xFF2E5CFF),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Make sure to add users in Firebase Console:\nFirestore Database > users collection",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NoRoleAssignedScreen(onBack: () -> Unit = {}) {
    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "AVR ENTERTAINMENT",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E5CFF)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Expense Management System",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Message Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Color(0xFFFFEBEE),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚠️",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "No Role Assigned",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E5CFF)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No roles has been assigned to your account. Please contact admin regarding that.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }

        // Contact information
        Text(
            text = "Contact Administrator",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2E5CFF)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "admin@avrentertainment.com",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun ProjectSelectionScreen(
    userRole: String,
    onProjectSelected: (Project) -> Unit,
    onBack: () -> Unit = {},
    projectRepository: ProjectRepository,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // State variables
    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Notification states for approver
    var allProjectNotifications by remember { mutableStateOf<List<NotificationData>>(emptyList()) }
    var isLoadingNotifications by remember { mutableStateOf(true) }
    var showNotificationsModal by remember { mutableStateOf(false) }
    var totalNotificationCount by remember { mutableStateOf(0) }

    // Load active projects
    LaunchedEffect(Unit) {
        scope.launch {
            android.util.Log.d("ProjectSelection", "🔍 Loading active projects...")
            projectRepository.getActiveProjects().fold(
                onSuccess = { activeProjectList: List<Project> ->
                    projects = activeProjectList
                    isLoading = false
                    android.util.Log.d("ProjectSelection", "✅ Loaded ${projects.size} active projects")
                },
                onFailure = { e: Throwable ->
                    error = "Failed to load projects: ${e.message}"
                    isLoading = false
                    android.util.Log.e("ProjectSelection", "❌ Error loading projects", e)
                }
            )
        }
    }

    // Load notifications for all user roles
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoadingNotifications = true
                val authRepository = AuthRepository()
                val notificationRepository = NotificationRepository(context)
                val currentUserId = authRepository.getCurrentUserPhoneNumber()
                
                if (currentUserId != null) {
                    authRepository.getUserRole(currentUserId).fold(
                        onSuccess = { role ->
                            when (role) {
                                com.deeksha.avrentertainment.models.UserRole.APPROVER -> {
                                    // Load all project notifications for approver
                                    notificationRepository.getNotificationsForUser(currentUserId, role).fold(
                                        onSuccess = { notifications ->
                                            allProjectNotifications = notifications.take(20) // Limit to recent 20
                                            totalNotificationCount = notifications.filter { !it.isRead }.size
                                            android.util.Log.d("ProjectSelection", "✅ Loaded ${notifications.size} approver notifications, ${totalNotificationCount} unread")
                                        },
                                        onFailure = { e ->
                                            android.util.Log.e("ProjectSelection", "❌ Error loading approver notifications: ${e.message}")
                                        }
                                    )
                                }
                                com.deeksha.avrentertainment.models.UserRole.USER -> {
                                    // Load user-specific notifications (approval/rejection of their submitted expenses)
                                    notificationRepository.getNotificationsForUser(currentUserId, role).fold(
                                        onSuccess = { notifications ->
                                            allProjectNotifications = notifications.take(20) // Limit to recent 20
                                            totalNotificationCount = notifications.filter { !it.isRead }.size
                                            android.util.Log.d("ProjectSelection", "✅ Loaded ${notifications.size} user notifications, ${totalNotificationCount} unread")
                                        },
                                        onFailure = { e ->
                                            android.util.Log.e("ProjectSelection", "❌ Error loading user notifications: ${e.message}")
                                        }
                                    )
                                }
                                else -> {
                                    // Production Head gets no notifications as per business rules
                                    android.util.Log.d("ProjectSelection", "Production Head - no notifications loaded")
                                }
                            }
                        },
                        onFailure = { e ->
                            android.util.Log.e("ProjectSelection", "❌ Error getting user role: ${e.message}")
                        }
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("ProjectSelection", "❌ Exception loading notifications: ${e.message}")
            } finally {
                isLoadingNotifications = false
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            // Show plus icon only for Production Head
            if (userRole == "production_head") {
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
                        onClick = { navController.navigate("create_project") },
                        containerColor = Color(0xFF4169E1),
                        contentColor = Color.White,
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
        },
        containerColor = Color(0xFFF9F7F4)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9F7F4))
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "AVR ENTERTAINMENT",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E5CFF)
                        )
                    )
                    Text(
                        "Select Project",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
                // Action Icons - Show notifications for Users and Approvers, Analytics for Approvers and Production Heads
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notifications Icon - For Users and Approvers
                    if (userRole == "team_member" || userRole == "approver") {
                        Box {
                            IconButton(
                                onClick = { showNotificationsModal = true }
                            ) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = if (userRole == "team_member") "My Notifications" else "All Project Notifications",
                                    tint = Color(0xFF4169E1),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            // Notification Badge
                            if (totalNotificationCount > 0) {
                                Badge(
                                    modifier = Modifier
                                        .offset(x = 4.dp, y = (-4).dp)
                                        .size(18.dp),
                                    containerColor = Color(0xFFE53E3E),
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (totalNotificationCount > 99) "99+" else totalNotificationCount.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    
                    // Analytics Icon - Only for Approvers and Production Heads
                if (userRole == "approver" || userRole == "production_head") {
                    IconButton(
                        onClick = { 
                            navController.navigate("all_projects_reports")
                        }
                    ) {
                        Icon(
                            Icons.Default.Assessment,
                            contentDescription = "All Projects Report",
                            tint = Color(0xFF4169E1),
                            modifier = Modifier.size(28.dp)
                        )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
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
                            text = error!!,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                // Retry loading projects
                                isLoading = true
                                error = null
                                scope.launch {
                                    projectRepository.getActiveProjects().fold(
                                        onSuccess = { activeProjectList ->
                                            projects = activeProjectList
                                            isLoading = false
                                        },
                                        onFailure = { e ->
                                            error = "Failed to load projects: ${e.message}"
                                            isLoading = false
                                        }
                                    )
                                }
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                // Project list
                Text(
                    "Choose a project to continue:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333)
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(projects) { project ->
                        ProjectSelectionCard(
                            project = project,
                            onClick = { onProjectSelected(project) }
                        )
                    }

                    if (projects.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "No active projects found",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color(0xFF666666)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Create a new project to get started",
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

        // Notifications Modal
        if (showNotificationsModal) {
            Dialog(
                onDismissRequest = { showNotificationsModal = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (userRole == "team_member") "My Notifications" else "All Project Notifications",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E5CFF)
                                )
                            )
                            IconButton(onClick = { showNotificationsModal = false }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.Gray
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Notifications List
                        if (isLoadingNotifications) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF2E5CFF))
                            }
                        } else if (allProjectNotifications.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        if (userRole == "team_member") "No expense updates yet" else "No notifications yet",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.heightIn(max = 400.dp)
                            ) {
                                items(allProjectNotifications) { notification ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if ((notification.type == NotificationType.EXPENSE_APPROVED ||
                                                    notification.type == NotificationType.EXPENSE_REJECTED ||
                                                    notification.type == NotificationType.EXPENSE_SUBMITTED)
                                                    && !notification.projectId.isNullOrBlank()) {
                                                    navController.navigate("project_details/${notification.projectId}")
                                                }
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (notification.isRead) 
                                                Color(0xFFF8F9FA) 
                                            else 
                                                Color(0xFFE3F2FD)
                                        ),
                                        elevation = CardDefaults.cardElevation(2.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        notification.title,
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                            fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                                                            color = Color(0xFF2E5CFF)
                                                        )
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        notification.message,
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            color = Color(0xFF333333)
                                                        )
                                                    )
                                                }
                                                if (!notification.isRead) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(8.dp)
                                                            .background(
                                                                Color(0xFF2E5CFF),
                                                                CircleShape
                                                            )
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                "Recently",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color.Gray
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Mark All as Read Button
                        if (totalNotificationCount > 0) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        try {
                                            val notificationRepository = NotificationRepository(context)
                                            // Mark all notifications as read
                                            allProjectNotifications.filter { !it.isRead }.forEach { notification ->
                                                notificationRepository.markNotificationAsRead(notification.id)
                                            }
                                            // Update local state
                                            allProjectNotifications = allProjectNotifications.map { it.copy(isRead = true) }
                                            totalNotificationCount = 0
                                        } catch (e: Exception) {
                                            android.util.Log.e("ProjectSelection", "❌ Error marking notifications as read: ${e.message}")
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E5CFF)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    if (userRole == "team_member") "Mark All as Read" else "Mark All as Read",
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
@Composable
fun ProjectSelectionCard(
    project: Project,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Project icon/avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        Color(0xFF2E5CFF).copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = project.name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E5CFF)
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Project details
            Column(
                modifier = Modifier.weight(1f)
            ) {
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

                // Days left indicator
                val daysLeft = calculateDaysLeft(project.endDate)
                if (daysLeft >= 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Days Left",
                            tint = when {
                                daysLeft <= 7 -> Color.Red
                                daysLeft <= 14 -> Color(0xFFFF9800) // Orange
                                else -> Color(0xFF4CAF50) // Green
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Ends: ${project.endDate} ($daysLeft days left)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = when {
                                    daysLeft <= 7 -> Color.Red
                                    daysLeft <= 14 -> Color(0xFFFF9800) // Orange
                                    else -> Color(0xFF4CAF50) // Green
                                },
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Forward arrow
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Select Project",
                tint = Color(0xFF2E5CFF)
            )
        }
    }
}
}

@Composable
fun RoleSelectionScreen(onRoleSelected: (String) -> Unit, onBack: () -> Unit = {}) {
    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select Your Role", fontSize = 22.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { onRoleSelected("team_member") }) { Text("Team Member") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onRoleSelected("approver") }) { Text("Approver") }
    }
}

@Composable
fun TeamMemberHomeScreen(navController: NavHostController, projectId: String, onBack: () -> Unit = {}) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Dynamic project-specific data state
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var projectExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var total by remember { mutableStateOf(0.0) }
    var categories by remember { mutableStateOf<List<Pair<String, Double>>>(emptyList()) }
    var isLoadingExpenses by remember { mutableStateOf(true) }
    var expenseError by remember { mutableStateOf<String?>(null) }

    // State for user notifications
    var userNotifications by remember { mutableStateOf<List<NotificationData>>(emptyList()) }
    var isLoadingNotifications by remember { mutableStateOf(true) }
    var showNotifications by remember { mutableStateOf(false) }
    var showAllNotifications by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(0) } // Trigger to force notification refresh

    // Load project and expense data
    LaunchedEffect(projectId) {
        scope.launch {
            try {
                isLoadingExpenses = true
                expenseError = null
                
                // Load project details
                val projectRepository = ProjectRepository()
                projectRepository.getProjectById(projectId).fold(
                    onSuccess = { project ->
                        selectedProject = project
                        android.util.Log.d("TeamMemberExpenses", "✅ Loaded project: ${project.name}")
                    },
                    onFailure = { e ->
                        expenseError = "Failed to load project: ${e.message}"
                        android.util.Log.e("TeamMemberExpenses", "❌ Error loading project", e)
                    }
                )

                // Load project expenses (approved expenses only for expense summary)
                val expenseRepository = ExpenseRepository()
                expenseRepository.getAllExpenses().fold(
                    onSuccess = { allExpenses ->
                        // Filter expenses for this project only (approved expenses for totals)
                        val approvedProjectExpenses = allExpenses.filter { expense -> 
                            expense.projectId == projectId && expense.status == ExpenseStatus.APPROVED 
                        }
                        projectExpenses = approvedProjectExpenses
                        
                        // Calculate total and categories from actual data
                        total = approvedProjectExpenses.sumOf { it.amount }
                        
                        // Group by department/category and calculate totals
                        val categoryTotals = approvedProjectExpenses.groupBy { expense -> 
                            expense.department 
                        }.mapValues { (_, expenseList) ->
                            expenseList.sumOf { expense -> expense.amount }
                        }
                        
                        categories = categoryTotals.toList().sortedByDescending { it.second }
                        
                        android.util.Log.d("TeamMemberExpenses", "✅ Loaded ${approvedProjectExpenses.size} approved expenses")
                        android.util.Log.d("TeamMemberExpenses", "💰 Total: ₹${String.format("%.0f", total)}")
                        categories.forEach { (category, amount) ->
                            android.util.Log.d("TeamMemberExpenses", "📊 $category: ₹${String.format("%.0f", amount)}")
                        }
                    },
                    onFailure = { e ->
                        expenseError = "Failed to load expenses: ${e.message}"
                        android.util.Log.e("TeamMemberExpenses", "❌ Error loading expenses", e)
                    }
                )
                
                isLoadingExpenses = false
            } catch (e: Exception) {
                expenseError = "Error loading expense data: ${e.message}"
                isLoadingExpenses = false
            }
        }
    }

    // Function to load user notifications dynamically (matches screenshot format)
    fun loadUserNotifications() {
        scope.launch {
            try {
                val notificationRepository = NotificationRepository(context)
                val expenseRepository = ExpenseRepository()

                // Get current user ID from Firebase Auth or use test user
                val authRepository = AuthRepository()
                val currentUserId = authRepository.getCurrentUserPhoneNumber() ?: "+919876543210" // Fallback to test user

                android.util.Log.d("NotificationLoader", "🔍 Loading project-specific notifications for user: $currentUserId, project: $projectId")

                // Load project-specific stored notifications only
                val storedNotificationsResult = notificationRepository.getProjectSpecificNotifications(
                    userId = currentUserId,
                    projectId = projectId,
                    userRole = com.deeksha.avrentertainment.models.UserRole.USER
                )

                // Load recent expenses to generate dynamic notifications
                val recentExpensesResult = expenseRepository.getAllExpenses()

                val combinedNotifications = mutableListOf<NotificationData>()

                // Add stored notifications
                storedNotificationsResult.fold(
                    onSuccess = { notifications ->
                        combinedNotifications.addAll(notifications)
                        android.util.Log.d("NotificationLoader", "✅ Loaded ${notifications.size} stored notifications")
                    },
                    onFailure = {
                        android.util.Log.e("NotificationLoader", "Failed to load stored notifications: ${it.message}")
                    }
                )

                // Generate dynamic notifications from recent expenses (last 24 hours)
                recentExpensesResult.fold(
                    onSuccess = { expenses ->
                        val currentTime = System.currentTimeMillis()
                        val oneDayAgo = currentTime - (24 * 60 * 60 * 1000)

                        val recentExpenses = expenses.filter { expense ->
                            val expenseTime = expense.submittedAt.seconds * 1000
                            expenseTime > oneDayAgo && expense.projectId == projectId
                        }

                        android.util.Log.d("NotificationLoader", "📊 Found ${recentExpenses.size} recent expenses for project $projectId")

                        // Create dynamic notifications for recent activity
                        recentExpenses.forEach { expense ->
                            when (expense.status) {
                                ExpenseStatus.APPROVED -> {
                                    // Create approval notification matching screenshot format
                                    val approvalNotification = NotificationData(
                                        id = "dynamic_approval_${expense.id}",
                                        title = "Expense approved:",
                                        message = "${expense.department}, ${formatIndianNumber(expense.amount)}",
                                        type = NotificationType.EXPENSE_APPROVED,
                                        recipientId = currentUserId,
                                        senderId = expense.approvedById ?: "system",
                                        expenseId = expense.id,
                                        projectId = expense.projectId,
                                        departmentId = expense.department,
                                        amount = expense.amount,
                                        isRead = false,
                                        createdAt = expense.reviewedAt ?: expense.submittedAt
                                    )

                                    // Only add if not already in stored notifications
                                    if (!combinedNotifications.any { it.expenseId == expense.id && it.type == NotificationType.EXPENSE_APPROVED }) {
                                        combinedNotifications.add(approvalNotification)
                                        android.util.Log.d("NotificationLoader", "➕ Added dynamic approval: ${expense.department}, ₹${expense.amount}")
                                    }
                                }

                                ExpenseStatus.REJECTED -> {
                                    // Create rejection notification
                                    val rejectionNotification = NotificationData(
                                        id = "dynamic_rejection_${expense.id}",
                                        title = "Expense rejected:",
                                        message = "${expense.department}, ${formatIndianNumber(expense.amount)}",
                                        type = NotificationType.EXPENSE_REJECTED,
                                        recipientId = currentUserId,
                                        senderId = expense.approvedById ?: "system",
                                        expenseId = expense.id,
                                        projectId = expense.projectId,
                                        departmentId = expense.department,
                                        amount = expense.amount,
                                        isRead = false,
                                        createdAt = expense.reviewedAt ?: expense.submittedAt
                                    )

                                    if (!combinedNotifications.any { it.expenseId == expense.id && it.type == NotificationType.EXPENSE_REJECTED }) {
                                        combinedNotifications.add(rejectionNotification)
                                        android.util.Log.d("NotificationLoader", "➕ Added dynamic rejection: ${expense.department}, ₹${expense.amount}")
                                    }
                                }

                                ExpenseStatus.PENDING -> {
                                    // Skip pending/submitted notifications for users - they only see approval/rejection results
                                    android.util.Log.d("NotificationLoader", "⏭️ Skipping pending expense notification for user (only show approval/rejection)")
                                }
                            }
                        }
                    },
                    onFailure = {
                        android.util.Log.e("NotificationLoader", "Failed to load recent expenses: ${it.message}")
                    }
                )

                // Sort by creation time (most recent first) and limit to 10
                val sortedNotifications = combinedNotifications
                    .distinctBy { "${it.type}_${it.expenseId}_${it.departmentId}" } // Remove duplicates
                    .sortedByDescending { it.createdAt.seconds }
                    .take(10)

                userNotifications = sortedNotifications
                isLoadingNotifications = false

                android.util.Log.d("NotificationLoader", "✅ Final project-specific notification count: ${userNotifications.size} for project $projectId")
                userNotifications.forEach { notification ->
                    android.util.Log.d("NotificationLoader", "📬 ${notification.title} ${notification.message}")
                }

            } catch (e: Exception) {
                android.util.Log.e("NotificationLoader", "Error loading dynamic notifications: ${e.message}")
                isLoadingNotifications = false
            }
        }
    }

    // Load notifications initially and refresh periodically
    LaunchedEffect(refreshTrigger, showNotifications) {
        loadUserNotifications()

        // Refresh more frequently when notifications are visible
        val refreshInterval = if (showNotifications) 5_000L else 15_000L // 5 seconds when open, 15 seconds when closed

        while (true) {
            kotlinx.coroutines.delay(refreshInterval)
            loadUserNotifications()
        }
    }

    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F7F4)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar with back button and notification indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Text("Expenses", fontWeight = FontWeight.Bold, fontSize = 28.sp, modifier = Modifier.padding(start = 16.dp))
            }

            // Notification indicator for users
            Box {
                IconButton(onClick = {
                    showNotifications = !showNotifications
                    if (showNotifications) {
                        // Force immediate refresh when opening notifications
                        android.util.Log.d("NotificationUI", "🔔 Opening notification dropdown - forcing refresh")
                        refreshTrigger++
                    }
                }) {
                    val unreadCount = userNotifications.count { !it.isRead }
                    if (unreadCount > 0) {
                        Badge(containerColor = Color.Red) {
                            Text("$unreadCount", color = Color.White, fontSize = 12.sp)
                        }
                    }
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.Black)
                }

                // User Notifications Dropdown
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

                        // User notification items (approval/rejection/budget notifications)
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
                        } else if (userNotifications.isEmpty()) {
                            Text(
                                "No notifications",
                                fontSize = 14.sp,
                                color = Color(0xFF999999),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            // Show only the first 4 notifications
                            userNotifications.take(4).forEachIndexed { index, notification ->
                                val timeAgo = getTimeAgo(notification.createdAt.seconds * 1000)

                                NotificationItem(
                                    title = notification.title,
                                    description = notification.message,
                                    time = timeAgo,
                                    isRead = notification.isRead,
                                    onClick = {
                                        if ((notification.type == NotificationType.EXPENSE_APPROVED ||
                                             notification.type == NotificationType.EXPENSE_REJECTED ||
                                             notification.type == NotificationType.EXPENSE_SUBMITTED)
                                            && !notification.projectId.isNullOrBlank()) {
                                            navController.navigate("project_details/${notification.projectId}")
                                        }
                                        // else: Optionally show a message to the user
                                    }
                                )
                                
                                // Add spacing between notifications (except after the last one)
                                if (index < userNotifications.take(4).size - 1) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }

                            // Show count if there are more notifications
                            if (userNotifications.size > 4) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "+${userNotifications.size - 4} more notifications",
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
                                .clickable {
                                    showNotifications = false
                                    showAllNotifications = true
                                }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Project Info Header
        selectedProject?.let { project ->
            Text(
                project.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Color(0xFF2E5CFF),
                modifier = Modifier.padding(start = 32.dp, end = 32.dp, bottom = 8.dp)
            )
                                Text(
                        "Budget: ${formatIndianNumber(project.budget)}",
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(start = 32.dp, end = 32.dp, bottom = 16.dp)
                    )
        }

        if (isLoadingExpenses) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (expenseError != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Error Loading Expenses",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(expenseError!!, color = Color.Red)
                }
            }
        } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                    Text("Total Expenses", fontWeight = FontWeight.Medium, fontSize = 18.sp, color = Color.Black)
                Spacer(Modifier.height(8.dp))
                Text(
                        formatIndianNumber(total),
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color(0xFF2D4ECF)
                    )
                    if (projectExpenses.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${projectExpenses.size} approved expenses",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }
            }
        }
        Spacer(Modifier.height(28.dp))
            Text(
                "Categories", 
                fontWeight = FontWeight.Medium, 
                fontSize = 20.sp, 
                color = Color.Black, 
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 32.dp, bottom = 8.dp)
            )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    if (categories.isEmpty()) {
                        Text(
                            "No expenses found for this project",
                            fontSize = 16.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(20.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                categories.forEachIndexed { idx, (cat, amt) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(cat, fontSize = 17.sp, color = Color.Black)
                                Text(formatIndianNumber(amt), fontSize = 17.sp, color = Color.Black)
                    }
                    if (idx != categories.lastIndex) {
                        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        // Track Recent Submissions Button
        Button(
            onClick = { navController.navigate("track_submissions/$projectId") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Icon(
                Icons.Default.Assessment,
                contentDescription = "Track Submissions",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Track Recent Submissions", fontSize = 16.sp, color = Color.White)
        }
        
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { navController.navigate("new_expense/$projectId") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 32.dp)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D4ECF))
        ) {
            Text("Add Expense", fontSize = 18.sp, color = Color.White)
        }
    }

    // View All Notifications Dialog
    if (showAllNotifications) {
        AlertDialog(
            onDismissRequest = { showAllNotifications = false },
            title = {
                Text(
                    "All Notifications",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    if (userNotifications.isEmpty()) {
                        item {
                            Text(
                                "No notifications",
                                fontSize = 14.sp,
                                color = Color(0xFF999999),
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    } else {
                        items(userNotifications) { notification ->
                            val timeAgo = getTimeAgo(notification.createdAt.seconds * 1000)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (notification.isRead) Color.White else Color(0xFFF0F8FF)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        notification.title,
                                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF333333)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        notification.message,
                                        fontSize = 12.sp,
                                        color = Color(0xFF666666),
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        timeAgo,
                                        fontSize = 10.sp,
                                        color = Color(0xFF999999)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showAllNotifications = false }
                ) {
                    Text("Close", color = Color(0xFF2E5CFF))
                }
            }
        )
    }
}

@Composable
fun ApproverHomeScreen(navController: NavHostController, projectId: String, onBack: () -> Unit = {}) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }

    // State for project data
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var projectExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var departmentBudgets by remember { mutableStateOf<Map<String, DepartmentBudgetData>>(emptyMap()) }
    var isLoadingProject by remember { mutableStateOf(true) }
    var projectError by remember { mutableStateOf<String?>(null) }

    // State for recent expenses/notifications
    var recentExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var isLoadingNotifications by remember { mutableStateOf(true) }
    var approverNotifications by remember { mutableStateOf<List<com.deeksha.avrentertainment.models.NotificationData>>(emptyList()) }
    var refreshTrigger by remember { mutableStateOf(0) }

    // Load project data and calculate department budgets
    LaunchedEffect(projectId) {
        scope.launch {
            try {
                isLoadingProject = true
                projectError = null
                
                // Load project details
                val projectRepository = ProjectRepository()
                projectRepository.getProjectById(projectId).fold(
                    onSuccess = { project ->
                        selectedProject = project
                        android.util.Log.d("ApproverDashboard", "✅ Loaded project: ${project.name}")
                    },
                    onFailure = { e ->
                        projectError = "Failed to load project: ${e.message}"
                        android.util.Log.e("ApproverDashboard", "❌ Error loading project", e)
                    }
                )

                // Load project expenses
                val expenseRepository = ExpenseRepository()
                expenseRepository.getAllExpenses().fold(
                    onSuccess = { allExpenses ->
                        // Filter expenses for this project only
                        projectExpenses = allExpenses.filter { expense -> 
                            expense.projectId == projectId && expense.status == ExpenseStatus.APPROVED 
                        }
                        android.util.Log.d("ApproverDashboard", "✅ Loaded ${projectExpenses.size} approved expenses for project")
                        
                        // Calculate department budgets dynamically
                        departmentBudgets = calculateDepartmentBudgets(projectExpenses, selectedProject?.budget ?: 0.0)
                    },
                    onFailure = { e ->
                        projectError = "Failed to load expenses: ${e.message}"
                        android.util.Log.e("ApproverDashboard", "❌ Error loading expenses", e)
                    }
                )
                
                isLoadingProject = false
            } catch (e: Exception) {
                projectError = "Error loading project data: ${e.message}"
                isLoadingProject = false
            }
        }
    }

    // Function to load approver notifications dynamically (matching screenshot format)
    fun loadApproverNotifications() {
        scope.launch {
            try {
                val notificationRepository = NotificationRepository(context)
                val expenseRepository = ExpenseRepository()
                val authRepository = AuthRepository()
                val currentApproverId = authRepository.getCurrentUserPhoneNumber() ?: "+918765432109"

                android.util.Log.d("ApproverNotifications", "🔍 Loading dynamic notifications for approver: $currentApproverId")

                // Load stored notifications
                val storedNotificationsResult = notificationRepository.getNotificationsForUser(
                    userId = currentApproverId,
                    userRole = com.deeksha.avrentertainment.models.UserRole.APPROVER
                )

                // Load recent expenses to generate dynamic notifications
                val recentExpensesResult = expenseRepository.getAllExpenses()

                val combinedNotifications = mutableListOf<NotificationData>()

                // Add stored notifications
                storedNotificationsResult.fold(
                    onSuccess = { notifications ->
                        combinedNotifications.addAll(notifications)
                        android.util.Log.d("ApproverNotifications", "✅ Loaded ${notifications.size} stored notifications")
                    },
                    onFailure = {
                        android.util.Log.e("ApproverNotifications", "Failed to load stored notifications: ${it.message}")
                    }
                )

                // Generate dynamic notifications from recent expenses
                recentExpensesResult.fold(
                    onSuccess = { expenses ->
                        val currentTime = System.currentTimeMillis()
                        val oneDayAgo = currentTime - (24 * 60 * 60 * 1000)

                        // Get recent submissions (last 24 hours) for this project only
                        val recentSubmissions = expenses.filter { expense ->
                            val expenseTime = expense.submittedAt.seconds * 1000
                            expenseTime > oneDayAgo && expense.projectId == projectId
                        }

                        android.util.Log.d("ApproverNotifications", "📊 Found ${recentSubmissions.size} recent expense submissions")

                        // Create notifications for new expense submissions (matching screenshot format)
                        recentSubmissions.forEach { expense ->
                            if (expense.status == ExpenseStatus.PENDING) {
                                val submissionNotification = NotificationData(
                                    id = "dynamic_new_submission_${expense.id}",
                                    title = "New expense submitted:",
                                    message = "${expense.department}, ${formatIndianNumber(expense.amount)}",
                                    type = NotificationType.EXPENSE_SUBMITTED,
                                    recipientId = currentApproverId,
                                    senderId = expense.submittedById,
                                    expenseId = expense.id,
                                    projectId = expense.projectId,
                                    departmentId = expense.department,
                                    amount = expense.amount,
                                    isRead = false,
                                    createdAt = expense.submittedAt
                                )

                                // Only add if not already in stored notifications
                                if (!combinedNotifications.any { it.expenseId == expense.id && it.type == NotificationType.EXPENSE_SUBMITTED }) {
                                    combinedNotifications.add(submissionNotification)
                                    android.util.Log.d("ApproverNotifications", "➕ Added dynamic submission: ${expense.department}, ₹${expense.amount}")
                                }
                            }
                        }

                        // Remove pending expenses summary - only show individual expense submissions
                        android.util.Log.d("ApproverNotifications", "✅ Skipping pending summary - showing only individual submissions")
                    },
                    onFailure = {
                        android.util.Log.e("ApproverNotifications", "Failed to load recent expenses: ${it.message}")
                    }
                )

                // Sort by creation time (most recent first) and limit to 10
                val sortedNotifications = combinedNotifications
                    .distinctBy { "${it.type}_${it.expenseId}_${it.title}" } // Remove duplicates
                    .sortedByDescending { it.createdAt.seconds }
                    .take(10)

                approverNotifications = sortedNotifications
                isLoadingNotifications = false

                android.util.Log.d("ApproverNotifications", "✅ Final approver notification count: ${approverNotifications.size}")
                approverNotifications.forEach { notification ->
                    android.util.Log.d("ApproverNotifications", "📬 ${notification.title} ${notification.message}")
                }

            } catch (e: Exception) {
                android.util.Log.e("ApproverNotifications", "❌ Error loading dynamic notifications: ${e.message}")
                isLoadingNotifications = false
            }
        }
    }

    // Function to load pending expenses for approver notifications
    fun loadPendingExpensesForApprover() {
        scope.launch {
            try {
                val expenseRepository = ExpenseRepository()
                expenseRepository.getPendingExpenses().fold(
                    onSuccess = { expenses ->
                        // For approvers, only show PENDING expenses for this project
                        recentExpenses = expenses.filter { it.projectId == projectId }
                            .sortedByDescending { it.submittedAt.seconds }
                            .take(10) // Show up to 10 pending expenses
                        isLoadingNotifications = false
                        android.util.Log.d("ApproverNotifications", "Loaded ${recentExpenses.size} pending expenses for approver")
                    },
                    onFailure = {
                        android.util.Log.e("ApproverNotifications", "Failed to load pending expenses: ${it.message}")
                        isLoadingNotifications = false
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("ApproverNotifications", "Error loading pending expenses: ${e.message}")
                isLoadingNotifications = false
            }
        }
    }

    // Load notifications and pending expenses initially and refresh periodically
    LaunchedEffect(refreshTrigger) {
        loadApproverNotifications()
        loadPendingExpensesForApprover()

        // Refresh every 15 seconds to catch new notifications and expenses
        while (true) {
            kotlinx.coroutines.delay(15_000) // 15 seconds
            loadApproverNotifications()
            loadPendingExpensesForApprover()
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
                                    "Dashboard" -> {
                                        // Already on dashboard, just close drawer
                                    }
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
        // Main content
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "AVR ENTERTAINMENT",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                Row {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                    }
                },
                actions = {
                    var showNotifications by remember { mutableStateOf(false) }

                    Box {
                        IconButton(onClick = {
                            showNotifications = !showNotifications
                            if (showNotifications) {
                                // Force immediate refresh when opening notifications
                                android.util.Log.d("ApproverNotificationUI", "🔔 Opening approver notification dropdown - forcing refresh")
                                refreshTrigger++
                            }
                        }) {
                            val unreadCount = approverNotifications.count { !it.isRead }
                            if (unreadCount > 0) {
                                Badge(containerColor = Color.Red) {
                                    Text("$unreadCount", color = Color.White, fontSize = 12.sp)
                                }
                            }
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }

                        // Notifications Dropdown
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

                                // Real approver notifications (expense submissions, etc.)
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
                                } else if (approverNotifications.isEmpty()) {
                                    Text(
                                        "No notifications",
                                        fontSize = 14.sp,
                                        color = Color(0xFF999999),
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    // Show real notifications for approvers (limit to 4 notifications)
                                    approverNotifications.take(4).forEachIndexed { index, notification ->
                                        val timeAgo = getTimeAgo(notification.createdAt.seconds * 1000)

                                        NotificationItem(
                                            title = notification.title,
                                            description = notification.message,
                                            time = timeAgo,
                                            isRead = notification.isRead,
                                            onClick = {
                                                if ((notification.type == NotificationType.EXPENSE_APPROVED ||
                                                     notification.type == NotificationType.EXPENSE_REJECTED ||
                                                     notification.type == NotificationType.EXPENSE_SUBMITTED)
                                                    && !notification.projectId.isNullOrBlank()) {
                                                    navController.navigate("project_details/${notification.projectId}")
                                                }
                                                // else: Optionally show a message to the user
                                            }
                                        )
                                        
                                        // Add spacing between notifications (except after the last one)
                                        if (index < approverNotifications.take(4).size - 1) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }

                                    // Show count if there are more notifications
                                    if (approverNotifications.size > 4) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "+${approverNotifications.size - 4} more notifications",
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
                                        .clickable { /* TODO: Navigate to all notifications */ }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF1565C0))
            )

            // Dashboard content
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

                // Budget Summary Section
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
                                    // Total Budget
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
                                    
                                    // Spent
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
                                
                                // View Report Link
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
                    // Department Budget Cards - Dynamic based on actual project expenses
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
                            // Grid layout with smaller cards covering whole screen
                            val departmentList = departmentBudgets.entries.toList()
                            val colors = listOf(
                                Color(0xFF1565C0), Color(0xFF4CAF50), Color(0xFF9C27B0),
                                Color(0xFFFF9800), Color(0xFFF44336), Color(0xFF607D8B)
                            )
                            
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Create rows of 2 cards each
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
                                                    .height(80.dp), // Reduced height
                                                onClick = {
                                                    // Navigate to department detailed report
                                                    navController.navigate("department_report/$projectId/$department")
                                                }
                                            )
                                        }
                                        // Fill remaining space if odd number of items in the row
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
                    // Enhanced Pie Chart
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
                                // Enhanced Pie Chart with proper slices
                                PieChart(
                                    modifier = Modifier.size(120.dp),
                                    departmentBudgets = departmentBudgets
                                )

                                // Legend - Dynamic based on actual department spending
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (departmentBudgets.isNotEmpty()) {
                                        // Use same color mapping as pie chart
                                        val departmentColors = mapOf(
                                            "Sound" to Color(0xFF1565C0),      // Blue
                                            "Other" to Color(0xFF4CAF50),      // Green  
                                            "Art" to Color(0xFF9C27B0),        // Purple
                                            "Camera" to Color(0xFFFF9800),     // Orange
                                            "Costumes" to Color(0xFFF44336),   // Red
                                            "Lighting" to Color(0xFF607D8B),   // Blue Grey
                                            "Set Design" to Color(0xFF795548), // Brown
                                            "Set" to Color(0xFF795548)         // Brown (alternative name)
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

// Data class for department budget tracking
data class DepartmentBudgetData(
    val budget: Double,
    val spent: Double,
    val remaining: Double
)

// Helper function to calculate department budgets from expenses
fun calculateDepartmentBudgets(expenses: List<Expense>, totalProjectBudget: Double): Map<String, DepartmentBudgetData> {
    if (expenses.isEmpty()) return emptyMap()

    // Group expenses by department and calculate totals
    val departmentSpending = expenses.groupBy { it.department }
        .mapValues { (_, expenseList) ->
            expenseList.sumOf { it.amount }
        }

    // Calculate proportional budgets based on spending patterns
    val totalSpent = departmentSpending.values.sum()
    val budgetPercentage = if (totalSpent > 0) totalProjectBudget / totalSpent else 1.0

    return departmentSpending.mapValues { (_, spent) ->
        // Allocate budget proportionally to spending, with minimum baseline
        val allocatedBudget = maxOf(spent * budgetPercentage, spent * 1.2) // At least 20% buffer
        val remaining = allocatedBudget - spent
        
        DepartmentBudgetData(
            budget = allocatedBudget,
            spent = spent,
            remaining = remaining
        )
    }
}

// Helper function to calculate time ago
fun getTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000} mins ago"
        diff < 86_400_000 -> "${diff / 3_600_000} hr ago"
        diff < 2_592_000_000 -> "${diff / 86_400_000} days ago"
        else -> "Long ago"
    }
}

// Helper function to check budget limits
suspend fun checkBudgetLimit(
    projectId: String,
    department: String,
    expenseAmount: Double,
    projectRepository: ProjectRepository,
    expenseRepository: ExpenseRepository
): BudgetCheckResult {
    return try {
        // Get project details
        val projectResult = projectRepository.getProjectById(projectId)
        val project = projectResult.getOrNull() ?: return BudgetCheckResult.Error("Project not found")
        
        // Get all approved expenses for this project and department
        val expensesResult = expenseRepository.getAllExpenses()
        val allExpenses = expensesResult.getOrNull() ?: return BudgetCheckResult.Error("Failed to load expenses")
        
        val departmentExpenses = allExpenses.filter { expense ->
            expense.projectId == projectId && 
            expense.department == department && 
            expense.status == ExpenseStatus.APPROVED
        }
        
        val currentDepartmentSpent = departmentExpenses.sumOf { it.amount }
        val totalProjectSpent = allExpenses.filter { expense ->
            expense.projectId == projectId && expense.status == ExpenseStatus.APPROVED
        }.sumOf { it.amount }
        
        // Calculate department budget allocation (proportional to project budget)
        val departmentBudgetLimit = project.budget * 0.3 // Assuming 30% per department max, adjust as needed
        val newDepartmentTotal = currentDepartmentSpent + expenseAmount
        val newProjectTotal = totalProjectSpent + expenseAmount
        
        // Check department budget limit
        if (newDepartmentTotal > departmentBudgetLimit) {
            return BudgetCheckResult.Exceeded(
                message = "Department budget exceeded! Limit: ₹${String.format("%,.0f", departmentBudgetLimit)}, " +
                        "Current: ₹${String.format("%,.0f", currentDepartmentSpent)}, " +
                        "After expense: ₹${String.format("%,.0f", newDepartmentTotal)}",
                type = "department"
            )
        }
        
        // Check project budget limit
        if (newProjectTotal > project.budget) {
            return BudgetCheckResult.Exceeded(
                message = "Project budget exceeded! Limit: ₹${String.format("%,.0f", project.budget)}, " +
                        "Current: ₹${String.format("%,.0f", totalProjectSpent)}, " +
                        "After expense: ₹${String.format("%,.0f", newProjectTotal)}",
                type = "project"
            )
        }
        
        BudgetCheckResult.WithinLimit
        
    } catch (e: Exception) {
        BudgetCheckResult.Error("Budget check failed: ${e.message}")
    }
}

// Data class for budget check results
sealed class BudgetCheckResult {
    object WithinLimit : BudgetCheckResult()
    data class Exceeded(val message: String, val type: String) : BudgetCheckResult()
    data class Error(val message: String) : BudgetCheckResult()
}

@Composable
fun NotificationItem(
    title: String,
    description: String,
    time: String,
    isRead: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val backgroundColor = if (isRead) Color.Transparent else Color(0xFFF0F8FF)
    val titleColor = if (isRead) Color(0xFF2E2E2E) else Color(0xFF1565C0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(vertical = 6.dp, horizontal = if (isRead) 0.dp else 8.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                fontWeight = if (isRead) FontWeight.Medium else FontWeight.Bold,
                fontSize = 14.sp,
                color = titleColor,
                modifier = Modifier.weight(1f)
            )
            if (!isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Red, CircleShape)
                )
            }
        }
        if (description.isNotEmpty()) {
            Text(
                description,
                fontSize = 14.sp,
                color = Color(0xFF2E2E2E),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Text(
            time,
            fontSize = 12.sp,
            color = Color(0xFF999999),
            modifier = Modifier.padding(top = 2.dp)
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
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .height(120.dp) // Fixed height to ensure all content is visible
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                department,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Total budget on first line
            Text(
                    "Total: ${formatIndianNumber(budget)}",
                color = Color.White.copy(alpha = 0.9f),
                    fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
                )
                // Spent and Remaining side by side on second line
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Spent: ${formatIndianCurrency(spent)}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "Remaining: ${formatIndianCurrency(remaining)}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
            }
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = CircleShape)
        )
        Text(
            label,
            fontSize = 12.sp,
            color = Color(0xFF666666)
        )
    }
}

@Composable
fun LogExpenseScreen(onBack: () -> Unit = {}) {
    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Log Expense Screen", modifier = Modifier.padding(start = 8.dp))
        }
        // TODO: Add form fields for expense input
    }
}

@Composable
fun TrackSubmissionsScreen(projectId: String, onBack: () -> Unit = {}) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // State for tracking submissions
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var submissions by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Filter state - null means show all, specific status means filter by that status
    var selectedFilter by remember { mutableStateOf<ExpenseStatus?>(null) }
    
    // Filtered submissions based on selected status
    val filteredSubmissions = if (selectedFilter == null) {
        submissions
    } else {
        submissions.filter { it.status == selectedFilter }
    }
    
    // Load project and submissions data
    LaunchedEffect(projectId) {
        scope.launch {
            try {
                isLoading = true
                error = null
                
                // Load project details
                val projectRepository = ProjectRepository()
                projectRepository.getProjectById(projectId).fold(
                    onSuccess = { project ->
                        selectedProject = project
                        android.util.Log.d("TrackSubmissions", "✅ Loaded project: ${project.name}")
                    },
                    onFailure = { e ->
                        error = "Failed to load project: ${e.message}"
                        android.util.Log.e("TrackSubmissions", "❌ Error loading project", e)
                    }
                )

                // Load submissions for this project and current user
                val expenseRepository = ExpenseRepository()
                expenseRepository.getAllExpenses().fold(
                    onSuccess = { allExpenses ->
                        // Get current user's phone number
                        val authRepository = AuthRepository()
                        val currentUserPhone = authRepository.getCurrentUserPhoneNumber()
                        
                        android.util.Log.d("TrackSubmissions", "🔍 Current user phone: $currentUserPhone")
                        android.util.Log.d("TrackSubmissions", "🔍 Project ID: $projectId")
                        android.util.Log.d("TrackSubmissions", "🔍 Total expenses in database: ${allExpenses.size}")
                        
                        // Get current user's role to determine what submissions to show
                        authRepository.getUserRole(currentUserPhone ?: "").fold(
                            onSuccess = { userRole ->
                                val userProjectSubmissions = when (userRole) {
                                    com.deeksha.avrentertainment.models.UserRole.PRODUCTION_HEAD -> {
                                        // Production heads can see all submissions for the project
                                        allExpenses.filter { expense -> 
                                            expense.projectId == projectId
                                        }
                                    }
                                    com.deeksha.avrentertainment.models.UserRole.APPROVER -> {
                                        // Approvers can see all submissions for the project
                                        allExpenses.filter { expense -> 
                                            expense.projectId == projectId
                                        }
                                    }
                                    else -> {
                                        // Regular users see only their own submissions for the project
                                        allExpenses.filter { expense -> 
                                            expense.projectId == projectId && 
                                            (expense.submittedById == currentUserPhone || expense.submittedBy == currentUserPhone)
                                        }
                                    }
                                }
                                
                                submissions = userProjectSubmissions.sortedByDescending { it.submittedAt.seconds }
                                
                                android.util.Log.d("TrackSubmissions", "✅ Loaded ${submissions.size} submissions for $userRole")
                                submissions.forEach { submission ->
                                    android.util.Log.d("TrackSubmissions", "📊 ${submission.department}: ${submission.status} - ₹${submission.amount}")
                                }
                            },
                            onFailure = { e ->
                                android.util.Log.e("TrackSubmissions", "❌ Error getting user role: ${e.message}")
                                // Fallback to showing only current user's submissions
                                val userProjectSubmissions = allExpenses.filter { expense -> 
                                    expense.projectId == projectId && 
                                    (expense.submittedById == currentUserPhone || expense.submittedBy == currentUserPhone)
                                }
                                submissions = userProjectSubmissions.sortedByDescending { it.submittedAt.seconds }
                                android.util.Log.d("TrackSubmissions", "✅ Fallback: Loaded ${submissions.size} user submissions")
                            }
                        )
                    },
                    onFailure = { e ->
                        error = "Failed to load submissions: ${e.message}"
                        android.util.Log.e("TrackSubmissions", "❌ Error loading submissions", e)
                    }
                )
                
                isLoading = false
            } catch (e: Exception) {
                error = "Error loading submission data: ${e.message}"
                isLoading = false
            }
        }
    }
    


    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F7F4))
            .padding(16.dp)
    ) {
        // Header with refresh button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Text(
                    "Track Recent Submissions", 
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            // Refresh button
            IconButton(onClick = { 
                // Refresh data by reloading
                scope.launch {
                    isLoading = true
                    kotlinx.coroutines.delay(100) // Small delay to show loading
                    
                    val expenseRepository = ExpenseRepository()
                    val authRepository = AuthRepository()
                    val currentUserPhone = authRepository.getCurrentUserPhoneNumber()
                    
                    expenseRepository.getAllExpenses().fold(
                        onSuccess = { allExpenses ->
                            authRepository.getUserRole(currentUserPhone ?: "").fold(
                                onSuccess = { userRole ->
                                    val refreshedSubmissions = when (userRole) {
                                        com.deeksha.avrentertainment.models.UserRole.PRODUCTION_HEAD,
                                        com.deeksha.avrentertainment.models.UserRole.APPROVER -> {
                                            allExpenses.filter { expense -> expense.projectId == projectId }
                                        }
                                        else -> {
                                            allExpenses.filter { expense -> 
                                                expense.projectId == projectId && 
                                                (expense.submittedById == currentUserPhone || expense.submittedBy == currentUserPhone)
                                            }
                                        }
                                    }.sortedByDescending { it.submittedAt.seconds }
                                    
                                    submissions = refreshedSubmissions
                                    android.util.Log.d("TrackSubmissions", "🔄 Manual refresh: ${refreshedSubmissions.size} submissions found")
                                },
                                onFailure = { e ->
                                    error = "Failed to refresh: ${e.message}"
                                }
                            )
                        },
                        onFailure = { e ->
                            error = "Failed to refresh: ${e.message}"
                        }
                    )
                    isLoading = false
                }
            }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF2E5CFF))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Project Name
        selectedProject?.let { project ->
            Text(
                project.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color(0xFF2E5CFF),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Error Loading Submissions",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(error!!, color = Color.Red)
                }
            }
        } else {
            // Statistics Cards
            val approvedCount = submissions.count { it.status == ExpenseStatus.APPROVED }
            val rejectedCount = submissions.count { it.status == ExpenseStatus.REJECTED }
            val pendingCount = submissions.count { it.status == ExpenseStatus.PENDING }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                item {
                    ClickableStatCard(
                        title = "Approved",
                        count = approvedCount,
                        color = Color(0xFF4CAF50),
                        icon = Icons.Default.Check,
                        isSelected = selectedFilter == ExpenseStatus.APPROVED,
                        onClick = {
                            selectedFilter = if (selectedFilter == ExpenseStatus.APPROVED) null else ExpenseStatus.APPROVED
                        }
                    )
                }
                item {
                    ClickableStatCard(
                        title = "Pending",
                        count = pendingCount,
                        color = Color(0xFFFF9800),
                        icon = Icons.Default.Refresh,
                        isSelected = selectedFilter == ExpenseStatus.PENDING,
                        onClick = {
                            selectedFilter = if (selectedFilter == ExpenseStatus.PENDING) null else ExpenseStatus.PENDING
                        }
                    )
                }
                item {
                    ClickableStatCard(
                        title = "Rejected",
                        count = rejectedCount,
                        color = Color(0xFFF44336),
                        icon = Icons.Default.Close,
                        isSelected = selectedFilter == ExpenseStatus.REJECTED,
                        onClick = {
                            selectedFilter = if (selectedFilter == ExpenseStatus.REJECTED) null else ExpenseStatus.REJECTED
                        }
                    )
                }
            }
            
            // Recent Submissions List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Submissions",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                if (selectedFilter != null) {
                    TextButton(
                        onClick = { selectedFilter = null }
                    ) {
                        Text(
                            "Show All (${submissions.size})",
                            color = Color(0xFF2E5CFF),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (filteredSubmissions.isEmpty() && submissions.isNotEmpty()) {
                // Show "no items for filter" message
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "No filtered results",
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFF999999)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No ${selectedFilter?.name?.lowercase()} submissions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Try selecting a different status or view all submissions",
                            fontSize = 14.sp,
                            color = Color(0xFF999999),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (submissions.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Assessment,
                            contentDescription = "No submissions",
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFF999999)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No submissions found",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Submit your first expense to see it here",
                            fontSize = 14.sp,
                            color = Color(0xFF999999),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSubmissions) { submission ->
                        SubmissionCard(submission = submission)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    count: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                count.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                title,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ClickableStatCard(
    title: String,
    count: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color else color.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) BorderStroke(2.dp, color) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(if (isSelected) 24.dp else 20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                count.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = if (isSelected) 18.sp else 16.sp,
                color = Color.White
            )
            Text(
                title,
                fontSize = if (isSelected) 11.sp else 10.sp,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
fun SubmissionCard(submission: Expense) {
    val statusColor = when (submission.status) {
        ExpenseStatus.APPROVED -> Color(0xFF4CAF50)
        ExpenseStatus.REJECTED -> Color(0xFFF44336)
        ExpenseStatus.PENDING -> Color(0xFFFF9800)
    }
    
    val statusText = when (submission.status) {
        ExpenseStatus.APPROVED -> "Approved"
        ExpenseStatus.REJECTED -> "Rejected"
        ExpenseStatus.PENDING -> "Pending"
    }
    
    val timeAgo = getTimeAgo(submission.submittedAt.seconds * 1000)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        submission.department,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        submission.description,
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        formatIndianNumber(submission.amount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF2E5CFF)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor,
                        modifier = Modifier
                            .background(
                                statusColor.copy(alpha = 0.1f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        timeAgo,
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatScreen(onBack: () -> Unit = {}) {
    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Chat Screen", modifier = Modifier.padding(start = 8.dp))
        }
        // TODO: Implement messaging UI
    }
}

@Composable
fun ReviewApprovalsScreen(onBack: () -> Unit = {}) {
    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Review Approvals", modifier = Modifier.padding(start = 8.dp))
        }
        // TODO: Approve/Reject expense submissions
    }
}

@Composable
fun ReportsScreen(navController: NavHostController, onBack: () -> Unit = {}) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }

    // State for dynamic data
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var budgets by remember { mutableStateOf<List<Budget>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Repository instances
    val expenseRepository = remember { ExpenseRepository() }
    val budgetRepository = remember { BudgetRepository() }

    // Function to load data
    fun loadData() {
        scope.launch {
            isLoading = true
            error = null

            try {
                // Fetch expenses
                expenseRepository.getAllExpenses().fold(
                    onSuccess = { expenseList ->
                        expenses = expenseList.filter { it.status == ExpenseStatus.APPROVED }
                    },
                    onFailure = { e ->
                        error = "Failed to load expenses: ${e.message}"
                    }
                )

                // Fetch budgets (simplified approach)
                try {
                    budgetRepository.getAllBudgets().collect { result ->
                        result.fold(
                            onSuccess = { budgetList ->
                                budgets = budgetList
                            },
                            onFailure = { e ->
                                // Don't show budget error, just use empty list
                                budgets = emptyList()
                                android.util.Log.w("Reports", "Budget loading failed: ${e.message}")
                            }
                        )
                    }
                } catch (e: Exception) {
                    // Fallback: use empty budget list
                    budgets = emptyList()
                    android.util.Log.w("Reports", "Budget loading exception: ${e.message}")
                }
            } catch (e: Exception) {
                error = "Error loading data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Load data initially and refresh every 30 seconds
    LaunchedEffect(Unit) {
        loadData()

        // Auto-refresh every 30 seconds to catch new expenses
        while (true) {
            kotlinx.coroutines.delay(30_000) // 30 seconds
            loadData()
        }
    }

    // Process data for charts and tables (using your original sample data structure)

    val subcategorySplit = if (expenses.isNotEmpty()) {
        val categoryTotals = expenses.groupBy { expense -> expense.category }
            .mapValues { entry -> entry.value.sumOf { expense -> expense.amount } }
        val maxAmount = categoryTotals.values.maxOrNull() ?: 1.0

        categoryTotals.map { (category, amount) ->
            val barWidth = (amount / maxAmount).toFloat()
            Pair(category, Pair(amount, barWidth))
        }
    } else {
        // Original sample data as fallback
        listOf(
            Pair("Equipment", Pair(934000.0, 1.0f)),
            Pair("Wages & Crew Payments", Pair(0.0, 0.0f)),
            Pair("Location", Pair(1722930.0, 0.8f)),
            Pair("Miscellaneous", Pair(783939.0, 0.4f))
        )
    }

    // Budget overview table data (dynamic)
    val budgetOverviewData = if (expenses.isNotEmpty()) {
        val departmentTotals = expenses.groupBy { expense -> expense.department }
            .mapValues { entry -> entry.value.sumOf { expense -> expense.amount } }

        departmentTotals.map { (dept, spent) ->
            val budget = when (dept) {
                "Costumes" -> 100000.0
                "Set Design", "Set" -> 300000.0
                "Camera" -> 150000.0
                "Lighting" -> 100000.0
                "Sound" -> 80000.0
                "Art" -> 1175909.0
                "Other" -> 975000.0
                else -> spent * 1.5
            }
            val remaining = budget - spent
            val isOverBudget = spent > budget

            mapOf(
                "department" to dept,
                "budget" to budget,
                "spent" to spent,
                "remaining" to remaining,
                "isOverBudget" to isOverBudget
            )
        }
    } else {
        // Original sample data
        listOf(
            mapOf("department" to "Lighting", "budget" to 100000.0, "spent" to 269000.0, "remaining" to -169000.0, "isOverBudget" to true),
            mapOf("department" to "Sound", "budget" to 80000.0, "spent" to 1049000.0, "remaining" to -969000.0, "isOverBudget" to true),
            mapOf("department" to "Art", "budget" to 1175909.0, "spent" to 783939.0, "remaining" to 391970.0, "isOverBudget" to false),
            mapOf("department" to "Other", "budget" to 975000.0, "spent" to 650000.0, "remaining" to 325000.0, "isOverBudget" to false),
            mapOf("department" to "Costumes", "budget" to 100000.0, "spent" to 15000.0, "remaining" to 85000.0, "isOverBudget" to false),
            mapOf("department" to "Camera", "budget" to 150000.0, "spent" to 673930.0, "remaining" to -523930.0, "isOverBudget" to true)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        TopAppBar(
            title = { Text("Reports", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7B68EE))
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            // Subcategory Split (Stacked Bar Chart)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            "Subcategory Split",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF2E2E2E),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (subcategorySplit.isEmpty()) {
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
                            // Convert subcategorySplit data to the format needed for DynamicBarChart
                            val spentData = subcategorySplit.associate { (category, data) -> 
                                category to data.first 
                            }
                            // Create mock budget data (assuming spent is 70% of budget for demo)
                            val budgetData = spentData.mapValues { it.value / 0.7 }
                            
                            DynamicBarChart(
                                data = spentData,
                                        modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                budgetData = budgetData
                                    )
                        }
                    }
                }
            }

            // Budget Overview Table
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            "Budget Overview Table",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF2E2E2E),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text("Department", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF5F6368), modifier = Modifier.weight(1f))
                            Text("Budget", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF5F6368), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text("Spent", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF5F6368), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text("Remaining", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF5F6368), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Table Rows
                        budgetOverviewData.forEach { row ->
                            val dept = row["department"] as String
                            val budget = row["budget"] as Double
                            val spent = row["spent"] as Double
                            val remaining = row["remaining"] as Double
                            val isOverBudget = row["isOverBudget"] as Boolean

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(dept, fontSize = 14.sp, color = Color(0xFF2E2E2E), modifier = Modifier.weight(1f))
                                                            Text(formatIndianNumber(budget), fontSize = 14.sp, color = Color(0xFF2E2E2E), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(formatIndianNumber(spent), fontSize = 14.sp, color = if (isOverBudget) Color.Red else Color(0xFF2E2E2E), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(formatIndianNumber(remaining), fontSize = 14.sp, color = if (isOverBudget) Color.Red else Color(0xFF4CAF50), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            }

                            // Progress bar
                            val utilizationPercentage = (spent / budget).coerceAtMost(1.0).toFloat()
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .padding(horizontal = 16.dp)
                                    .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(utilizationPercentage)
                                        .height(4.dp)
                                        .background(
                                            if (isOverBudget) Color.Red else Color(0xFF4CAF50),
                                            RoundedCornerShape(2.dp)
                                        )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Total Row
                        val totalBudget = budgetOverviewData.sumOf { row -> row["budget"] as Double }
                        val totalSpent = budgetOverviewData.sumOf { row -> row["spent"] as Double }
                        val totalRemaining = totalBudget - totalSpent

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            Text("TOTAL", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E2E2E), modifier = Modifier.weight(1f))
                            Text(formatIndianNumber(totalBudget), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E2E2E), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(formatIndianNumber(totalSpent), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E2E2E), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(formatIndianNumber(totalRemaining), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (totalRemaining < 0) Color.Red else Color(0xFF4CAF50), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Export Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    val reportData = ReportData(
                                        title = "Approver Dashboard Report",
                                        totalSpent = totalSpent,
                                        totalBudget = totalBudget,
                                        departmentData = budgetOverviewData.map { row ->
                                            DepartmentReportItem(
                                                department = row["department"] as String,
                                                budget = row["budget"] as Double,
                                                spent = row["spent"] as Double,
                                                percentage = if ((row["budget"] as Double) > 0) 
                                                    ((row["spent"] as Double) / (row["budget"] as Double)) * 100 
                                                else 0.0
                                            )
                                        },
                                        filters = ReportFilters(
                                            dateRange = "All Time",
                                            department = "All Departments",
                                            project = "Project Report"
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
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Export PDF", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            }

                            Button(
                                onClick = {
                                    val reportData = ReportData(
                                        title = "Approver Dashboard Report",
                                        totalSpent = totalSpent,
                                        totalBudget = totalBudget,
                                        departmentData = budgetOverviewData.map { row ->
                                            DepartmentReportItem(
                                                department = row["department"] as String,
                                                budget = row["budget"] as Double,
                                                spent = row["spent"] as Double,
                                                percentage = if ((row["budget"] as Double) > 0) 
                                                    ((row["spent"] as Double) / (row["budget"] as Double)) * 100 
                                                else 0.0
                                            )
                                        },
                                        filters = ReportFilters(
                                            dateRange = "All Time",
                                            department = "All Departments",
                                            project = "Project Report"
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
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34A853)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Export Excel", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewExpenseScreen(
    projectId: String,
    onSubmit: (ExpenseData) -> Unit,
    onBack: () -> Unit,
    projectRepository: ProjectRepository,
    expenseRepository: ExpenseRepository,
    notificationRepository: NotificationRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State variables
    var date by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var department by remember { mutableStateOf("Costumes") }
    var category by remember { mutableStateOf("Wages & Crew Payments") }
    var modeOfPayment by remember { mutableStateOf("By cash") }
    var description by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var submittedExpense by remember { mutableStateOf<Expense?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    var attachment by remember { mutableStateOf<String?>(null) }
    var attachmentUri by remember { mutableStateOf<Uri?>(null) }

    // Load the selected project by ID
    LaunchedEffect(projectId) {
        scope.launch {
            android.util.Log.d("NewExpenseScreen", "🔍 Loading project with ID: $projectId")
            projectRepository.getProjectById(projectId).fold(
                onSuccess = { project: Project ->
                    selectedProject = project
                    android.util.Log.d("NewExpenseScreen", "✅ Loaded project: ${project.name}")
                },
                onFailure = { e: Throwable ->
                    val errorMessage = when {
                        e.message?.contains("PERMISSION_DENIED") == true ->
                            "Database access denied. Please ensure you're logged in and have proper permissions."
                        e.message?.contains("UNAVAILABLE") == true ->
                            "Database temporarily unavailable. Please check your internet connection and try again."
                        else ->
                            "Failed to load project: ${e.message}"
                    }
                    error = errorMessage
                    android.util.Log.e("ProjectLoad", "Error loading project", e)
                }
            )
        }
    }

    val departments = listOf("Costumes", "Camera", "Lighting", "Sound", "Art", "Other")
    val categories = listOf("Wages & Crew Payments", "Equipment", "Location", "Miscellaneous")
    val paymentModes = listOf("By cash", "By UPI", "By check")

    // Compose launchers for camera, gallery, and PDF
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            // TODO: Save bitmap to file and get Uri if needed
            attachment = "Camera Photo"
            // Optionally, handle bitmap saving and set attachmentUri
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            attachmentUri = uri
            attachment = uri.toString()
        }
    }
    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            attachmentUri = uri
            attachment = uri.toString()
        }
    }

    // Date Picker
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            date = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(Modifier.width(8.dp))
                Text("New Expense", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(16.dp))
            Text("AVR Entertainment", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
        }

        item {
            // Selected Project Display (Read-only)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                                    Text(
                        "Selected Project",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        selectedProject?.name ?: "Loading project...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E5CFF)
                        )
                    )
                    selectedProject?.let { project ->
                        Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                "Budget: ${formatIndianNumber(project.budget)}",
                                        style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666666)
                            )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        item {
            OutlinedTextField(
                value = date,
                onValueChange = {},
                label = { Text("Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                readOnly = true
            )
            Spacer(Modifier.height(12.dp))
        }

        item {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                prefix = { Text("₹ ") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }

        item {
            // Department Dropdown
            var expandedDept by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedDept,
                onExpandedChange = { expandedDept = !expandedDept }
            ) {
                OutlinedTextField(
                    value = department,
                    onValueChange = {},
                    label = { Text("Department") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .clickable { expandedDept = true }
                )
                ExposedDropdownMenu(
                    expanded = expandedDept,
                    onDismissRequest = { expandedDept = false }
                ) {
                    departments.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                department = it
                                expandedDept = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        item {
            // Category Dropdown
            var expandedCat by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedCat,
                onExpandedChange = { expandedCat = !expandedCat }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    label = { Text("Category") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .clickable { expandedCat = true }
                )
                ExposedDropdownMenu(
                    expanded = expandedCat,
                    onDismissRequest = { expandedCat = false }
                ) {
                    categories.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                category = it
                                expandedCat = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            Spacer(Modifier.height(12.dp))
        }

        item {
            // Mode of Payment Radio Buttons
            Text("Mode of Payment", style = MaterialTheme.typography.bodyLarge)
            paymentModes.forEach { mode ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = modeOfPayment == mode,
                        onClick = { modeOfPayment = mode }
                    )
                    Text(mode)
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        item {
            // Add Attachment Button
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2D4ECF),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach",
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (attachment != null) "Attachment Selected" else "Add Attachment",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        item {
            // Submit Button
            Button(
                onClick = {
                    if (selectedProject != null && amount.isNotBlank() && date.isNotBlank()) {
                        isSubmitting = true
                        error = null
                        success = null

                        // Get current user information
                        val authRepository = AuthRepository()
                        val currentUserPhone = authRepository.getCurrentUserPhoneNumber() ?: "+919876543210"
                        val currentUserName = "Current User" // In real app, get from user profile

                        val expense = Expense(
                            projectId = selectedProject!!.id,
                            projectName = selectedProject!!.name,
                            date = date,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            department = department,
                            category = category,
                            modeOfPayment = modeOfPayment,
                            description = description,
                            attachment = attachment,
                            submittedBy = currentUserName,
                            submittedById = currentUserPhone, // Use actual logged-in user's phone number
                            status = ExpenseStatus.PENDING
                        )

                        scope.launch {
                            android.util.Log.d("ExpenseSubmission", "🚀 Submitting expense")
                            android.util.Log.d("ExpenseSubmission", "👤 Submitter: ${expense.submittedById}")
                            android.util.Log.d("ExpenseSubmission", "💰 Amount: ₹${expense.amount}")
                            android.util.Log.d("ExpenseSubmission", "📋 Project: ${expense.projectName}")

                            // Check budget limits before submission
                            android.util.Log.d("ExpenseSubmission", "🔍 Checking budget limits...")
                            val budgetCheck = checkBudgetLimit(
                                projectId = selectedProject!!.id,
                                department = department,
                                expenseAmount = expense.amount,
                                projectRepository = projectRepository,
                                expenseRepository = expenseRepository
                            )

                            when (budgetCheck) {
                                is BudgetCheckResult.Exceeded -> {
                                    // Budget exceeded - show toast and prevent submission
                                    android.util.Log.w("ExpenseSubmission", "❌ Budget exceeded: ${budgetCheck.message}")
                                    Toast.makeText(
                                        context,
                                        "❌ ${budgetCheck.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    error = "Cannot submit: ${budgetCheck.message}"
                                    isSubmitting = false
                                    return@launch
                                }
                                is BudgetCheckResult.Error -> {
                                    // Budget check failed
                                    android.util.Log.e("ExpenseSubmission", "❌ Budget check error: ${budgetCheck.message}")
                                    error = "Budget validation failed: ${budgetCheck.message}"
                                    isSubmitting = false
                                    return@launch
                                }
                                is BudgetCheckResult.WithinLimit -> {
                                    // Budget is fine, proceed with submission
                                    android.util.Log.d("ExpenseSubmission", "✅ Budget check passed")
                                }
                            }

                            expenseRepository.createExpense(expense).fold(
                                onSuccess = { expenseId ->
                                    android.util.Log.d("ExpenseSubmission", "✅ Expense created with ID: $expenseId")

                                    // Send notification to approvers
                                    val approverIds = listOf("approver1", "approver2") // TODO: Get real approver IDs
                                    notificationRepository.sendExpenseSubmittedNotification(
                                        expense.copy(id = expenseId),
                                        approverIds
                                    )

                                    success = "Expense submitted successfully! Approvers have been notified."
                                    isSubmitting = false

                                    // Show a simulated push notification for demonstration
                                    android.util.Log.d("ExpenseSubmission", "🎉 SUCCESS: Expense submitted!")
                                    android.util.Log.d("ExpenseSubmission", "📱 Push notification will be sent to approvers")
                                    android.util.Log.d("ExpenseSubmission", "💰 Amount: ₹${expense.amount}")
                                    android.util.Log.d("ExpenseSubmission", "📋 Project: ${expense.projectName}")
                                    android.util.Log.d("ExpenseSubmission", "👤 Submitted by: ${expense.submittedBy}")

                                    // Show immediate success notification to user
                                    try {
                                        val localNotificationService = LocalNotificationService(context)
                                        localNotificationService.sendDelayedNotification(
                                            title = "Expense Submitted Successfully!",
                                            message = "Your ${formatIndianNumber(expense.amount)} expense for ${expense.projectName} has been submitted for approval.",
                                            delayMs = 1000
                                        )
                                    } catch (e: Exception) {
                                        android.util.Log.e("ExpenseSubmission", "Failed to show notification: ${e.message}")
                                    }

                                    // Show success toast
                                    Toast.makeText(
                                        context,
                                        "✅ Expense submitted successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Navigate back after delay
                                    kotlinx.coroutines.delay(3000)
                                    onBack()
                                },
                                onFailure = { e ->
                                    error = "Failed to submit expense: ${e.message}"
                                    isSubmitting = false
                                }
                            )
                        }
                    } else {
                        error = "Please fill in all required fields"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D4ECF)),
                enabled = !isSubmitting && selectedProject != null
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    if (isSubmitting) "Submitting..." else "Submit for Approval",
                    color = Color.White
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // Error and Success Messages
        error?.let { errorMsg ->
            item {
                Text(
                    text = errorMsg,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        success?.let { successMsg ->
            item {
                Text(
                    text = successMsg,
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    // Compose-based Attachment Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Attachment") },
            text = {
                Column {
                    TextButton(onClick = {
                        cameraLauncher.launch(null)
                        showDialog = false
                    }) { Text("Take Photo") }
                    TextButton(onClick = {
                        galleryLauncher.launch("image/*")
                        showDialog = false
                    }) { Text("Choose Image") }
                    TextButton(onClick = {
                        pdfLauncher.launch("application/pdf")
                        showDialog = false
                    }) { Text("Choose PDF") }
                }
            },
            confirmButton = { Spacer(Modifier) }, // Required, but can be empty
            dismissButton = { Spacer(Modifier) }  // Optional, can be empty
        )
    }
}

@Composable
fun ExpenseDetailDialog(
    expense: ExpenseData,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color(0xFFF9EAE7), // Light pink as in your screenshot
            modifier = Modifier
                .fillMaxWidth(0.95f) // 95% of screen width
                .heightIn(min = 420.dp, max = 600.dp) // Set min/max height as needed
        ) {
            Box(
                Modifier
                    .padding(24.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Expense Detail",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFF2D2D2D)
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    // Details
                    Text(buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Department: ") }
                        append(expense.department)
                    })
                    Text(buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Subcategory: ") }
                        append(expense.category)
                    })
                    Text(buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Date: ") }
                        append(expense.date)
                    })
                    Text(buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Amount: ") }
                        append("₹${expense.amount}")
                    })
                    Text(buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Submitted By: ") }
                        append("Anil")
                    })
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Invoice Attachment: ", fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.AttachFile, contentDescription = "Attachment", tint = Color(0xFF2D4ECF))
                        Text(
                            "View Full",
                            color = Color(0xFF2D4ECF),
                            modifier = Modifier.clickable { /* TODO: open attachment */ }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Notes: \"Paid to makeup crew for 3-day shoot\"")
                    Spacer(Modifier.height(8.dp))
                    Text("Budget Remaining BEFORE: ₹98,000")
                    Text("Budget Remaining AFTER Approval: ₹90,100")
                    Spacer(Modifier.height(24.dp))
                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DBA8E)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("✔ Approve")
                        }
                        Spacer(Modifier.width(16.dp))
                        Button(
                            onClick = onReject,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE74C3C)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("✖ Reject")
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Add Reviewer Note",
                        color = Color(0xFF2D4ECF),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { /* TODO: Add reviewer note */ }
                    )
                }
            }
        }
    }
}

// Data class for expense
data class ExpenseData(
    val date: String,
    val amount: String,
    val department: String,
    val category: String,
    val modeOfPayment: String,
    val attachment: String?
)

suspend fun uploadBudgetsFromJson(context: Context) = withContext(Dispatchers.IO) {
    val db = FirebaseFirestore.getInstance()
    val budgetsCollection = db.collection("budgets")

    // Read JSON from assets
    val json = context.assets.open("budgets.json").bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Budget>>() {}.type
    val budgets: List<Budget> = Gson().fromJson(json, type)

    for (budget in budgets) {
        val docRef = budgetsCollection.document()
        val budgetWithId = budget.copy(id = docRef.id)
        docRef.set(budgetWithId).await()
    }
}

@Composable
fun DebugButton() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Button(onClick = {
        scope.launch {
            uploadBudgetsFromJson(context)
        }
    }) {
        Text("Upload Budgets")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestDropdown() {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("A", "B", "C")
    var selected by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Choose") },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        selected = it
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ExampleScreen() {
    val context = LocalContext.current
    var date by remember { mutableStateOf("") }
    var attachmentUri by remember { mutableStateOf<Uri?>(null) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            date = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        attachmentUri = uri
    }

    Column {
        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text("Date") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() },
            readOnly = true
        )
        Button(
            onClick = { pickFileLauncher.launch("/") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Attachment")
        }
        if (attachmentUri != null) {
            Text("Selected: $attachmentUri")
        }
    }
}

data class ApprovalItem(
    val date: String,
    val department: String,
    val subcategory: String,
    val submittedBy: String
)

class ApprovalsViewModel : ViewModel() {
    // In real app, fetch from Firestore and update this list
    var approvals = mutableStateListOf(
        ApprovalItem("10/04", "Set Des.", "Wages", "Anil"),
        ApprovalItem("10/05", "Costumes", "Equip/Rentals", "Priya"),
        ApprovalItem("10/06", "Misc", "Travel", "View"),
        ApprovalItem("10/04", "Misc", "Travel", "Ramesh")
    )
    // For dropdowns, you can fetch these from Firestore as well
    val departments = approvals.map { it.department }.distinct()
    val subcategories = approvals.map { it.subcategory }.distinct()
    val submitters = approvals.map { it.submittedBy }.distinct()
    val dates = approvals.map { it.date }.distinct()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingApprovalsScreen(
    navController: NavHostController, 
    projectId: String,
    expenseRepository: ExpenseRepository, 
    notificationRepository: NotificationRepository, 
    onBack: () -> Unit = {}
) {
    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State variables
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var filteredExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedDept by remember { mutableStateOf("") }
    val selectedItems = remember { mutableStateListOf<Boolean>() }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }

    // Load project details and pending expenses for this specific project
    LaunchedEffect(projectId) {
        scope.launch {
            try {
                isLoading = true
                error = null
                
                // Load project details
                val projectRepository = ProjectRepository()
                projectRepository.getProjectById(projectId).fold(
                    onSuccess = { project ->
                        selectedProject = project
                        android.util.Log.d("PendingApprovals", "✅ Loaded project: ${project.name}")
                    },
                    onFailure = { e ->
                        error = "Failed to load project: ${e.message}"
                        android.util.Log.e("PendingApprovals", "❌ Error loading project", e)
                    }
                )
                
                // Load pending expenses for this specific project only
                android.util.Log.d("PendingApprovals", "🔍 Loading pending expenses for project: $projectId")
                expenseRepository.getPendingExpensesByProject(projectId).fold(
                    onSuccess = { expenseList ->
                        android.util.Log.d("PendingApprovals", "✅ Loaded ${expenseList.size} pending expenses for this project")
                        expenseList.forEach { expense ->
                            android.util.Log.d("PendingApprovals", "📋 Expense: ${expense.department} - ₹${expense.amount}")
                        }
                        expenses = expenseList
                        filteredExpenses = expenseList
                        isLoading = false
                    },
                    onFailure = { e ->
                        android.util.Log.e("PendingApprovals", "❌ Error loading expenses: ${e.message}", e)
                        error = "Failed to load expenses: ${e.message}"
                        isLoading = false
                    }
                )
            } catch (e: Exception) {
                error = "Error loading project data: ${e.message}"
                isLoading = false
                android.util.Log.e("PendingApprovals", "❌ Unexpected error", e)
            }
        }
    }

    // Filter expenses when filter criteria change
    LaunchedEffect(selectedDate, selectedDept) {
        filteredExpenses = expenses.filter { expense ->
            (selectedDate.isBlank() || expense.date.contains(selectedDate)) &&
                    (selectedDept.isBlank() || expense.department == selectedDept)
        }
    }

    // Manage selectedItems size based on filteredExpenses
    LaunchedEffect(filteredExpenses.size) {
        val currentSize = selectedItems.size
        val targetSize = filteredExpenses.size
        
        when {
            targetSize > currentSize -> {
                // Add false values for new items
                selectedItems.addAll(List(targetSize - currentSize) { false })
            }
            targetSize < currentSize -> {
                // Remove excess items from the end
                repeat(currentSize - targetSize) {
                    if (selectedItems.isNotEmpty()) {
                        selectedItems.removeLast()
                    }
                }
            }
        }
        android.util.Log.d("SelectedItems", "Updated size to ${selectedItems.size} for ${filteredExpenses.size} expenses")
    }

    // Date picker state
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = "%02d/%02d".format(dayOfMonth, month + 1)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F7F4))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    "AVR ENTERTAINMENT",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF0D47A1))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Project and Pending Approvals Title
        Column(modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)) {
            selectedProject?.let { project ->
                Text(
                    project.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF2E5CFF),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Text(
                "Pending Approvals",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF222B45)
            )
        }

        // Filter Row (Date and Dept dropdowns)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date Dropdown
            Box(modifier = Modifier.weight(1f)) {
                var dateDropdownExpanded by remember { mutableStateOf(false) }
                val allDates = expenses.map { it.date }.distinct()
                ExposedDropdownMenuBox(
                    expanded = dateDropdownExpanded,
                    onExpandedChange = { dateDropdownExpanded = !dateDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedDate.ifBlank { "Date" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Date") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dateDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = dateDropdownExpanded,
                        onDismissRequest = { dateDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Dates") },
                            onClick = {
                                selectedDate = ""
                                dateDropdownExpanded = false
                            }
                        )
                        allDates.forEach { date ->
                            DropdownMenuItem(
                                text = { Text(date) },
                                onClick = {
                                    selectedDate = date
                                    dateDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Dept Dropdown
            Box(modifier = Modifier.weight(1f)) {
                var deptDropdownExpanded by remember { mutableStateOf(false) }
                val allDepts = expenses.map { it.department }.distinct()
                ExposedDropdownMenuBox(
                    expanded = deptDropdownExpanded,
                    onExpandedChange = { deptDropdownExpanded = !deptDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedDept.ifBlank { "Department" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Dept") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = deptDropdownExpanded,
                        onDismissRequest = { deptDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Departments") },
                            onClick = {
                                selectedDept = ""
                                deptDropdownExpanded = false
                            }
                        )
                        allDepts.forEach { dept ->
                            DropdownMenuItem(
                                text = { Text(dept) },
                                onClick = {
                                    selectedDept = dept
                                    deptDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (expenses.isNotEmpty()) {
            // Table Header - only show when there are expenses
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3E8F7))
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(Modifier.width(32.dp)) // For checkbox space
                Text("Date", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color(0xFF333333))
                Text("Project", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color(0xFF333333))
                Text("Dept", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color(0xFF333333))
                Text("Amount", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color(0xFF333333))
                Text("Submitted By", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color(0xFF333333))
            }

            // Table Rows
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(filteredExpenses) { idx, expense ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Navigate to approval modal when row is clicked
                                navController.navigate("approval_modal/${expense.id}")
                            }
                            .background(Color(0xFFFAFAFA))
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Custom checkbox with black border and green checkmark using mutableStateListOf
                        val isSelected = if (idx < selectedItems.size) selectedItems[idx] else false
                        
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clickable {
                                    // Simply toggle the selection - mutableStateListOf will trigger recomposition
                                    if (idx < selectedItems.size) {
                                        selectedItems[idx] = !selectedItems[idx]
                                        android.util.Log.d("Checkbox", "Item $idx clicked, selected: ${selectedItems[idx]}")
                                    }
                                }
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(2.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Show green checkmark if selected
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color(0xFF4CAF50), // Bright green checkmark
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                        Text(expense.date, modifier = Modifier.weight(1f), fontSize = 15.sp, color = Color(0xFF333333))
                        Text(
                            expense.projectName,
                            modifier = Modifier.weight(1f),
                            fontSize = 15.sp,
                            color = Color(0xFF333333),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(expense.department, modifier = Modifier.weight(1f), fontSize = 15.sp, color = Color(0xFF333333))
                        Text(formatIndianNumber(expense.amount), modifier = Modifier.weight(1f), fontSize = 15.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
                        Text(expense.submittedBy, modifier = Modifier.weight(1f), fontSize = 15.sp, color = Color(0xFF333333))
                    }
                    Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                }
            }

            // Action Buttons - only show when there are expenses
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        // Approve selected expenses
                        scope.launch {
                            val selectedExpenses = filteredExpenses.filterIndexed { index, _ ->
                                index < selectedItems.size && selectedItems[index]
                            }

                            if (selectedExpenses.isNotEmpty()) {
                                error = null
                                success = null

                                selectedExpenses.forEach { expense ->
                                    // Get current approver information
                                    val authRepository = AuthRepository()
                                    val currentApproverPhone = authRepository.getCurrentUserPhoneNumber() ?: "+918765432109"
                                    val currentApproverName = "Current Approver"

                                    android.util.Log.d("BulkApproval", "✅ Bulk approving expense: ${expense.id}")
                                    android.util.Log.d("BulkApproval", "👤 Submitter: ${expense.submittedById}")
                                    android.util.Log.d("BulkApproval", "💰 Amount: ₹${expense.amount}")

                                    // Use the enhanced approval method that includes proper notification handling
                                    expenseRepository.approveExpenseWithBudgetCheck(
                                        expenseId = expense.id,
                                        reviewerId = currentApproverPhone,
                                        reviewerName = currentApproverName,
                                        reviewerNote = "Approved via bulk action",
                                        budgetRepository = BudgetRepository(),
                                        notificationRepository = notificationRepository,
                                        projectRepository = ProjectRepository()
                                    ).fold(
                                        onSuccess = {
                                            android.util.Log.d("BulkApproval", "✅ Successfully approved expense ${expense.id}")
                                        },
                                        onFailure = { e ->
                                            android.util.Log.e("BulkApproval", "❌ Failed to approve expense ${expense.id}: ${e.message}")
                                            error = "Failed to approve expense: ${e.message}"
                                        }
                                    )
                                }

                                success = "${selectedExpenses.size} expense(s) approved successfully!"

                                // Reload expenses for this project
                                expenseRepository.getPendingExpensesByProject(projectId).fold(
                                    onSuccess = { expenseList ->
                                        expenses = expenseList
                                        filteredExpenses = expenseList.filter { expense ->
                                            (selectedDate.isBlank() || expense.date.contains(selectedDate)) &&
                                                    (selectedDept.isBlank() || expense.department == selectedDept)
                                        }
                                        // Clear and repopulate selectedItems
                                        selectedItems.clear()
                                        selectedItems.addAll(List(filteredExpenses.size) { false })
                                    },
                                    onFailure = { /* Handle error */ }
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D4ECF)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Approve Selected", color = Color.White)
                }
                Button(
                    onClick = {
                        // Reject selected expenses
                        scope.launch {
                            val selectedExpenses = filteredExpenses.filterIndexed { index, _ ->
                                index < selectedItems.size && selectedItems[index]
                            }

                            if (selectedExpenses.isNotEmpty()) {
                                error = null
                                success = null

                                selectedExpenses.forEach { expense ->
                                    // Get current approver information
                                    val authRepository = AuthRepository()
                                    val currentApproverPhone = authRepository.getCurrentUserPhoneNumber() ?: "+918765432109"
                                    val currentApproverName = "Current Approver"

                                    android.util.Log.d("BulkRejection", "❌ Bulk rejecting expense: ${expense.id}")
                                    android.util.Log.d("BulkRejection", "👤 Submitter: ${expense.submittedById}")
                                    android.util.Log.d("BulkRejection", "💰 Amount: ₹${expense.amount}")

                                    expenseRepository.updateExpenseStatus(
                                        expense.id,
                                        ExpenseStatus.REJECTED,
                                        currentApproverPhone,
                                        currentApproverName,
                                        "Rejected via bulk action"
                                    ).fold(
                                        onSuccess = {
                                            android.util.Log.d("BulkRejection", "✅ Successfully rejected expense ${expense.id}")

                                            // Send notification to submitter
                                            scope.launch {
                                                val notificationResult = notificationRepository.sendExpenseStatusNotification(
                                                    expense,
                                                    ExpenseStatus.REJECTED,
                                                    currentApproverName
                                                )
                                                notificationResult.fold(
                                                    onSuccess = {
                                                        android.util.Log.d("BulkRejection", "✅ Rejection notification sent for expense ${expense.id}")
                                                    },
                                                    onFailure = { error ->
                                                        android.util.Log.e("BulkRejection", "❌ Failed to send rejection notification: ${error.message}")
                                                    }
                                                )
                                            }
                                        },
                                        onFailure = { e ->
                                            android.util.Log.e("BulkRejection", "❌ Failed to reject expense ${expense.id}: ${e.message}")
                                            error = "Failed to reject expense: ${e.message}"
                                        }
                                    )
                                }

                                success = "${selectedExpenses.size} expense(s) rejected."

                                // Reload expenses for this project
                                expenseRepository.getPendingExpensesByProject(projectId).fold(
                                    onSuccess = { expenseList ->
                                        expenses = expenseList
                                        filteredExpenses = expenseList.filter { expense ->
                                            (selectedDate.isBlank() || expense.date.contains(selectedDate)) &&
                                                    (selectedDept.isBlank() || expense.department == selectedDept)
                                        }
                                        // Clear and repopulate selectedItems
                                        selectedItems.clear()
                                        selectedItems.addAll(List(filteredExpenses.size) { false })
                                    },
                                    onFailure = { /* Handle error */ }
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Reject Selected", color = Color.White)
                }
            }
        }

        // Error and Success Messages
        // Show clean "no data found" message when no expenses exist
        if (!isLoading && expenses.isEmpty() && error == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Assessment,
                        contentDescription = "No data",
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF999999)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No pending expenses found for this project",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "All expenses for this project have been processed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF999999),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Show error message only for actual errors (not empty data)
        error?.let { errorMsg ->
            if (!errorMsg.contains("No pending expenses found")) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Error",
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            errorMsg,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Success message (displayed at the bottom)
        success?.let { successMsg ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(
                    text = successMsg,
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun DelegatesScreen(onBack: () -> Unit = {}) {
    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Delegates Screen", modifier = Modifier.padding(start = 8.dp))
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Delegates Screen Content")
        }
    }
}

@Composable
fun ExportReportsScreen(onBack: () -> Unit = {}) {
    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Export Reports Screen", modifier = Modifier.padding(start = 8.dp))
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Export Reports Screen Content")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovalModalScreen(
    navController: NavHostController,
    expenseId: String,
    expenseRepository: ExpenseRepository,
    notificationRepository: NotificationRepository,
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }
    var expense by remember { mutableStateOf<Expense?>(null) }
    var reviewerNote by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    // Load expense details
    LaunchedEffect(expenseId) {
        scope.launch {
            expenseRepository.getAllExpenses().fold(
                onSuccess = { expenses ->
                    expense = expenses.find { it.id == expenseId }
                    isLoading = false
                    if (expense == null) {
                        error = "Expense not found"
                    }
                },
                onFailure = { e ->
                    error = "Failed to load expense: ${e.message}"
                    isLoading = false
                }
            )
        }
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
                    "AVR Entertainment",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF1565C0))
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (expense != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Title
                    Text(
                        "APPROVAL MODAL",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E2E2E),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    // Expense Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Department
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Department",
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    expense!!.department,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E2E2E)
                                )
                            }

                            // Subcategory
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Subcategory",
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    expense!!.category,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E2E2E)
                                )
                            }

                            // Amount
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Amount",
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    formatIndianNumber(expense!!.amount),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E),
                                    fontSize = 16.sp
                                )
                            }

                            // Date Submitted
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Date Submitted",
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    expense!!.date,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E2E2E)
                                )
                            }

                            // Payment Mode
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Payment Mode",
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    expense!!.modeOfPayment,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E2E2E)
                                )
                            }
                        }
                    }
                }

                item {
                    // Notes from Submitter
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
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AttachFile,
                                    contentDescription = "Notes",
                                    tint = Color(0xFF666666),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Notes from Submitter:",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                expense!!.description.ifEmpty { "No notes provided" },
                                color = Color(0xFF666666),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    // Attachment
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
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AttachFile,
                                    contentDescription = "Attachment",
                                    tint = Color(0xFF1565C0),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Attachment:",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E)
                                )
                                Spacer(Modifier.width(8.dp))
                                if (expense!!.attachment != null) {
                                    Text(
                                        "invoice_${expense!!.department.lowercase()}.jpg",
                                        color = Color(0xFF1565C0),
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.clickable {
                                            // TODO: Open attachment
                                        }
                                    )
                                } else {
                                    Text(
                                        "No attachment",
                                        color = Color(0xFF999999),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    // Budget Summary
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
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(
                                    Icons.Default.AttachFile,
                                    contentDescription = "Budget",
                                    tint = Color(0xFF666666),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Budget Summary:",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E)
                                )
                            }

                            // Budget details
                            val budget = 30000.0
                            val spent = 21000.0
                            val remaining = budget - spent
                            val afterApproval = remaining - expense!!.amount

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• Budget", color = Color(0xFF666666))
                                    Text(
                                        formatIndianNumber(budget),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• Spent", color = Color(0xFF666666))
                                    Text(
                                        "₹ ${String.format("%.0f", spent)}",
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• Remaining", color = Color(0xFF666666))
                                    Text(
                                        "₹ ${String.format("%.0f", remaining)}",
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• After Approval:", color = Color(0xFF666666))
                                    Text(
                                        "₹ ${String.format("%.0f", afterApproval)}",
                                        fontWeight = FontWeight.Bold,
                                        color = if (afterApproval >= 0) Color(0xFF4CAF50) else Color(
                                            0xFFE74C3C
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    // Reviewer Note Input
                    OutlinedTextField(
                        value = reviewerNote,
                        onValueChange = { reviewerNote = it },
                        label = { Text("Add Reviewer Note (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1565C0),
                            focusedLabelColor = Color(0xFF1565C0)
                        ),
                        maxLines = 3
                    )
                }

                item {
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Approve Button
                        Button(
                            onClick = {
                                isProcessing = true
                                error = null
                                success = null

                                scope.launch {
                                    // Get current approver information
                                    val authRepository = AuthRepository()
                                    val currentApproverPhone = authRepository.getCurrentUserPhoneNumber() ?: "+918765432109"
                                    val currentApproverName = "Current Approver" // In real app, get from user profile

                                    // Check budget limits before approval
                                    android.util.Log.d("ApprovalAction", "🔍 Checking budget limits before approval...")
                                    val budgetCheck = checkBudgetLimit(
                                        projectId = expense!!.projectId,
                                        department = expense!!.department,
                                        expenseAmount = expense!!.amount,
                                        projectRepository = ProjectRepository(),
                                        expenseRepository = expenseRepository
                                    )

                                    when (budgetCheck) {
                                        is BudgetCheckResult.Exceeded -> {
                                            // Budget exceeded - show toast and prevent approval
                                            android.util.Log.w("ApprovalAction", "❌ Budget exceeded: ${budgetCheck.message}")
                                            Toast.makeText(
                                                context,
                                                "❌ Cannot approve: ${budgetCheck.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            error = "Cannot approve: ${budgetCheck.message}"
                                            isProcessing = false
                                            return@launch
                                        }
                                        is BudgetCheckResult.Error -> {
                                            // Budget check failed
                                            android.util.Log.e("ApprovalAction", "❌ Budget check error: ${budgetCheck.message}")
                                            error = "Budget validation failed: ${budgetCheck.message}"
                                            isProcessing = false
                                            return@launch
                                        }
                                        is BudgetCheckResult.WithinLimit -> {
                                            // Budget is fine, proceed with approval
                                            android.util.Log.d("ApprovalAction", "✅ Budget check passed")
                                        }
                                    }

                                    // Use the enhanced approval method that includes budget checking
                                    expenseRepository.approveExpenseWithBudgetCheck(
                                        expenseId = expenseId,
                                        reviewerId = currentApproverPhone,
                                        reviewerName = currentApproverName,
                                        reviewerNote = reviewerNote.ifBlank { null },
                                        budgetRepository = BudgetRepository(),
                                        notificationRepository = notificationRepository,
                                        projectRepository = ProjectRepository()
                                    ).fold(
                                        onSuccess = {
                                            success = "Expense approved successfully! Budget limits checked."
                                            isProcessing = false

                                            android.util.Log.d("ApprovalAction", "✅ EXPENSE APPROVED!")
                                            android.util.Log.d("ApprovalAction", "📱 Expense ID: $expenseId")
                                            android.util.Log.d("ApprovalAction", "👤 Submitter: ${expense!!.submittedById}")
                                            android.util.Log.d("ApprovalAction", "💰 Amount: ₹${expense!!.amount}")
                                            android.util.Log.d("ApprovalAction", "📋 Project: ${expense!!.projectName}")
                                            android.util.Log.d("ApprovalAction", "🔔 Notification should be sent to: ${expense!!.submittedById}")

                                            // Show approval notification and toast
                                            try {
                                                val localNotificationService = LocalNotificationService(context)
                                                localNotificationService.sendDelayedNotification(
                                                    title = "Expense Approved!",
                                                    message = "${formatIndianNumber(expense!!.amount)} expense for ${expense!!.projectName} has been approved.",
                                                    delayMs = 500
                                                )
                                                
                                                Toast.makeText(
                                                    context,
                                                    "✅ Expense approved successfully!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } catch (e: Exception) {
                                                android.util.Log.e("ApprovalAction", "Failed to show notification: ${e.message}")
                                            }

                                            kotlinx.coroutines.delay(1500)
                                            navController.popBackStack()
                                        },
                                        onFailure = { e ->
                                            error = "Failed to approve expense: ${e.message}"
                                            isProcessing = false
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isProcessing
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Approve",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        // Reject Button
                        Button(
                            onClick = {
                                isProcessing = true
                                error = null
                                success = null

                                scope.launch {
                                    // Get current approver information
                                    val authRepository = AuthRepository()
                                    val currentApproverPhone = authRepository.getCurrentUserPhoneNumber() ?: "+918765432109"
                                    val currentApproverName = "Current Approver" // In real app, get from user profile

                                    expenseRepository.updateExpenseStatus(
                                        expenseId,
                                        ExpenseStatus.REJECTED,
                                        currentApproverPhone,
                                        currentApproverName,
                                        reviewerNote.ifBlank { "Rejected by approver" }
                                    ).fold(
                                        onSuccess = {
                                            android.util.Log.d("RejectionAction", "❌ EXPENSE REJECTED!")
                                            android.util.Log.d("RejectionAction", "📱 Expense ID: $expenseId")
                                            android.util.Log.d("RejectionAction", "👤 Submitter: ${expense!!.submittedById}")
                                            android.util.Log.d("RejectionAction", "💰 Amount: ₹${expense!!.amount}")
                                            android.util.Log.d("RejectionAction", "📋 Project: ${expense!!.projectName}")
                                            android.util.Log.d("RejectionAction", "🔔 Notification should be sent to: ${expense!!.submittedById}")

                                            // Send notification to submitter and wait for completion
                                            scope.launch {
                                                val notificationResult = notificationRepository.sendExpenseStatusNotification(
                                                    expense!!,
                                                    ExpenseStatus.REJECTED,
                                                    currentApproverName
                                                )
                                                notificationResult.fold(
                                                    onSuccess = {
                                                        android.util.Log.d("RejectionAction", "✅ Rejection notification sent successfully")
                                                    },
                                                    onFailure = { error ->
                                                        android.util.Log.e("RejectionAction", "❌ Failed to send rejection notification: ${error.message}")
                                                    }
                                                )
                                            }

                                            success = "Expense rejected."
                                            isProcessing = false

                                            kotlinx.coroutines.delay(1500)
                                            navController.popBackStack()
                                        },
                                        onFailure = { e ->
                                            error = "Failed to reject expense: ${e.message}"
                                            isProcessing = false
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C42)),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isProcessing
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Reject",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
                // Error and Success Messages
                error?.let { errorMsg ->
                    item {
                        Text(
                            text = errorMsg,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                success?.let { successMsg ->
                    item {
                        Text(
                            text = successMsg,
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        error ?: "Expense not found",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { navController.popBackStack() }
                    ) {
                        Text("Go Back")
                    }
                }
            }
        }
    }
}

@Composable
fun AttachmentDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onPdf: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add Attachment") },
            text = {
                Column {
                    TextButton(onClick = {
                        onCamera()
                        onDismiss()
                    }) { Text("Take Photo") }
                    TextButton(onClick = {
                        onGallery()
                        onDismiss()
                    }) { Text("Choose Image") }
                    TextButton(onClick = {
                        onPdf()
                        onDismiss()
                    }) { Text("Choose PDF") }
                }
            },
            confirmButton = { Spacer(Modifier) }, // Required, can be empty
            dismissButton = { Spacer(Modifier) }  // Optional, can be empty
        )
    }
}

@Composable
fun AddAttachmentSection() {
    var showDialog by remember { mutableStateOf(false) }

    // Launchers must be inside the composable!
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        // handle camera image (bitmap)
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // handle image uri
    }
    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // handle pdf uri
    }

    Button(onClick = { showDialog = true }) {
        Text("Add Attachment")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Attachment") },
            text = {
                Column {
                    TextButton(onClick = {
                        cameraLauncher.launch(null)
                        showDialog = false
                    }) { Text("Take Photo") }
                    TextButton(onClick = {
                        galleryLauncher.launch("image/*")
                        showDialog = false
                    }) { Text("Choose Image") }
                    TextButton(onClick = {
                        pdfLauncher.launch("application/pdf")
                        showDialog = false
                    }) { Text("Choose PDF") }
                }
            },
            confirmButton = { Spacer(Modifier) }, // Required, can be empty
            dismissButton = { Spacer(Modifier) }  // Optional, can be empty
        )
    }
}

@Composable
fun UnknownUserScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon
        Card(
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚠️",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp),
                    color = Color(0xFFD32F2F)
                )
            }
        }

        // Title
        Text(
            text = "Access Restricted",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E5CFF)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Main message
        Text(
            text = "Your phone number is not registered in our system.",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Instructions
        Text(
            text = "Please contact the administrator to get your role assigned and gain access to the AVR Entertainment Expense Management System.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Contact info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Contact Administrator",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E5CFF)
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "📧 admin@avrentertainment.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "📞 +91-XXXXX-XXXXX",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF333333)
                )
            }
        }

        // Back to login button
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E5CFF),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Back to Login",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    departmentBudgets: Map<String, DepartmentBudgetData>
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (departmentBudgets.isEmpty()) {
            // No data placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0E0E0), CircleShape)
                    .border(2.dp, Color(0xFFBDBDBD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No Data",
                    color = Color(0xFF666666),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            // Define department colors to match the cards
            val departmentColors = mapOf(
                "Sound" to Color(0xFF1565C0),      // Blue
                "Other" to Color(0xFF4CAF50),      // Green  
                "Art" to Color(0xFF9C27B0),        // Purple
                "Camera" to Color(0xFFFF9800),     // Orange
                "Costumes" to Color(0xFFF44336),   // Red
                "Lighting" to Color(0xFF607D8B),   // Blue Grey
                "Set Design" to Color(0xFF795548), // Brown
                "Set" to Color(0xFF795548)         // Brown (alternative name)
            )

            val totalSpent = departmentBudgets.values.sumOf { it.spent }
            
            if (totalSpent > 0) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
                    val radius = size.minDimension / 2f
                    val innerRadius = radius * 0.5f // For donut chart
                    
                    var startAngle = -90f // Start from top
                    
                    // Draw each department slice
                    departmentBudgets.entries.forEach { (department, budgetData) ->
                        val percentage = budgetData.spent / totalSpent
                        val sweepAngle = (percentage * 360f).toFloat()
                        val color = departmentColors[department] ?: Color.Gray
                        
                        // Draw the slice
                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                center.x - radius,
                                center.y - radius
                            ),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                        )
                        
                        // Draw white separator line
                        if (sweepAngle > 0) {
                            val endAngle = startAngle + sweepAngle
                            val lineEndX = center.x + radius * cos(Math.toRadians(endAngle.toDouble())).toFloat()
                            val lineEndY = center.y + radius * sin(Math.toRadians(endAngle.toDouble())).toFloat()
                            val lineStartX = center.x + innerRadius * cos(Math.toRadians(endAngle.toDouble())).toFloat()
                            val lineStartY = center.y + innerRadius * sin(Math.toRadians(endAngle.toDouble())).toFloat()
                            
                            drawLine(
                                color = Color.White,
                                start = androidx.compose.ui.geometry.Offset(lineStartX, lineStartY),
                                end = androidx.compose.ui.geometry.Offset(lineEndX, lineEndY),
                                strokeWidth = 3.dp.toPx()
                            )
                        }
                        
                        startAngle += sweepAngle
                    }
                    
                    // Draw center circle to create donut effect
                    drawCircle(
                        color = Color.White,
                        radius = innerRadius,
                        center = center
                    )
                    
                    // Draw center circle border
                    drawCircle(
                        color = Color(0xFFE0E0E0),
                        radius = innerRadius,
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                    )
                }
            } else {
                // Fallback for zero spending
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE0E0E0), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No Spending",
                        color = Color(0xFF666666),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun DepartmentReportScreen(
    navController: NavHostController,
    projectId: String,
    department: String,
    projectRepository: ProjectRepository,
    expenseRepository: ExpenseRepository,
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    
    // State variables
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var departmentExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var filteredExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Filter states
    var selectedStatus by remember { mutableStateOf("All") }
    var selectedTimeRange by remember { mutableStateOf("All Time") }
    
    // Statistics
    var totalSpent by remember { mutableStateOf(0.0) }
    var approvedCount by remember { mutableStateOf(0) }
    var pendingCount by remember { mutableStateOf(0) }
    var rejectedCount by remember { mutableStateOf(0) }
    
    // Handle back press
    androidx.activity.compose.BackHandler {
        onBack()
    }
    
    // Load department data
    LaunchedEffect(projectId, department) {
        scope.launch {
            try {
                isLoading = true
                error = null
                
                // Load project details
                projectRepository.getProjectById(projectId).fold(
                    onSuccess = { project ->
                        selectedProject = project
                    },
                    onFailure = { e ->
                        error = "Failed to load project: ${e.message}"
                    }
                )
                
                // Load department expenses
                expenseRepository.getAllExpenses().fold(
                    onSuccess = { allExpenses ->
                        // Filter expenses for this project and department
                        departmentExpenses = allExpenses.filter { expense ->
                            expense.projectId == projectId && expense.department == department
                        }.sortedByDescending { it.submittedAt.seconds }
                        
                        filteredExpenses = departmentExpenses
                        
                        // Calculate statistics
                        totalSpent = departmentExpenses.filter { it.status == ExpenseStatus.APPROVED }.sumOf { it.amount }
                        approvedCount = departmentExpenses.count { it.status == ExpenseStatus.APPROVED }
                        pendingCount = departmentExpenses.count { it.status == ExpenseStatus.PENDING }
                        rejectedCount = departmentExpenses.count { it.status == ExpenseStatus.REJECTED }
                        
                        android.util.Log.d("DepartmentReport", "Loaded ${departmentExpenses.size} expenses for $department")
                    },
                    onFailure = { e ->
                        error = "Failed to load expenses: ${e.message}"
                    }
                )
                
                isLoading = false
            } catch (e: Exception) {
                error = "Error loading department data: ${e.message}"
                isLoading = false
            }
        }
    }
    
    // Filter expenses when filter criteria change
    LaunchedEffect(selectedStatus, selectedTimeRange, departmentExpenses) {
        filteredExpenses = departmentExpenses.filter { expense ->
            val statusMatch = when (selectedStatus) {
                "All" -> true
                "Approved" -> expense.status == ExpenseStatus.APPROVED
                "Pending" -> expense.status == ExpenseStatus.PENDING
                "Rejected" -> expense.status == ExpenseStatus.REJECTED
                else -> true
            }
            
            val timeMatch = when (selectedTimeRange) {
                "All Time" -> true
                "This Month" -> {
                    val currentTime = System.currentTimeMillis()
                    val oneMonthAgo = currentTime - (30 * 24 * 60 * 60 * 1000)
                    val expenseTime = expense.submittedAt.seconds * 1000
                    expenseTime > oneMonthAgo
                }
                "This Week" -> {
                    val currentTime = System.currentTimeMillis()
                    val oneWeekAgo = currentTime - (7 * 24 * 60 * 60 * 1000)
                    val expenseTime = expense.submittedAt.seconds * 1000
                    expenseTime > oneWeekAgo
                }
                else -> true
            }
            
            statusMatch && timeMatch
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    "$department Department Report",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1565C0))
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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
                        error!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Project Info
                    selectedProject?.let { project ->
                        Text(
                            "${project.name} - $department Department",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E2E2E)
                            )
                        )
                        Text(
                            "Project Budget: ${formatIndianNumber(project.budget)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666)
                        )
                    }
                }
                
                item {
                    // Statistics Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Total Spent",
                            value = formatIndianNumber(totalSpent),
                            color = Color(0xFF1565C0),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Approved",
                            value = approvedCount.toString(),
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Pending",
                            value = pendingCount.toString(),
                            color = Color(0xFFFF9800),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Rejected",
                            value = rejectedCount.toString(),
                            color = Color(0xFFF44336),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                item {
                    // Filters
                    Text(
                        "Filters",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Status Filter
                        FilterDropdown(
                            label = "Status",
                            selectedValue = selectedStatus,
                            options = listOf("All", "Approved", "Pending", "Rejected"),
                            onValueChange = { selectedStatus = it },
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Time Range Filter
                        FilterDropdown(
                            label = "Time Range",
                            selectedValue = selectedTimeRange,
                            options = listOf("All Time", "This Month", "This Week"),
                            onValueChange = { selectedTimeRange = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                item {
                    Text(
                        "Expense History (${filteredExpenses.size} expenses)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E)
                        )
                    )
                }
                
                // Expense List
                if (filteredExpenses.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "No expenses found",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    "Try adjusting your filters",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF999999)
                                )
                            }
                        }
                    }
                } else {
                    items(filteredExpenses) { expense ->
                        ExpenseItemCard(
                            expense = expense,
                            onClick = {
                                // Navigate to expense details or approval modal if needed
                                if (expense.status == ExpenseStatus.PENDING) {
                                    navController.navigate("approval_modal/${expense.id}")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ExpenseItemCard(
    expense: Expense,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        expense.category,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF2E2E2E)
                    )
                    Text(
                        "Submitted: ${expense.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                    Text(
                        "By: ${expense.submittedBy}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        formatIndianNumber(expense.amount),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF2E2E2E)
                    )
                    
                    val statusColor = when (expense.status) {
                        ExpenseStatus.APPROVED -> Color(0xFF4CAF50)
                        ExpenseStatus.PENDING -> Color(0xFFFF9800)
                        ExpenseStatus.REJECTED -> Color(0xFFF44336)
                    }
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = statusColor),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            expense.status.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            if (expense.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    expense.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

fun calculateAnalytics(
    expenses: List<Expense>, 
    projects: List<Project>,
    onTotalSpentUpdate: (Double) -> Unit,
    onDepartmentDataUpdate: (Map<String, Double>) -> Unit,
    onProjectDataUpdate: (Map<String, Double>) -> Unit,
    onStatusDataUpdate: (Map<String, Int>) -> Unit,
    onMonthlyDataUpdate: (Map<String, Double>) -> Unit
) {
    // Total spent (approved expenses only)
    val totalSpent = expenses.filter { it.status == ExpenseStatus.APPROVED }.sumOf { it.amount }
    onTotalSpentUpdate(totalSpent)
    
    // Department breakdown (approved expenses only)
    val departmentData = expenses
        .filter { it.status == ExpenseStatus.APPROVED }
        .groupBy { it.department }
        .mapValues { (_, expenseList) -> expenseList.sumOf { it.amount } }
    onDepartmentDataUpdate(departmentData)
    
    // Project breakdown (approved expenses only)
    val projectData = expenses
        .filter { it.status == ExpenseStatus.APPROVED }
        .groupBy { expense -> 
            projects.find { it.id == expense.projectId }?.name ?: "Unknown Project"
        }
        .mapValues { (_, expenseList) -> expenseList.sumOf { it.amount } }
    onProjectDataUpdate(projectData)
    
    // Status breakdown (count)
    val statusData = expenses.groupBy { it.status.name }.mapValues { (_, expenseList) -> expenseList.size }
    onStatusDataUpdate(statusData)
    
    // Monthly breakdown (approved expenses only)
    val monthlyData = expenses
        .filter { it.status == ExpenseStatus.APPROVED }
        .groupBy { expense ->
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = expense.submittedAt.seconds * 1000
            "${calendar.get(java.util.Calendar.MONTH) + 1}/${calendar.get(java.util.Calendar.YEAR)}"
        }
        .mapValues { (_, expenseList) -> expenseList.sumOf { it.amount } }
    onMonthlyDataUpdate(monthlyData)
}

@Composable
fun ReportsScreen(
    navController: NavHostController,
    projectRepository: ProjectRepository,
    expenseRepository: ExpenseRepository,
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    
    // State variables
    var allProjects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var allExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var filteredExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Filter states
    var selectedProject by remember { mutableStateOf("All Projects") }
    var selectedDepartment by remember { mutableStateOf("All Departments") }
    var selectedStatus by remember { mutableStateOf("All") }
    var selectedTimeRange by remember { mutableStateOf("All Time") }
    var chartType by remember { mutableStateOf("Pie Chart") }
    
    // Analytics data
    var totalSpent by remember { mutableStateOf(0.0) }
    var totalBudget by remember { mutableStateOf(0.0) }
    var departmentData by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var projectData by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var statusData by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var monthlyData by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    
    // Available filter options
    var availableDepartments by remember { mutableStateOf<List<String>>(emptyList()) }
    
    // Handle back press
    androidx.activity.compose.BackHandler {
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
                        totalBudget = projects.sumOf { it.budget }
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
                        
                        // Extract unique departments
                        availableDepartments = expenses.map { it.department }.distinct().sorted()
                        
                        // Calculate initial analytics
                        calculateAnalytics(expenses, allProjects, 
                            { totalSpent = it },
                            { departmentData = it },
                            { projectData = it },
                            { statusData = it },
                            { monthlyData = it }
                        )
                        
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
    
    // Filter expenses when filter criteria change
    LaunchedEffect(selectedProject, selectedDepartment, selectedStatus, selectedTimeRange, allExpenses) {
        filteredExpenses = allExpenses.filter { expense ->
            val projectMatch = selectedProject == "All Projects" || 
                allProjects.find { it.id == expense.projectId }?.name == selectedProject
            
            val departmentMatch = selectedDepartment == "All Departments" || 
                expense.department == selectedDepartment
            
            val statusMatch = when (selectedStatus) {
                "All" -> true
                "Approved" -> expense.status == ExpenseStatus.APPROVED
                "Pending" -> expense.status == ExpenseStatus.PENDING
                "Rejected" -> expense.status == ExpenseStatus.REJECTED
                else -> true
            }
            
            val timeMatch = when (selectedTimeRange) {
                "All Time" -> true
                "This Month" -> {
                    val currentTime = System.currentTimeMillis()
                    val oneMonthAgo = currentTime - (30 * 24 * 60 * 60 * 1000)
                    val expenseTime = expense.submittedAt.seconds * 1000
                    expenseTime > oneMonthAgo
                }
                "This Week" -> {
                    val currentTime = System.currentTimeMillis()
                    val oneWeekAgo = currentTime - (7 * 24 * 60 * 60 * 1000)
                    val expenseTime = expense.submittedAt.seconds * 1000
                    expenseTime > oneWeekAgo
                }
                "Last 3 Months" -> {
                    val currentTime = System.currentTimeMillis()
                    val threeMonthsAgo = currentTime - (90 * 24 * 60 * 60 * 1000)
                    val expenseTime = expense.submittedAt.seconds * 1000
                    expenseTime > threeMonthsAgo
                }
                else -> true
            }
            
            projectMatch && departmentMatch && statusMatch && timeMatch
        }
        
        // Recalculate analytics with filtered data
        calculateAnalytics(filteredExpenses, allProjects,
            { totalSpent = it },
            { departmentData = it },
            { projectData = it },
            { statusData = it },
            { monthlyData = it }
        )
    }
    

    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    "Reports & Analytics",
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1565C0))
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
                        color = Color(0xFF1565C0)
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
                        error!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Summary Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryCard(
                            title = "Total Spent",
                            value = formatIndianNumber(totalSpent),
                            subtitle = "Approved expenses",
                            color = Color(0xFF1565C0),
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "Total Budget",
                            value = formatIndianNumber(totalBudget),
                            subtitle = "All projects",
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val budgetUtilization = if (totalBudget > 0) (totalSpent / totalBudget * 100) else 0.0
                        SummaryCard(
                            title = "Budget Used",
                            value = "${String.format("%.1f", budgetUtilization)}%",
                            subtitle = "Of total budget",
                            color = if (budgetUtilization > 80) Color(0xFFF44336) else Color(0xFFFF9800),
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "Total Expenses",
                            value = "${filteredExpenses.size}",
                            subtitle = "Matching filters",
                            color = Color(0xFF9C27B0),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                item {
                    // Filters Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Filters",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E)
                                ),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            // First row of filters
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                FilterDropdown(
                                    label = "Project",
                                    selectedValue = selectedProject,
                                    options = listOf("All Projects") + allProjects.map { it.name },
                                    onValueChange = { selectedProject = it },
                                    modifier = Modifier.weight(1f)
                                )
                                
                                FilterDropdown(
                                    label = "Department",
                                    selectedValue = selectedDepartment,
                                    options = listOf("All Departments") + availableDepartments,
                                    onValueChange = { selectedDepartment = it },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Second row of filters
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                FilterDropdown(
                                    label = "Status",
                                    selectedValue = selectedStatus,
                                    options = listOf("All", "Approved", "Pending", "Rejected"),
                                    onValueChange = { selectedStatus = it },
                                    modifier = Modifier.weight(1f)
                                )
                                
                                FilterDropdown(
                                    label = "Time Range",
                                    selectedValue = selectedTimeRange,
                                    options = listOf("All Time", "This Week", "This Month", "Last 3 Months"),
                                    onValueChange = { selectedTimeRange = it },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                
                item {
                    // Chart Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Expense Distribution",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E2E2E)
                                    )
                                )
                                
                                FilterDropdown(
                                    label = "",
                                    selectedValue = chartType,
                                    options = listOf("Pie Chart", "Bar Chart"),
                                    onValueChange = { chartType = it },
                                    modifier = Modifier.width(140.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (departmentData.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No data available for selected filters",
                                        color = Color(0xFF666666),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            } else {
                                when (chartType) {
                                    "Pie Chart" -> {
                                        DynamicPieChart(
                                            data = departmentData,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(300.dp)
                                        )
                                    }
                                    "Bar Chart" -> {
                                        DynamicBarChart(
                                            data = departmentData,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(300.dp),
                                            budgetData = projectData
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                item {
                    // Department Breakdown Table
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Department Breakdown",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E)
                                ),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            if (departmentData.isEmpty()) {
                                Text(
                                    "No department data available",
                                    color = Color(0xFF666666),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                departmentData.entries.sortedByDescending { it.value }.forEach { (department, amount) ->
                                    val percentage = if (totalSpent > 0) (amount / totalSpent * 100) else 0.0
                                    
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                department,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = Color(0xFF2E2E2E)
                                            )
                                            Text(
                                                "${String.format("%.1f", percentage)}% of total",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF666666)
                                            )
                                        }
                                        
                                        Text(
                                            formatIndianNumber(amount),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFF1565C0)
                                        )
                                    }
                                    
                                    if (department != departmentData.entries.sortedByDescending { it.value }.last().key) {
                                        Divider(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            color = Color(0xFFE0E0E0)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                item {
                    // Status Overview
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Expense Status Overview",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E)
                                ),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val approvedCount = statusData["APPROVED"] ?: 0
                                val pendingCount = statusData["PENDING"] ?: 0
                                val rejectedCount = statusData["REJECTED"] ?: 0
                                
                                StatusCard(
                                    title = "Approved",
                                    count = approvedCount,
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.weight(1f)
                                )
                                StatusCard(
                                    title = "Pending",
                                    count = pendingCount,
                                    color = Color(0xFFFF9800),
                                    modifier = Modifier.weight(1f)
                                )
                                StatusCard(
                                    title = "Rejected",
                                    count = rejectedCount,
                                    color = Color(0xFFF44336),
                                    modifier = Modifier.weight(1f)
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
fun SummaryCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatusCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                count.toString(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DynamicPieChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFF9C27B0), // Purple (like your image)
        Color(0xFF4285F4), // Blue
        Color(0xFF34A853), // Green
        Color(0xFFFF9800), // Orange
        Color(0xFFF44336)  // Red
    )
    
    Column(modifier = modifier) {
        if (data.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No data available",
                    color = Color(0xFF666666),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            val total = data.values.sum()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Pie Chart
                Box(
                    modifier = Modifier
                        .size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
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
                
                // Legend
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    data.entries.forEachIndexed { index, (department, amount) ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        colors[index % colors.size],
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    department,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF2E2E2E)
                                )
                                Text(
                                    formatIndianNumber(amount),
                                    fontSize = 10.sp,
                                    color = Color(0xFF666666)
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
fun DynamicBarChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier,
    budgetData: Map<String, Double> = emptyMap() // Optional budget data for stacked bars
) {
    if (data.isEmpty()) return
    
    val sortedData = data.entries.sortedByDescending { it.value }
    val maxValue = budgetData.values.maxOrNull() ?: data.values.maxOrNull() ?: 0.0
    
    // Ensure we have budget data for all departments
    val completeBudgetData = data.mapKeys { it.key }.mapValues { (category, spentValue) ->
        budgetData[category] ?: (spentValue * 1.3) // Default to 30% buffer if no budget specified
    }
    
    val adjustedMaxValue = completeBudgetData.values.maxOrNull() ?: maxValue
    
    Box(modifier = modifier) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) {
                // Chart area with fixed height
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    // Value labels row - Show budget amounts
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        sortedData.forEachIndexed { index, (category, spentValue) ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                            val budget = completeBudgetData[category] ?: spentValue
                                Text(
                                "₹${String.format("%.0f", budget / 1000)}K",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Bars row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                    sortedData.forEachIndexed { index, (category, spentValue) ->
                        val budget = completeBudgetData[category] ?: spentValue
                        val remainingValue = (budget - spentValue).coerceAtLeast(0.0)
                        val spentHeight = (spentValue / adjustedMaxValue * 160.dp.value).dp
                        val remainingHeight = (remainingValue / adjustedMaxValue * 160.dp.value).dp
                        val totalHeight = spentHeight + remainingHeight
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                            // Stacked bar container
                            Column(
                                    modifier = Modifier
                                        .width(45.dp)
                                    .height(totalHeight),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                // Remaining amount (top part) - light blue/grey
                                if (remainingValue > 0) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(remainingHeight)
                                        .background(
                                                Color(0xFFE3F2FD), // Light blue for remaining budget
                                                RoundedCornerShape(
                                                    topStart = 4.dp,
                                                    topEnd = 4.dp,
                                                    bottomStart = 0.dp,
                                                    bottomEnd = 0.dp
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (remainingHeight > 25.dp) { // Show text if bar is tall enough
                                            Text(
                                                text = "₹${String.format("%.0f", remainingValue / 1000)}K",
                                                color = Color(0xFF1976D2),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Medium,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                                
                                // Spent amount (bottom part) - green
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(spentHeight)
                                        .background(
                                            Color(0xFF4CAF50), // Green for spent
                                            RoundedCornerShape(
                                                topStart = if (remainingValue <= 0) 4.dp else 0.dp,
                                                topEnd = if (remainingValue <= 0) 4.dp else 0.dp,
                                                bottomStart = 4.dp,
                                                bottomEnd = 4.dp
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (spentHeight > 30.dp) { // Only show text if bar is tall enough
                                        Text(
                                            text = "₹${String.format("%.0f", spentValue / 1000)}K",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Category labels row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Top
                    ) {
                    sortedData.forEachIndexed { index, (category, _) ->
                            Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    category,
                                    fontSize = 11.sp,
                                    color = Color(0xFF666666),
                                textAlign = TextAlign.Center,
                                maxLines = 2
                                )
                            }
                        }
                    }
                }
            
            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spent legend item
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFF4CAF50), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Spent",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Remaining legend item
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFFB0BEC5), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Remaining",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

