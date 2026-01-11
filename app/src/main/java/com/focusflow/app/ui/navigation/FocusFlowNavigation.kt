package com.focusflow.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.focusflow.app.data.repository.FocusFlowRepository
import com.focusflow.app.ui.screens.*
import com.focusflow.app.ui.viewmodel.FocusViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Focus : Screen("focus")
    object Island : Screen("island")
    object Shop : Screen("shop")
    object Stats : Screen("stats")
}

@Composable
fun FocusFlowNavigation(
    viewModel: FocusViewModel,
    repository: FocusFlowRepository
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToFocus = { navController.navigate(Screen.Focus.route) },
                onNavigateToIsland = { navController.navigate(Screen.Island.route) },
                onNavigateToShop = { navController.navigate(Screen.Shop.route) },
                onNavigateToStats = { navController.navigate(Screen.Stats.route) }
            )
        }

        composable(Screen.Focus.route) {
            FocusScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Island.route) {
            IslandScreen(
                repository = repository,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Shop.route) {
            ShopScreen(
                repository = repository,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}