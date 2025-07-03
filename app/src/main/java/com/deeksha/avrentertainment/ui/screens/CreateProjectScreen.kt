package com.deeksha.avrentertainment.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import com.deeksha.avrentertainment.models.*
import com.deeksha.avrentertainment.repository.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import android.app.DatePickerDialog

// Data class for Firebase users
data class FirebaseUser(
    val phone: String,
    val name: String? = null,
    val role: UserRole? = null
)

// Data class for dynamic department input boxes
data class DepartmentInputBox(
    val id: Int,
    val departmentName: String = "",
    val budget: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    navController: NavHostController,
    onProjectCreated: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // State for form fields
    var projectName by remember { mutableStateOf("") }
    var projectDescription by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    var selectedApprover by remember { mutableStateOf<FirebaseUser?>(null) }
    var selectedTeamMembers by remember { mutableStateOf<List<FirebaseUser>>(emptyList()) }
    var totalBudget by remember { mutableStateOf("") }
    var departmentBudgets by remember { mutableStateOf(mutableMapOf<String, String>()) }
    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Search states
    var approverSearchQuery by remember { mutableStateOf("") }
    var memberSearchQuery by remember { mutableStateOf("") }
    var availableApprovers by remember { mutableStateOf<List<FirebaseUser>>(emptyList()) }
    var availableUsers by remember { mutableStateOf<List<FirebaseUser>>(emptyList()) }
    var showApproverDropdown by remember { mutableStateOf(false) }
    var showMemberDropdown by remember { mutableStateOf(false) }
    var isLoadingUsers by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val calendar = Calendar.getInstance()

    val defaultDepartments = listOf("Costumes", "Camera", "Lighting", "Sound", "Art", "Other")
    var selectedDepartment by remember { mutableStateOf("") }
    var departmentBudgetInput by remember { mutableStateOf("") }
    
    // State for dynamic department input boxes
    var departmentInputBoxes by remember { mutableStateOf(listOf<DepartmentInputBox>()) }

    // Enhanced search functions
    fun searchApprovers(query: String): List<FirebaseUser> {
        if (query.isEmpty()) return emptyList()
        
        return availableApprovers.filter { user ->
            val normalizedQuery = query.lowercase().trim()
            val nameMatch = user.name?.lowercase()?.contains(normalizedQuery) == true
            
            // Enhanced phone number matching
            val cleanUserPhone = user.phone.replace(Regex("[^0-9]"), "")
            val cleanSearchQuery = normalizedQuery.replace(Regex("[^0-9]"), "")
            val phoneMatch = cleanUserPhone.contains(cleanSearchQuery) && cleanSearchQuery.length >= 3
            
            nameMatch || phoneMatch
        }.take(5)
    }
    
    fun searchUsers(query: String): List<FirebaseUser> {
        if (query.isEmpty()) return emptyList()
        
        return availableUsers.filter { user ->
            val normalizedQuery = query.lowercase().trim()
            val nameMatch = user.name?.lowercase()?.contains(normalizedQuery) == true
            
            // Enhanced phone number matching
            val cleanUserPhone = user.phone.replace(Regex("[^0-9]"), "")
            val cleanSearchQuery = normalizedQuery.replace(Regex("[^0-9]"), "")
            val phoneMatch = cleanUserPhone.contains(cleanSearchQuery) && cleanSearchQuery.length >= 3
            
            // Exclude already selected members
            !selectedTeamMembers.contains(user) && (nameMatch || phoneMatch)
        }.take(5)
    }

    // Load users from Firebase
    fun loadUsersFromFirebase() {
        scope.launch {
            try {
                isLoadingUsers = true
                val firestore = FirebaseFirestore.getInstance()
                
                // Load all users from Firebase
                val allUsersQuery = firestore.collection("users").get().await()
                
                val allUsers = allUsersQuery.documents.mapNotNull { doc ->
                    try {
                        val phone = doc.id
                        val name = doc.getString("name") ?: "Unknown"
                        val roleString = doc.getString("role")?.uppercase() ?: ""
                        
                        android.util.Log.d("CreateProject", "Loading user: phone=$phone, name=$name, role=$roleString")
                        
                        val role = when (roleString.trim()) {
                            "APPROVER", "Approver", "approver" -> UserRole.APPROVER
                            "USER", "User", "user" -> UserRole.USER
                            "PRODUCTION HEAD", "Production Head", "production head" -> UserRole.PRODUCTION_HEAD
                            else -> {
                                android.util.Log.w("CreateProject", "Unknown role: '$roleString' for user $phone")
                                UserRole.USER
                            }
                        }
                        
                        FirebaseUser(phone = phone, name = name, role = role)
                    } catch (e: Exception) {
                        android.util.Log.e("CreateProject", "Error parsing user document: ${doc.id}", e)
                        null
                    }
                }
                
                // Separate by role
                availableApprovers = allUsers.filter { it.role == UserRole.APPROVER }
                availableUsers = allUsers.filter { it.role == UserRole.USER }
                
                isLoadingUsers = false
                android.util.Log.d("CreateProject", "✅ Loaded ${availableApprovers.size} approvers and ${availableUsers.size} users")
                
            } catch (e: Exception) {
                isLoadingUsers = false
                error = "Failed to load users: ${e.message}"
                android.util.Log.e("CreateProject", "❌ Error loading users", e)
            }
        }
    }

    // Load users when screen opens
    LaunchedEffect(Unit) {
        loadUsersFromFirebase()
    }

    // Function to show date picker
    fun showDatePicker(
        initialDate: Date?,
        isStartDate: Boolean,
        onDateSelected: (Date) -> Unit
    ) {
        val initialCalendar = Calendar.getInstance()
        if (initialDate != null) {
            initialCalendar.time = initialDate
        }
        
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                onDateSelected(selectedCalendar.time)
            },
            initialCalendar.get(Calendar.YEAR),
            initialCalendar.get(Calendar.MONTH),
            initialCalendar.get(Calendar.DAY_OF_MONTH)
        )
        
        if (isStartDate) {
            datePicker.setTitle("Select Start Date")
        } else {
            datePicker.setTitle("Select End Date (Optional)")
            // For end date, set minimum date to start date if available
            startDate?.let { startDateValue ->
                datePicker.datePicker.minDate = startDateValue.time
            }
        }
        
        datePicker.show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("New Project", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Cancel", color = Color(0xFF2E5CFF))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9F7F4)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Project Details
            item {
                SectionHeader("📁 PROJECT DETAILS")
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Project Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = false
                )
                // Project name validation - only show after user interaction
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = projectDescription,
                    onValueChange = { projectDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
            
            // Timeline
            item {
                SectionHeader("📅 TIMELINE")
                
                // Start Date (Mandatory)
                OutlinedTextField(
                    value = startDate?.let { dateFormatter.format(it) } ?: "",
                    onValueChange = { },
                    label = { Text("Start Date *") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    isError = false,
                    trailingIcon = {
                        IconButton(onClick = { 
                            showDatePicker(startDate, true) { selectedDate ->
                                startDate = selectedDate
                                // Clear end date if it's before start date
                                endDate?.let { currentEndDate ->
                                    if (currentEndDate.before(selectedDate)) {
                                        endDate = null
                                    }
                                }
                            }
                        }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                    }
                )
                // Start date validation - only show after user interaction
                
                Spacer(Modifier.height(12.dp))
                
                // End Date (Optional)
                OutlinedTextField(
                    value = endDate?.let { dateFormatter.format(it) } ?: "",
                    onValueChange = { },
                    label = { Text("End Date (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Row {
                            if (endDate != null) {
                                IconButton(onClick = { endDate = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear Date")
                                }
                            }
                            IconButton(onClick = { 
                                showDatePicker(endDate, false) { selectedDate ->
                                    endDate = selectedDate
                                }
                            }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                            }
                        }
                    }
                )
                Text(
                    "Start date is mandatory. End date is optional.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // Team Assignment
            item {
                SectionHeader("👥 TEAM ASSIGNMENT")
                
                // Approver Selection
                Text("Project Manager (Approver) *", fontWeight = FontWeight.Medium, color = Color(0xFF2E5CFF))
                Spacer(Modifier.height(8.dp))
                
                // Selected Approver Display
                if (selectedApprover != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF4CAF50))
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        selectedApprover!!.name ?: "Unknown",
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        selectedApprover!!.phone,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                            IconButton(onClick = { selectedApprover = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove")
                            }
                        }
                    }
                } else {
                    // Approver Search
                    OutlinedTextField(
                        value = approverSearchQuery,
                        onValueChange = { query ->
                            approverSearchQuery = query
                            // Show dropdown immediately when typing starts and we have users
                            showApproverDropdown = query.isNotEmpty() && availableApprovers.isNotEmpty()
                        },
                        label = { Text("Search Approver by name or phone") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF2E5CFF))
                        },
                        trailingIcon = {
                            Row {
                                if (approverSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { 
                                        approverSearchQuery = ""
                                        showApproverDropdown = false
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                                    }
                                }
                                if (isLoadingUsers) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFF2E5CFF)
                                    )
                                }
                            }
                        },
                        placeholder = { 
                            Text(
                                if (isLoadingUsers) "Loading approvers..." 
                                else "Type name or phone number...",
                                color = Color.Gray
                            )
                        },
                        enabled = !isLoadingUsers,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2E5CFF),
                            focusedLabelColor = Color(0xFF2E5CFF),
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            cursorColor = Color(0xFF2E5CFF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Show available approvers count
                    if (!isLoadingUsers && availableApprovers.isNotEmpty()) {
                        Text(
                            "${availableApprovers.size} approvers available",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                    
                    // Show no results message
                    if (approverSearchQuery.isNotEmpty() && searchApprovers(approverSearchQuery).isEmpty() && !isLoadingUsers) {
                        Text(
                            "No approvers found matching \"$approverSearchQuery\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                    
                    // Approver Dropdown
                    if (showApproverDropdown && approverSearchQuery.isNotEmpty()) {
                        val filteredApprovers = searchApprovers(approverSearchQuery)
                        
                        if (filteredApprovers.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        "Select Approver",
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF2E5CFF),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                    filteredApprovers.forEach { user ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedApprover = user
                                                    approverSearchQuery = ""
                                                    showApproverDropdown = false
                                                }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Person, 
                                                contentDescription = null, 
                                                tint = Color(0xFF2E5CFF),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    user.name ?: "Unknown",
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color.Black
                                                )
                                                Text(
                                                    user.phone,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                        if (user != filteredApprovers.last()) {
                                            Divider(color = Color.Gray.copy(alpha = 0.3f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Team Members Selection
                Text("Team Members (Users) *", fontWeight = FontWeight.Medium, color = Color(0xFF2E5CFF))
                Spacer(Modifier.height(8.dp))
                
                // Selected Team Members Display
                if (selectedTeamMembers.isNotEmpty()) {
                    selectedTeamMembers.forEach { member ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF2196F3))
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            member.name ?: "Unknown",
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            member.phone,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                IconButton(onClick = { 
                                    selectedTeamMembers = selectedTeamMembers.filter { it != member }
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove")
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
                
                // Team Member Search
                OutlinedTextField(
                    value = memberSearchQuery,
                    onValueChange = { query ->
                        memberSearchQuery = query
                        // Show dropdown immediately when typing starts and we have users
                        showMemberDropdown = query.isNotEmpty() && availableUsers.isNotEmpty()
                    },
                    label = { Text("Search Team Member by name or phone") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF2E5CFF))
                    },
                    trailingIcon = {
                        Row {
                            if (memberSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { 
                                    memberSearchQuery = ""
                                    showMemberDropdown = false
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                            if (isLoadingUsers) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF2E5CFF)
                                )
                            }
                        }
                    },
                    placeholder = { 
                        Text(
                            if (isLoadingUsers) "Loading team members..." 
                            else "Type name or phone number...",
                            color = Color.Gray
                        )
                    },
                    enabled = !isLoadingUsers,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E5CFF),
                        focusedLabelColor = Color(0xFF2E5CFF),
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        cursorColor = Color(0xFF2E5CFF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Show available users count
                if (!isLoadingUsers && availableUsers.isNotEmpty()) {
                    val availableCount = availableUsers.size - selectedTeamMembers.size
                    Text(
                        "$availableCount team members available",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
                
                // Show no results message
                if (memberSearchQuery.isNotEmpty() && searchUsers(memberSearchQuery).isEmpty() && !isLoadingUsers) {
                    Text(
                        "No team members found matching \"$memberSearchQuery\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
                
                // Team Member Dropdown
                if (showMemberDropdown && memberSearchQuery.isNotEmpty()) {
                    val filteredUsers = searchUsers(memberSearchQuery)
                    
                    if (filteredUsers.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    "Select Team Members",
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF2E5CFF),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                                filteredUsers.forEach { user ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedTeamMembers = selectedTeamMembers + user
                                                memberSearchQuery = ""
                                                showMemberDropdown = false
                                            }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Person, 
                                            contentDescription = null, 
                                            tint = Color(0xFF2196F3),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                user.name ?: "Unknown",
                                                fontWeight = FontWeight.Medium,
                                                color = Color.Black
                                            )
                                            Text(
                                                user.phone,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    if (user != filteredUsers.last()) {
                                        Divider(color = Color.Gray.copy(alpha = 0.3f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Departments
            item {
                SectionHeader("🏢 DEPARTMENTS")
                OutlinedTextField(
                    value = totalBudget,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) totalBudget = newValue
                    },
                    label = { Text("Total Budget *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Department selection: allow both dropdown and manual entry
                    var expanded by remember { mutableStateOf(false) }
                    var manualEntryMode by remember { mutableStateOf(false) }
                    if (!manualEntryMode) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedDepartment.ifBlank { "Select Department" },
                                onValueChange = {},
                                label = { Text("Department") },
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    Row {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                        IconButton(onClick = { manualEntryMode = true }) {
                                            Icon(Icons.Default.Add, contentDescription = "Type Department")
                                        }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2E5CFF),
                                    focusedLabelColor = Color(0xFF2E5CFF)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                defaultDepartments.filter { dept ->
                                    dept !in departmentBudgets.keys && 
                                    dept !in departmentInputBoxes.map { it.departmentName }
                                }.forEach { dept ->
                                    DropdownMenuItem(
                                        text = { Text(dept) },
                                        onClick = {
                                            selectedDepartment = dept
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = selectedDepartment,
                            onValueChange = { selectedDepartment = it },
                            label = { Text("Department Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { manualEntryMode = false; selectedDepartment = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Cancel Manual Entry")
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2E5CFF),
                                focusedLabelColor = Color(0xFF2E5CFF)
                            )
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    // Budget input
                    OutlinedTextField(
                        value = departmentBudgetInput,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) departmentBudgetInput = newValue
                        },
                        label = { Text("Budget") },
                        modifier = Modifier.width(120.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(Modifier.width(8.dp))
                    // Add button
                    IconButton(
                        onClick = {
                            if (selectedDepartment.isNotBlank() && departmentBudgetInput.isNotBlank()) {
                                val budgetVal = departmentBudgetInput.toDoubleOrNull() ?: 0.0
                                val allocated = departmentBudgets.values.sumOf { it.toDoubleOrNull() ?: 0.0 } + 
                                               departmentInputBoxes.sumOf { it.budget.toDoubleOrNull() ?: 0.0 }
                                val total = totalBudget.toDoubleOrNull() ?: 0.0
                                if (allocated + budgetVal <= total) {
                                    departmentBudgets[selectedDepartment] = departmentBudgetInput
                                    selectedDepartment = ""
                                    departmentBudgetInput = ""
                                    manualEntryMode = false
                                }
                            } else {
                                val newId = (departmentInputBoxes.maxOfOrNull { it.id } ?: 0) + 1
                                departmentInputBoxes = departmentInputBoxes + DepartmentInputBox(id = newId)
                            }
                        },
                        enabled = true
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Department")
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Dynamic department input boxes
                departmentInputBoxes.forEach { inputBox ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        // Department name input
                        OutlinedTextField(
                            value = inputBox.departmentName,
                            onValueChange = { newValue ->
                                departmentInputBoxes = departmentInputBoxes.map { box ->
                                    if (box.id == inputBox.id) box.copy(departmentName = newValue) else box
                                }
                            },
                            label = { Text("Department Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2E5CFF),
                                focusedLabelColor = Color(0xFF2E5CFF)
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        // Budget input
                        OutlinedTextField(
                            value = inputBox.budget,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() }) {
                                    departmentInputBoxes = departmentInputBoxes.map { box ->
                                        if (box.id == inputBox.id) box.copy(budget = newValue) else box
                                    }
                                }
                            },
                            label = { Text("Budget") },
                            modifier = Modifier.width(120.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2E5CFF),
                                focusedLabelColor = Color(0xFF2E5CFF)
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        // Remove button
                        IconButton(
                            onClick = {
                                departmentInputBoxes = departmentInputBoxes.filter { it.id != inputBox.id }
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove")
                        }
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                // List of added departments (from predefined list)
                if (departmentBudgets.isNotEmpty()) {
                    departmentBudgets.keys.toList().forEach { dept ->
                        val budget = departmentBudgets[dept] ?: ""
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Text(dept, modifier = Modifier.weight(1f))
                            Text("₹$budget", modifier = Modifier.padding(end = 8.dp))
                            IconButton(onClick = {
                                departmentBudgets = departmentBudgets.toMutableMap().apply { remove(dept) }
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove")
                            }
                        }
                    }
                }
                // List of added departments (from custom input boxes)
                departmentInputBoxes.filter { it.departmentName.isNotBlank() && it.budget.isNotBlank() }.forEach { inputBox ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text(inputBox.departmentName, modifier = Modifier.weight(1f))
                        Text("₹${inputBox.budget}", modifier = Modifier.padding(end = 8.dp))
                        IconButton(onClick = {
                            departmentInputBoxes = departmentInputBoxes.filter { it.id != inputBox.id }
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove")
                        }
                    }
                }
                
                // Allocated/Total calculation including dynamic boxes
                val predefinedAllocated = departmentBudgets.values.sumOf { it.toDoubleOrNull() ?: 0.0 }
                val dynamicAllocated = departmentInputBoxes.filter { 
                    it.departmentName.isNotBlank() && it.budget.isNotBlank() 
                }.sumOf { it.budget.toDoubleOrNull() ?: 0.0 }
                val totalAllocated = predefinedAllocated + dynamicAllocated
                
                Text(
                    "Allocated: ₹${String.format("%,.0f", totalAllocated)} / ₹${totalBudget.ifBlank { "0" }}", 
                    color = if ((totalBudget.toDoubleOrNull() ?: 0.0) < totalAllocated) Color.Red else Color(0xFF2E5CFF), 
                    fontWeight = FontWeight.Bold
                )
                if ((totalBudget.toDoubleOrNull() ?: 0.0) < totalAllocated) {
                    Text("Department budgets exceed total budget!", color = Color.Red)
                }
            }
            
            // Submit Button
            item {
                Spacer(Modifier.height(16.dp))
                
                val isFormValid = projectName.isNotBlank() && 
                                startDate != null && 
                                selectedApprover != null && 
                                selectedTeamMembers.isNotEmpty()
                
                Button(
                    onClick = {
                        if (isFormValid) {
                            isSubmitting = true
                            scope.launch {
                                try {
                                    val projectRepo = ProjectRepository()
                                    // Combine predefined departments and dynamic departments
                                    val allDepartmentAllocations = departmentBudgets.mapValues { it.value.toDoubleOrNull() ?: 0.0 }.toMutableMap()
                                    
                                    // Add dynamic departments
                                    departmentInputBoxes.filter { 
                                        it.departmentName.isNotBlank() && it.budget.isNotBlank() 
                                    }.forEach { inputBox ->
                                        allDepartmentAllocations[inputBox.departmentName] = inputBox.budget.toDoubleOrNull() ?: 0.0
                                    }
                                    
                                    val project = Project(
                                        name = projectName,
                                        description = projectDescription,
                                        budget = totalBudget.toDoubleOrNull() ?: 0.0,
                                        startDate = startDate?.let { dateFormatter.format(it) } ?: "",
                                        endDate = endDate?.let { dateFormatter.format(it) } ?: "",
                                        status = ProjectStatus.ACTIVE,
                                        managerId = selectedApprover!!.phone,
                                        teamMembers = selectedTeamMembers.map { it.phone },
                                        departmentAllocations = allDepartmentAllocations
                                    )
                                    val result = projectRepo.createProject(project)
                                    result.fold(
                                        onSuccess = {
                                            onProjectCreated()
                                        },
                                        onFailure = { e ->
                                            error = "Failed to create project: ${e.message}"
                                            isSubmitting = false
                                        }
                                    )
                                } catch (e: Exception) {
                                    error = "Failed to create project: ${e.message}"
                                    isSubmitting = false
                                }
                            }
                        }
                    },
                    enabled = !isSubmitting && isFormValid,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5CFF))
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
                        if (isSubmitting) "Creating Project..." else "Create Project",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        it, 
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2E5CFF),
        fontSize = 16.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
} 