package com.deeksha.avrentertainment.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.deeksha.avrentertainment.ui.screens.*
import com.deeksha.avrentertainment.viewmodels.ProjectSelectionViewModel
import com.deeksha.avrentertainment.NewReportsScreen
import com.deeksha.avrentertainment.repository.ExpenseRepository
import com.deeksha.avrentertainment.repository.ProjectRepository

sealed class Screen(val route: String) {
    object ProductionHeadLogin : Screen("production_head_login")
    object ProjectSelection : Screen("project_selection")
    object CreateUser : Screen("create_user")
    object ProjectDetails : Screen("project_details/{projectId}")
    object CreateProject : Screen("create_project")
    object AllProjectsReports : Screen("all_projects_reports")
    
    companion object {
        // Helper function to create route with projectId
        fun createProjectDetailsRoute(projectId: String) = "project_details/$projectId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.ProductionHeadLogin.route
    ) {
        composable(Screen.ProductionHeadLogin.route) {
            ProductionHeadLoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate(Screen.ProjectSelection.route) {
                        popUpTo(Screen.ProductionHeadLogin.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProjectSelection.route) {
            val viewModel: ProjectSelectionViewModel = viewModel()
            val projects by viewModel.projects.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val error by viewModel.error.collectAsState()

            ProjectSelectionScreen(
                navController = navController,
                projects = projects,
                isLoading = isLoading,
                error = error,
                onProjectSelected = { projectId ->
                    // Navigate to project details with project-specific notifications
                    navController.navigate(Screen.createProjectDetailsRoute(projectId))
                },
                onCreateNewProject = {
                    // Navigate to create new project screen
                    navController.navigate(Screen.CreateProject.route)
                }
            )
        }

        composable(
            route = Screen.ProjectDetails.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            
            ProjectDetailsWithNotificationsScreen(
                navController = navController,
                projectId = projectId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CreateProject.route) {
            CreateProjectScreen(
                navController = navController,
                onProjectCreated = {
                    navController.popBackStack(Screen.ProjectSelection.route, false)
                }
            )
        }

        composable(Screen.CreateUser.route) {
            CreateUserScreen(
                navController = navController,
                onUserCreated = {
                    navController.popBackStack()
                }
            )
        }

        // Production Head Home Screen with project ID
        composable(
            route = "production_head_home/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            
            ProductionHeadHomeScreen(
                navController = navController,
                projectId = projectId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // All Projects Reports Screen
        composable(Screen.AllProjectsReports.route) {
            NewReportsScreen(
                navController = navController,
                projectRepository = ProjectRepository(),
                expenseRepository = ExpenseRepository(),
                projectId = "ALL_PROJECTS", // Special identifier for all projects
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ... other existing routes
    }
} 