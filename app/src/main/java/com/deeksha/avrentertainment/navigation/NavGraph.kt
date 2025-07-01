package com.deeksha.avrentertainment.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.deeksha.avrentertainment.ui.screens.*
import com.deeksha.avrentertainment.viewmodels.ProjectSelectionViewModel

sealed class Screen(val route: String) {
    object ProductionHeadLogin : Screen("production_head_login")
    object ProjectSelection : Screen("project_selection")
    object CreateUser : Screen("create_user")
    // ... other existing routes
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
                    // Navigate to project details
                    navController.navigate("project_details/$projectId")
                },
                onCreateNewProject = {
                    // Navigate to create new project screen
                    navController.navigate("create_project")
                }
            )
        }

        composable("create_project") {
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

        // ... other existing routes
    }
} 