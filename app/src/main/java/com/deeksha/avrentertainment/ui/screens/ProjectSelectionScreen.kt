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