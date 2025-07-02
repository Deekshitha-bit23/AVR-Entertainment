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
import com.deeksha.avrentertainment.repository.NotificationRepository
import com.deeksha.avrentertainment.repository.AuthRepository
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextOverflow

// Notification Item Component
@Composable
fun NotificationItem(
    notification: NotificationData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = when (notification.type) {
                        NotificationType.EXPENSE_SUBMITTED -> Color(0xFF2E5CFF)
                        NotificationType.BUDGET_EXCEEDED_PROJECT, NotificationType.BUDGET_EXCEEDED_DEPARTMENT -> Color(0xFFE53E3E)
                        NotificationType.PENDING_APPROVAL_REMINDER -> Color(0xFFFF9500)
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.weight(1f)
                )
                
                // Notification type indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when (notification.type) {
                                NotificationType.EXPENSE_SUBMITTED -> Color(0xFF2E5CFF)
                                NotificationType.BUDGET_EXCEEDED_PROJECT, NotificationType.BUDGET_EXCEEDED_DEPARTMENT -> Color(0xFFE53E3E)
                                NotificationType.PENDING_APPROVAL_REMINDER -> Color(0xFFFF9500)
                                else -> Color.Gray
                            }
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Notification Panel Component
@Composable
fun NotificationPanel(
    notifications: List<NotificationData>,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pending Notifications",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4169E1)
                        )
                    )
                    if (notifications.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(
                            containerColor = Color(0xFFE53E3E),
                            contentColor = Color.White
                        ) {
                            Text(
                                text = notifications.size.toString(),
                                fontSize = 10.sp
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
                Spacer(modifier = Modifier.height(12.dp))
                
                if (notifications.isEmpty()) {
                    Text(
                        text = "No pending notifications",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(notifications) { notification ->
                            NotificationItem(notification = notification)
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
    
    // Notification states
    var approverNotifications by remember { mutableStateOf<List<NotificationData>>(emptyList()) }
    var isLoadingNotifications by remember { mutableStateOf(true) }
    var isNotificationPanelExpanded by remember { mutableStateOf(false) }
    
    // Load approver notifications on screen load
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoadingNotifications = true
                val authRepository = AuthRepository()
                val currentUserId = authRepository.getCurrentUserPhoneNumber()
                
                if (currentUserId != null) {
                    // Check if current user is an approver/production head
                    authRepository.getUserRole(currentUserId).fold(
                        onSuccess = { userRole ->
                            if (userRole == com.deeksha.avrentertainment.models.UserRole.APPROVER || 
                                userRole == com.deeksha.avrentertainment.models.UserRole.PRODUCTION_HEAD) {
                                
                                val notificationRepository = NotificationRepository(context)
                                notificationRepository.getApproverNotificationsPreLogin(currentUserId, 10).fold(
                                    onSuccess = { notifications ->
                                        approverNotifications = notifications
                                        android.util.Log.d("ProjectSelection", "✅ Loaded ${notifications.size} approver notifications for $currentUserId")
                                    },
                                    onFailure = { e ->
                                        android.util.Log.e("ProjectSelection", "❌ Error loading notifications: ${e.message}")
                                    }
                                )
                            } else {
                                android.util.Log.d("ProjectSelection", "Current user is not an approver, skipping notifications")
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
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                // Approver Notifications Panel (only show if there are notifications)
                if (!isLoadingNotifications && approverNotifications.isNotEmpty()) {
                    NotificationPanel(
                        notifications = approverNotifications,
                        isExpanded = isNotificationPanelExpanded,
                        onToggleExpanded = { isNotificationPanelExpanded = !isNotificationPanelExpanded }
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
                                                    notification = notification,
                                                    modifier = Modifier.fillMaxWidth()
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