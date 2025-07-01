package com.deeksha.avrentertainment.repository

import com.deeksha.avrentertainment.models.Project
import com.deeksha.avrentertainment.models.ProjectStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

class ProjectRepository {
    private val db = FirebaseFirestore.getInstance()
    private val projectsCollection = db.collection("projects")

    // Get all projects
    suspend fun getAllProjects(): Result<List<Project>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = projectsCollection.get().await()
            val projects = snapshot.documents.mapNotNull { document ->
                document.toObject(Project::class.java)?.copy(id = document.id)
            }
            Result.success(projects)
        } catch (e: Exception) {
            android.util.Log.e("ProjectRepository", "Failed to get all projects: ${e.message}")
            Result.failure(e)
        }
    }

    // Get project by ID
    suspend fun getProjectById(projectId: String): Result<Project> = withContext(Dispatchers.IO) {
        try {
            val document = projectsCollection.document(projectId).get().await()
            if (document.exists()) {
                val project = document.toObject(Project::class.java)?.copy(id = document.id)
                if (project != null) {
                    Result.success(project)
                } else {
                    Result.failure(Exception("Failed to parse project data"))
                }
            } else {
                Result.failure(Exception("Project not found"))
            }
        } catch (e: Exception) {
            android.util.Log.e("ProjectRepository", "Failed to get project by ID: ${e.message}")
            Result.failure(e)
        }
    }

    // Create new project
    suspend fun createProject(project: Project): Result<Project> = withContext(Dispatchers.IO) {
        try {
            val docRef = projectsCollection.document()
            val projectWithId = project.copy(
                id = docRef.id,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            docRef.set(projectWithId).await()
            android.util.Log.d("ProjectRepository", "✅ Created new project: ${project.name}")
            Result.success(projectWithId)
        } catch (e: Exception) {
            android.util.Log.e("ProjectRepository", "Failed to create project: ${e.message}")
            Result.failure(e)
        }
    }

    // DYNAMIC PROJECT MANAGEMENT - Enable/Disable projects from backend
    suspend fun updateProjectStatus(projectId: String, status: ProjectStatus): Result<Unit> =
        withContext(Dispatchers.IO) {
        try {
                android.util.Log.d(
                    "ProjectManagement",
                    "🔄 Updating project $projectId status to $status"
                )
            
            projectsCollection.document(projectId)
                .update("status", status.name)
                .await()
            
                android.util.Log.d(
                    "ProjectManagement",
                    "✅ Project $projectId status updated to $status"
                )
            Result.success(Unit)
        } catch (e: Exception) {
                android.util.Log.e(
                    "ProjectManagement",
                    "❌ Failed to update project status: ${e.message}"
                )
            Result.failure(e)
        }
    }
    
    // Get only active projects for expense creation
    suspend fun getActiveProjects(): Result<List<Project>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ProjectManagement", "🔍 Loading only ACTIVE projects...")
            
            val snapshot = projectsCollection
                .whereEqualTo("status", ProjectStatus.ACTIVE.name)
                .get()
                .await()
            
            val activeProjects = snapshot.documents.mapNotNull { document ->
                document.toObject(Project::class.java)?.copy(id = document.id)
            }
            
            android.util.Log.d(
                "ProjectManagement",
                "✅ Found ${activeProjects.size} active projects"
            )
            activeProjects.forEach { project ->
                android.util.Log.d(
                    "ProjectManagement",
                    "   📋 Active: ${project.name} (₹${String.format("%.0f", project.budget)})"
                )
            }
            
            Result.success(activeProjects)
        } catch (e: Exception) {
            android.util.Log.e(
                "ProjectManagement",
                "❌ Failed to load active projects: ${e.message}"
            )
            Result.failure(e)
        }
    }
    
    // Bulk update project statuses (for admin use)
    suspend fun bulkUpdateProjectStatuses(updates: Map<String, ProjectStatus>): Result<Unit> =
        withContext(Dispatchers.IO) {
        try {
                android.util.Log.d(
                    "ProjectManagement",
                    "🔄 Bulk updating ${updates.size} project statuses..."
                )
            
            updates.forEach { (projectId, status) ->
                try {
                    projectsCollection.document(projectId)
                        .update("status", status.name)
                        .await()
                    android.util.Log.d("ProjectManagement", "✅ Updated $projectId to $status")
                } catch (e: Exception) {
                        android.util.Log.e(
                            "ProjectManagement",
                            "❌ Failed to update $projectId: ${e.message}"
                        )
                    }
            }
            
            android.util.Log.d("ProjectManagement", "✅ Bulk update completed")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("ProjectManagement", "❌ Bulk update failed: ${e.message}")
            Result.failure(e)
        }
    }
    
    // Create admin panel method to manage projects dynamically
    suspend fun setProjectStatusFromBackend(projectName: String, isActive: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
        try {
            val status = if (isActive) ProjectStatus.ACTIVE else ProjectStatus.INACTIVE
                android.util.Log.d(
                    "ProjectManagement",
                    "🎛️ Backend request: Setting '$projectName' to $status"
                )
            
            // Find project by name and update its status
            val snapshot = projectsCollection
                .whereEqualTo("name", projectName)
                .get()
                .await()
            
            if (snapshot.documents.isEmpty()) {
                android.util.Log.e("ProjectManagement", "❌ Project '$projectName' not found")
                return@withContext Result.failure(Exception("Project '$projectName' not found"))
            }
            
            snapshot.documents.forEach { document ->
                document.reference.update("status", status.name).await()
                    android.util.Log.d(
                        "ProjectManagement",
                        "✅ Updated '$projectName' status to $status"
                    )
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
                android.util.Log.e(
                    "ProjectManagement",
                    "❌ Failed to set project status from backend: ${e.message}"
                )
            Result.failure(e)
        }
    }

    // Create default projects with proper status management
    suspend fun createDefaultProjects(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d(
                "ProjectRepository",
                "🎬 Creating default projects with dynamic status management..."
            )
            
            val defaultProjects = listOf(
                Project(
                    name = "Movie Production A",
                    description = "Main feature film production",
                    budget = 5000000.0,
                    status = ProjectStatus.ACTIVE, // ACTIVE by default
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                ),
                Project(
                    name = "Documentary Project B",
                    description = "Documentary production",
                    budget = 2000000.0,
                    status = ProjectStatus.ACTIVE, // ACTIVE by default
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                ),
                Project(
                    name = "Commercial Project C", 
                    description = "Commercial advertisement production",
                    budget = 1500000.0,
                    status = ProjectStatus.INACTIVE, // INACTIVE - can be enabled from backend
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                ),
                Project(
                    name = "Web Series D",
                    description = "Web series production",
                    budget = 3000000.0,
                    status = ProjectStatus.INACTIVE, // INACTIVE - can be enabled from backend
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )
            )
            
            // Only create projects that don't already exist
            defaultProjects.forEach { project ->
                val existingSnapshot = projectsCollection
                    .whereEqualTo("name", project.name)
                    .get()
                    .await()
                
                if (existingSnapshot.documents.isEmpty()) {
                    val docRef = projectsCollection.document()
                    val projectWithId = project.copy(id = docRef.id)
                    docRef.set(projectWithId).await()
                    android.util.Log.d(
                        "ProjectRepository",
                        "✅ Created project: ${project.name} (${project.status})"
                    )
                } else {
                    android.util.Log.d(
                        "ProjectRepository",
                        "⏭️ Project already exists: ${project.name}"
                    )
                }
            }
            
            android.util.Log.d(
                "ProjectRepository",
                "🎯 Default projects setup complete with dynamic status management"
            )
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e(
                "ProjectRepository",
                "❌ Failed to create default projects: ${e.message}"
            )
            Result.failure(e)
        }
    }

    // Migrate existing projects to have proper status
    suspend fun migrateProjectTimestamps(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d(
                "ProjectRepository",
                "🔄 Migrating project timestamps and ensuring status field..."
            )
            
            val snapshot = projectsCollection.get().await()
            var migratedCount = 0
            
            snapshot.documents.forEach { document ->
                val updates = mutableMapOf<String, Any>()
                val data = document.data
                
                // Ensure status field exists (default to ACTIVE for existing projects)
                if (!data?.containsKey("status")!!) {
                    updates["status"] = ProjectStatus.ACTIVE.name
                    android.util.Log.d(
                        "ProjectRepository",
                        "Adding status field to project ${document.id}"
                    )
                }
                
                // Update timestamps if they're Long instead of Timestamp
                data["createdAt"]?.let { createdAt ->
                    if (createdAt is Long) {
                        updates["createdAt"] = Timestamp(createdAt / 1000, 0)
                    }
                }
                
                data["updatedAt"]?.let { updatedAt ->
                    if (updatedAt is Long) {
                        updates["updatedAt"] = Timestamp(updatedAt / 1000, 0)
                    }
                }
                
                if (updates.isNotEmpty()) {
                    document.reference.update(updates).await()
                    migratedCount++
                }
            }
            
            android.util.Log.d(
                "ProjectRepository",
                "✅ Migration complete. Updated $migratedCount projects"
            )
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("ProjectRepository", "❌ Migration failed: ${e.message}")
            Result.failure(e)
        }
    }
} 