package com.deeksha.avrentertainment.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import com.deeksha.avrentertainment.models.User
import com.deeksha.avrentertainment.models.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    navController: NavHostController,
    onUserCreated: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Form state
    var phoneNumber by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.USER) }
    var isRoleDropdownExpanded by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showValidationError by remember { mutableStateOf(false) }
    
    // Form validation
    val isFormValid = phoneNumber.isNotBlank() && fullName.isNotBlank() && phoneNumber.length >= 10
    
    // Role descriptions
    val roleDescriptions = mapOf(
        UserRole.USER to "Can submit expenses and view project details",
        UserRole.APPROVER to "Can approve/reject expenses and manage budgets"
    )
    
    fun createUser() {
        if (!isFormValid) {
            showValidationError = true
            return
        }
        
        scope.launch {
            try {
                isSubmitting = true
                error = null
                
                val firestore = FirebaseFirestore.getInstance()
                
                // Check if user already exists
                val existingUser = firestore.collection("users")
                    .document(phoneNumber)
                    .get()
                    .await()
                
                if (existingUser.exists()) {
                    error = "User with this phone number already exists"
                    isSubmitting = false
                    return@launch
                }
                
                // Create user document
                val userData = hashMapOf(
                    "name" to fullName,
                    "role" to selectedRole.name,
                    "createdAt" to Timestamp.now(),
                    "createdBy" to "PRODUCTION_HEAD"
                )
                
                firestore.collection("users")
                    .document(phoneNumber)
                    .set(userData)
                    .await()
                
                // Show success message
                Toast.makeText(context, "User created successfully!", Toast.LENGTH_SHORT).show()
                
                // Call the callback to trigger navigation in MainActivity
                onUserCreated()
                
            } catch (e: Exception) {
                error = "Failed to create user: ${e.message}"
                isSubmitting = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Create User",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { createUser() },
                        enabled = !isSubmitting
                    ) {
                        Text(
                            "Create",
                            color = if (isFormValid && !isSubmitting) Color(0xFF007AFF) else Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .background(Color(0xFFF5F5F5))
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // USER INFORMATION Section
            Text(
                text = "USER INFORMATION",
                fontSize = 13.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Phone Number Field
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { 
                    if (it.all { char -> char.isDigit() } && it.length <= 10) {
                        phoneNumber = it
                        showValidationError = false
                    }
                },
                label = { Text("Phone Number") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        tint = Color(0xFF007AFF)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF007AFF),
                    focusedLabelColor = Color(0xFF007AFF),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                isError = showValidationError && phoneNumber.length < 10
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Full Name Field
            OutlinedTextField(
                value = fullName,
                onValueChange = { 
                    fullName = it
                    showValidationError = false
                },
                label = { Text("Full Name") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF007AFF)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF007AFF),
                    focusedLabelColor = Color(0xFF007AFF),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                isError = showValidationError && fullName.isBlank()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Role Dropdown
            ExposedDropdownMenuBox(
                expanded = isRoleDropdownExpanded,
                onExpandedChange = { isRoleDropdownExpanded = !isRoleDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedRole.name.lowercase().replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Role") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF007AFF)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFF007AFF)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF007AFF),
                        focusedLabelColor = Color(0xFF007AFF),
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
                
                ExposedDropdownMenu(
                    expanded = isRoleDropdownExpanded,
                    onDismissRequest = { isRoleDropdownExpanded = false }
                ) {
                    listOf(UserRole.USER, UserRole.APPROVER).forEach { role ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        role.name.lowercase().replaceFirstChar { it.uppercase() },
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        roleDescriptions[role] ?: "",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            },
                            onClick = {
                                selectedRole = role
                                isRoleDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            
            if (error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
} 